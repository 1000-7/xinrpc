package info.unclewang.serialization.impl;

import com.alibaba.fastjson.JSON;
import info.unclewang.serialization.XinSerializable;

/**
 * @Author unclewang
 * @Date 2019-07-22 17:36
 */
public class FastJsonSerialization implements XinSerializable {
	/**
	 * 对象序列成二进制
	 *
	 * @param obj
	 * @return
	 */
	@Override
	public <T> byte[] serialize(T obj) {
		return JSON.toJSONBytes(obj);
	}

	/**
	 * 二进制对象反序列化
	 *
	 * @param data
	 * @param clz
	 * @return
	 */
	@Override
	public <T> T deSerialize(byte[] data, Class<T> clz) {
		return JSON.parseObject(data, clz);
	}
}
