package info.unclewang.codec;

import info.unclewang.serialization.XinSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author unclewang
 * @date 2019-07-22 17:54
 */
public class RpcEncoder extends MessageToByteEncoder {
	private Class<?> clz;
	private XinSerializable xinSerializable;

	public RpcEncoder(Class<?> clz, XinSerializable xinSerializable) {
		this.clz = clz;
		this.xinSerializable = xinSerializable;
	}

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
		if (clz != null) {
			byte[] bytes = xinSerializable.serialize(o);
			byteBuf.writeInt(bytes.length);
			byteBuf.writeBytes(bytes);
		}
	}
}
