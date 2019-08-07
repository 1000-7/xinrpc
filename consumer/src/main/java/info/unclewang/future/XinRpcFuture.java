package info.unclewang.future;

import info.unclewang.entity.RpcResponse;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
/**
 * @author unclewang
 * @date 2019-08-07 15:32
 */
public class XinRpcFuture extends DefaultPromise<RpcResponse> {
	public XinRpcFuture(EventExecutor executor) {
		super(executor);
	}
}
