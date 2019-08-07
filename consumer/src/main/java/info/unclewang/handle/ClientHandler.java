package info.unclewang.handle;

import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;
import info.unclewang.future.CacheFuture;
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
	private final Map<String, CacheFuture> futureCacheMap = new ConcurrentHashMap<>();


	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		if (msg instanceof RpcRequest) {
			RpcRequest request = (RpcRequest) msg;
			futureCacheMap.putIfAbsent(request.getId(), new CacheFuture());
		}
		super.write(ctx, msg, promise);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof RpcResponse) {
			RpcResponse response = (RpcResponse) msg;
			CacheFuture cacheFuture = futureCacheMap.get(response.getRequestId());
			cacheFuture.setResponse(response);
		}
		super.channelRead(ctx, msg);
	}

	public RpcResponse getRpcResponse(String requestId) {
		try {
			CacheFuture cacheFuture = futureCacheMap.get(requestId);
			return cacheFuture.getResponse(10);
		} finally {
			futureCacheMap.remove(requestId);
		}
	}
}

