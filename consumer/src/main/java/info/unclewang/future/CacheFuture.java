package info.unclewang.future;

import info.unclewang.entity.RpcResponse;

public class CacheFuture {
	private RpcResponse rpcResponse;
	private volatile boolean isSucceed = false;
	private final Object object = new Object();

	public RpcResponse getResponse(int timeout) {
		synchronized (object) {
			while (!isSucceed) {
				try {
					object.wait(timeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return rpcResponse;
		}
	}

	public void setResponse(RpcResponse response) {
		if (isSucceed) {
			return;
		}
		synchronized (object) {
			this.rpcResponse = response;
			this.isSucceed = true;
			object.notify();
		}
	}
}
