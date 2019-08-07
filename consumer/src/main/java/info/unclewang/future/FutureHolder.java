package info.unclewang.future;

import io.netty.util.concurrent.FastThreadLocal;

import java.util.HashMap;

/**
 * @author unclewang
 * @date 2019-08-07 15:19
 */
public class FutureHolder {

	private static final FastThreadLocal<HashMap<String, XinRpcFuture>> FUTURE_HOLDER = new FastThreadLocal<HashMap<String, XinRpcFuture>>() {
		@Override
		protected HashMap<String, XinRpcFuture> initialValue() {
			return new HashMap<>();
		}
	};


	public static void registerFuture(String requestId, XinRpcFuture future) {
		FUTURE_HOLDER.get().put(requestId, future);
	}

	public static XinRpcFuture getAndRemoveFuture(String requestId) {
		return FUTURE_HOLDER.get().remove(requestId);
	}


}
