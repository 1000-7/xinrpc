package info.unclewang.proxy;

import info.unclewang.client.NettyClient;
import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;
import info.unclewang.etcd.EtcdRegister;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author unclewang
 * @date 2019-07-22 20:45
 */
@Slf4j
public class RpcInvocationHandler<T> implements InvocationHandler {
	private Class<T> clz;
	private static List<InetSocketAddress> discoverInetSocketAddress = null;

	public RpcInvocationHandler(Class<T> clz) {
		this.clz = clz;
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
		return send(request).getResult();
	}

	private RpcResponse send(RpcRequest request) {

		if (discoverInetSocketAddress == null) {
			EtcdRegister register = new EtcdRegister();
			register.initEtcd();
			discoverInetSocketAddress = register.discover(this.clz.getName());
			if (discoverInetSocketAddress.size() == 0) {
				log.error("No available service. Service name:{}", this.clz.getName());
				System.exit(0);
			}
		}
		//负载均衡 随机抽取实现 默认机器都能使用
		InetSocketAddress randomAddress = discoverInetSocketAddress.get(new Random().nextInt(discoverInetSocketAddress.size()));
		log.info("server provider address:{}", randomAddress.toString());
		NettyClient nettyClient = new NettyClient(randomAddress.getAddress().getHostAddress(), randomAddress.getPort());
		nettyClient.connect(nettyClient.getInetSocketAddress());
		return nettyClient.send(request);

	}
}
