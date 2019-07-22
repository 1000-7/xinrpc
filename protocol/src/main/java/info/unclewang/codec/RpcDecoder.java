package info.unclewang.codec;

import info.unclewang.serialization.XinSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author xavior.wx
 * @date 2019-07-22 17:52
 */
public class RpcDecoder<T> extends ByteToMessageDecoder {
	private Class<T> clz;
	private XinSerializable xinSerializable;

	public RpcDecoder(Class<T> clz, XinSerializable xinSerializable) {
		this.clz = clz;
		this.xinSerializable = xinSerializable;
	}

	/**
	 * @param channelHandlerContext
	 * @param byteBuf
	 * @param list
	 * @throws Exception
	 */
	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		if (byteBuf.readableBytes() < 4) {
			return;
		}

		byteBuf.markReaderIndex();
		int dataLength = byteBuf.readInt();
		if (byteBuf.readableBytes() < dataLength) {
			byteBuf.resetReaderIndex();
			return;
		}
		byte[] data = new byte[dataLength];
		byteBuf.readBytes(data);

		T obj = xinSerializable.deSerialize(data, clz);
		list.add(obj);
	}
}
