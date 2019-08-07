package info.unclewang.serialization.impl;

import info.unclewang.serialization.XinSerializable;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

/**
 * @author unclewang
 * @date 2019-08-02 11:03
 */
@Slf4j
public class ProtoStuffSerialization implements XinSerializable {
	@Override
	public <T> byte[] serialize(T obj) {
		log.info("obj:{}", obj);
		Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
		log.info("schema:{}", schema);
		LinkedBuffer linkBuffer = LinkedBuffer.allocate();
		return ProtostuffIOUtil.toByteArray(obj, schema, linkBuffer);
	}

	@Override
	public <T> T deSerialize(byte[] data, Class<T> clz) {
		Schema<T> schema = RuntimeSchema.createFrom(clz);
		T message = schema.newMessage();
		ProtostuffIOUtil.mergeFrom(data, message, schema);
		return message;
	}
}
