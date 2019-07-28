package info.unclewang.proxy;

import info.unclewang.client.NettyClient;
import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;
import info.unclewang.etcd.EtcdRegister;
import info.unclewang.util.RpcUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.ArrayList;
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
	private static List<InetSocketAddress> discoverInetSocketAddress = new ArrayList<>();

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

	private static RpcResponse send(RpcRequest request) {

		if (discoverInetSocketAddress.size() == 0) {
			EtcdRegister register = new EtcdRegister();
			register.initEtcd();
			discoverInetSocketAddress = register.discover(RpcUtils.RPC_SERVER_NAME);
		}
		//负载均衡 随机抽取实现 默认机器都能使用
		InetSocketAddress random = discoverInetSocketAddress.get(new Random().nextInt(discoverInetSocketAddress.size()));
		log.info("server provider address:{}", random.toString());
		NettyClient nettyClient = new NettyClient(random.getAddress().getHostAddress(), random.getPort());
		nettyClient.connect(nettyClient.getInetSocketAddress());
		return nettyClient.send(request);

	}
}
