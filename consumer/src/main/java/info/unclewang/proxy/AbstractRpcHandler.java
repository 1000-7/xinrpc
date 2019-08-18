package info.unclewang.proxy;

import info.unclewang.client.NettyClient;
import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;
import info.unclewang.future.FutureHolder;
import info.unclewang.future.XinRpcFuture;
import info.unclewang.registry.Register;
import info.unclewang.registry.impl.EtcdRegister;
import info.unclewang.registry.impl.ZookeeperRegister;
import info.unclewang.util.NettyProperties;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author unclewang
 * @date 2019-07-28 18:53
 */
@Slf4j
public abstract class AbstractRpcHandler<T> {
	protected Class<T> clz;
	protected List<InetSocketAddress> discoverInetSocketAddress = null;
	protected Register register;

	protected Object invokeSwitch(Method method, Object[] args) {
		if (NettyProperties.sync) {
			return this.handleRequestAndSend(method, args);
		} else {
			return this.handleRequestAndSendWithFuture(method, args);
		}
	}

	protected Object handleRequestAndSendWithFuture(Method method, Object[] args) {
		RpcRequest request = this.getRequest(method, args);
		InetSocketAddress randomAddress = this.getInetSocketAddress();
		NettyClient nettyClient = new NettyClient(randomAddress.getAddress().getHostAddress(), randomAddress.getPort());
		ChannelFuture channelFuture = nettyClient.connect(randomAddress);
		XinRpcFuture xinRpcFuture = new XinRpcFuture(channelFuture.channel().eventLoop());
		if (xinRpcFuture.isSuccess()) {
			sendRequest(request, xinRpcFuture, channelFuture);
		} else {
			channelFuture.addListener((ChannelFutureListener) future -> {
				if (future.isSuccess()) {
					sendRequest(request, xinRpcFuture, future);
				} else {
					log.error("send request error ", future.cause());
				}
			});
		}
		try {
			RpcResponse rpcResponse = xinRpcFuture.get(5, TimeUnit.SECONDS);
			if (rpcResponse.getSuccess()) {
				return rpcResponse.getResult();
			} else {
				throw rpcResponse.getThrowable();
			}
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		} catch (Throwable throwable) {
			log.info("result is  a exception");
			throwable.printStackTrace();
		}
		return null;
	}

	private void sendRequest(RpcRequest request, XinRpcFuture xinRpcFuture, ChannelFuture channelFuture) {
		channelFuture.channel().writeAndFlush(request).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					FutureHolder.registerFuture(request.getId(), xinRpcFuture);
					log.info("send request success");
				} else {
					xinRpcFuture.tryFailure(future.cause());
					FutureHolder.getAndRemoveFuture(request.getId());
					log.error("send request failed", future.cause());
				}
			}
		});
	}


	protected Object handleRequestAndSend(Method method, Object[] args) {
		RpcRequest request = this.getRequest(method, args);
		InetSocketAddress randomAddress = this.getInetSocketAddress();
		NettyClient nettyClient = new NettyClient(randomAddress.getAddress().getHostAddress(), randomAddress.getPort());
		nettyClient.connect(nettyClient.getInetSocketAddress());
		return nettyClient.send(request).getResult();
	}


	/**
	 * 每个consumer都需要5s一次获取最新的内容，应该缓存起来，不应该在这做
	 */
	protected void updateDiscoverInetSocketAddress() {
		log.info("updateDiscoverInetSocketAddress init");
		ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2, r -> {
			Thread t = new Thread(r);
			t.setName("info.unclewang.proxy.RpcInvocationHandler.updateDiscoverInetSocketAddress");
			t.setDaemon(true);
			return t;
		});

		scheduledExecutorService.scheduleAtFixedRate(() -> {
			this.getRegister();
			log.info("updateDiscoverInetSocketAddress success, {}", new Date(System.currentTimeMillis()).toString());
		}, 0, 60, TimeUnit.SECONDS);
	}

	private void getRegister() {
		if (this.register == null) {
			if (NettyProperties.useEtcd) {
				this.register = new EtcdRegister();
			} else {
				this.register = new ZookeeperRegister();
			}
			this.register.init();
		}
		this.discoverInetSocketAddress = this.register.discover(this.clz.getName());
	}

	private RpcRequest getRequest(Method method, Object[] args) {
		RpcRequest request = new RpcRequest();

		String requestId = UUID.randomUUID().toString();

		String className = method.getDeclaringClass().getName();
		String methodName = method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();

		request.setId(requestId);
		request.setClassName(className);
		request.setMethodName(methodName);
		request.setParameterTypes(parameterTypes);
		request.setParameters(args);
		return request;
	}


	private InetSocketAddress getInetSocketAddress() {
		if (this.discoverInetSocketAddress == null) {
			this.getRegister();
			if (this.discoverInetSocketAddress.size() == 0) {
				log.error("No available service. Service name:{}", this.clz.getName());
				System.exit(0);
			}
		}
		//负载均衡 随机抽取实现 默认机器都能使用
		InetSocketAddress randomAddress = this.discoverInetSocketAddress.get(new Random().nextInt(this.discoverInetSocketAddress.size()));
		log.info("server provider address:{}", randomAddress.toString());
		return randomAddress;
	}

}
