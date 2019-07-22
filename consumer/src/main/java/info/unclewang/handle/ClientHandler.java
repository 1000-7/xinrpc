package info.unclewang.handle;

import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author unclewang
 * @date 2019-07-22 20:37
 */
public class ClientHandler extends ChannelDuplexHandler {
	private final Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();


	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof RpcRequest) {
			RpcRequest request = (RpcRequest) msg;
			futureMap.putIfAbsent(request.getId(), new DefaultFuture());
		}
		super.write(ctx, msg, promise);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof RpcResponse) {
			RpcResponse response = (RpcResponse) msg;
			DefaultFuture defaultFuture = futureMap.get(response.getRequestId());
			defaultFuture.setResponse(response);
		}
		super.channelRead(ctx, msg);
	}

	public RpcResponse getRpcResponse(String requestId) {

		try {
			DefaultFuture defaultFuture = futureMap.get(requestId);
			return defaultFuture.getResponse(10);
		} finally {
			futureMap.remove(requestId);
		}


	}
}

class DefaultFuture {
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
