package info.unclewang.serialization.impl;

import info.unclewang.serialization.XinSerializable;
import org.nustaq.serialization.FSTConfiguration;

/**
 * @author unclewang
 * @date 2019-08-02 11:29
 */
public class FstSerialization implements XinSerializable {
	private static FSTConfiguration configuration = FSTConfiguration.createDefaultConfiguration();

	@Override
	public <T> byte[] serialize(T obj) {
		return configuration.asByteArray(obj);
	}

	@Override
	public <T> T deSerialize(byte[] data, Class<T> clz) {
		return (T) configuration.asObject(data);
	}
}

