package info.unclewang.proxy;

import info.unclewang.client.NettyClient;
import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author unclewang
 * @date 2019-07-22 20:45
 */
public class RpcProxy<T> implements InvocationHandler {
	private Class<T> clz;

	public RpcProxy(Class<T> clz) {
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
		NettyClient nettyClient = new NettyClient("127.0.0.1", 8000);
		nettyClient.connect(nettyClient.getInetSocketAddress());
		return nettyClient.send(request);

	}
}
