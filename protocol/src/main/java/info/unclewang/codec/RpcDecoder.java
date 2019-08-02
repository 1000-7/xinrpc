package info.unclewang.codec;

import info.unclewang.serialization.XinSerializable;
import info.unclewang.serialization.impl.FstSerialization;
import info.unclewang.serialization.impl.KryoSerialization;
import info.unclewang.serialization.impl.ProtoBufferSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author xavior.wx
 * @date 2019-07-22 17:52
 */
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {
	private Class<?> clz;
	private XinSerializable xinSerializable;

	public RpcDecoder(Class<?> clz, XinSerializable xinSerializable) {
		this.clz = clz;
		this.xinSerializable = new ProtoBufferSerialization();
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

		Object obj = xinSerializable.deSerialize(data, clz);
		list.add(obj);
	}
}
