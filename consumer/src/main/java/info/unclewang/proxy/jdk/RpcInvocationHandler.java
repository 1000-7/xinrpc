package info.unclewang.proxy.jdk;

import info.unclewang.etcd.EtcdRegister;
import info.unclewang.proxy.AbstractRpcHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
		return this.handleRequestAndSend(method, args);
	}
}
