package info.unclewang.proxy.jdk;

import info.unclewang.proxy.AbstractRpcHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author unclewang
 * @date 2019-07-22 20:45
 */
@Slf4j
public class RpcInvocationHandler<T> extends AbstractRpcHandler<T> implements InvocationHandler {
	public RpcInvocationHandler(Class<T> clz) {
		this.clz = clz;
		this.updateDiscoverInetSocketAddress();
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return this.invokeSwitch(method, args);
	}
}
