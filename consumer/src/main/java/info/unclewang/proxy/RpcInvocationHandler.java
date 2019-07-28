package info.unclewang.proxy;

import info.unclewang.client.NettyClient;
import info.unclewang.entity.RpcRequest;
import info.unclewang.etcd.EtcdRegister;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author unclewang
 * @date 2019-07-22 20:45
 */
@Slf4j
public class RpcInvocationHandler<T> implements InvocationHandler {
	private Class<T> clz;
	private List<InetSocketAddress> discoverInetSocketAddress = null;
	private EtcdRegister register;

	public RpcInvocationHandler(Class<T> clz) {
		this.clz = clz;
		updateDiscoverInetSocketAddress();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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
		if (this.discoverInetSocketAddress == null) {
			if (this.register == null) {
				this.register = new EtcdRegister();
				this.register.initEtcd();
			}
			this.discoverInetSocketAddress = this.register.discover(this.clz.getName());
			if (this.discoverInetSocketAddress.size() == 0) {
				log.error("No available service. Service name:{}", this.clz.getName());
				System.exit(0);
			}
		}
		//负载均衡 随机抽取实现 默认机器都能使用
		InetSocketAddress randomAddress = this.discoverInetSocketAddress.get(new Random().nextInt(this.discoverInetSocketAddress.size()));
		log.info("server provider address:{}", randomAddress.toString());
		NettyClient nettyClient = new NettyClient(randomAddress.getAddress().getHostAddress(), randomAddress.getPort());
		nettyClient.connect(nettyClient.getInetSocketAddress());
		return nettyClient.send(request).getResult();
	}

	public void updateDiscoverInetSocketAddress() {
		log.info("updateDiscoverInetSocketAddress init");
		ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(2, r -> {
			Thread t = new Thread(r);
			t.setName("info.unclewang.proxy.RpcInvocationHandler.updateDiscoverInetSocketAddress");
			t.setDaemon(true);
			return t;
		});

		scheduledExecutorService.scheduleAtFixedRate(() -> {
			if (this.register == null) {
				this.register = new EtcdRegister();
				this.register.initEtcd();
			}
			this.discoverInetSocketAddress = this.register.discover(this.clz.getName());
			log.info("updateDiscoverInetSocketAddress success, {}", new Date(System.currentTimeMillis()).toString());
		}, 0, 3 * 6, TimeUnit.SECONDS);


	}

}
