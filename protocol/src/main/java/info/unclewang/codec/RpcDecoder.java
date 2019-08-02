package info.unclewang.codec;

import info.unclewang.annotation.RpcBean;
import info.unclewang.serialization.SerializationFactory;
import info.unclewang.serialization.XinSerializable;
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

	public RpcDecoder(Class<?> clz) {
		this.clz = clz;
		RpcBean annotation = clz.getAnnotation(RpcBean.class);
		this.xinSerializable = SerializationFactory.getBySerializeType(annotation.serializeType());
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
