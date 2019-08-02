package info.unclewang.serialization.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import info.unclewang.serialization.XinSerializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author unclewang
 * @date 2019-08-02 11:29
 */
public class KryoSerialization implements XinSerializable {
	private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);//支持循环引用
		kryo.setRegistrationRequired(false);//关闭注册行为
		return kryo;
	});

	@Override
	public <T> byte[] serialize(T obj) {
		Kryo kryo = KRYO_THREAD_LOCAL.get();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Output output = new Output(byteArrayOutputStream);
		kryo.writeClassAndObject(output, obj);
		output.close();
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public <T> T deSerialize(byte[] data, Class<T> clz) {
		Kryo kryo = KRYO_THREAD_LOCAL.get();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		Input input = new Input(byteArrayInputStream);
		input.close();
		return (T) kryo.readClassAndObject(input);
	}
}
