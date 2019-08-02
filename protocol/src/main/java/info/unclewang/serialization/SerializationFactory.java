package info.unclewang.serialization;

import info.unclewang.serialization.impl.*;
import info.unclewang.util.SerializationType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author unclewang
 * @date 2019-08-02 16:50
 */
@Slf4j
public class SerializationFactory {
	public static XinSerializable getBySerializeType(SerializationType serializeType) {
		switch (serializeType) {
			case JSON:
				return new FastJsonSerialization();
			case FST:
				return new FstSerialization();
			case HESSIAN2:
				return new Hessian2Serialization();
			case KRYO:
				return new KryoSerialization();
			case PROTO_BUFFER:
				return new ProtoBufferSerialization();
			default:
				log.debug("serializeType exists error, {}", serializeType);
				return new ProtoBufferSerialization();
		}

	}
}
