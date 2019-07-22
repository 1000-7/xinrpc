package info.unclewang.serialization;

/**
 * @Author unclewang
 * @Date 2019-07-22 17:35
 */
public interface XinSerializable {

	/**
	 * 对象序列成二进制
	 * @param obj
	 * @param <T>
	 * @return
	 */
	<T> byte[] serialize(T obj);

	/**
	 * 二进制对象反序列化
	 * @param data
	 * @param clz
	 * @param <T>
	 * @return
	 */
	<T> T deSerialize(byte[] data, Class<T> clz);

}
