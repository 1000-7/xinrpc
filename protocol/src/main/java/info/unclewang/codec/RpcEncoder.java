package info.unclewang.codec;

import info.unclewang.annotation.RpcBean;
import info.unclewang.serialization.SerializationFactory;
import info.unclewang.serialization.XinSerializable;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author unclewang
 * @date 2019-07-22 17:54
 */
@Slf4j
public class RpcEncoder extends MessageToByteEncoder {
	private Class<?> clz;
	private XinSerializable xinSerializable;

	public RpcEncoder(Class<?> clz) {
		this.clz = clz;
		RpcBean annotation = clz.getAnnotation(RpcBean.class);
		this.xinSerializable = SerializationFactory.getBySerializeType(annotation.serializeType());
	}

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
		if (clz != null) {
			log.info("开始序列化,{},{}", xinSerializable.getClass().getName(), new Date());
			byte[] bytes = xinSerializable.serialize(o);
			log.info("序列化结束,{},{}", xinSerializable.getClass().getName(), new Date());
			byteBuf.writeInt(bytes.length);
			byteBuf.writeBytes(bytes);
		}
	}
}
