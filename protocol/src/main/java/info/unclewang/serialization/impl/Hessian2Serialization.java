package info.unclewang.serialization.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import info.unclewang.serialization.XinSerializable;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author unclewang
 * @date 2019-08-02 10:55
 */
@Slf4j
public class Hessian2Serialization implements XinSerializable {
	@Override
	public <T> byte[] serialize(T obj) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Hessian2Output hessian2Output = new Hessian2Output(byteArrayOutputStream);
		try {
			hessian2Output.writeObject(obj);
			// 必须先关闭，才能转成二进制数组
			hessian2Output.close();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			log.error("hessian write fail", e);
		} finally {
			try {
				byteArrayOutputStream.close();
			} catch (IOException e) {
				log.error("hessian write off fail", e);
			}
		}
		return new byte[0];

	}

	@Override
	public <T> T deSerialize(byte[] data, Class<T> clz) {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		Hessian2Input hessian2Input = new Hessian2Input(byteArrayInputStream);

		try {
			return (T) hessian2Input.readObject(clz);
		} catch (IOException e) {
			log.error("hessian read fail", e);
		} finally {
			try {
				hessian2Input.close();
				byteArrayInputStream.close();
			} catch (IOException e) {
				log.error("hessian read off fail", e);
			}
		}
		return null;
	}
}
