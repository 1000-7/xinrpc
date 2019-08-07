package info.unclewang.handle;

import info.unclewang.entity.RpcResponse;
import info.unclewang.future.FutureHolder;
import info.unclewang.future.XinRpcFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
/**
 * @author unclewang
 * @date 2019-08-07 15:32
 */
@ChannelHandler.Sharable
public class ClientFutureHandle extends SimpleChannelInboundHandler<RpcResponse> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
		XinRpcFuture xinRpcFuture = FutureHolder.getAndRemoveFuture(msg.getRequestId());
		if (xinRpcFuture != null) {
			xinRpcFuture.setSuccess(msg);
		}
	}
}
