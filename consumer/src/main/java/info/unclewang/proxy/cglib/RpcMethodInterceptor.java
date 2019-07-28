package info.unclewang.proxy.cglib;

import info.unclewang.proxy.AbstractRpcHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author unclewang
 * @date 2019-07-28 18:44
 */

@Slf4j
public class RpcMethodInterceptor<T> extends AbstractRpcHandler<T> implements MethodInterceptor {

	public RpcMethodInterceptor(Class<T> clz) {
		this.clz = clz;
		this.updateDiscoverInetSocketAddress();
	}

	@Override
	public Object intercept(Object o, Method method, Object[] parameters, MethodProxy methodProxy) throws Throwable {
		return this.handleRequestAndSend(method, parameters);
	}
}
