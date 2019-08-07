package info.unclewang.util;

import lombok.AllArgsConstructor;

/**
 * @author unclewang
 * @date 2019-08-02 16:54
 */
@AllArgsConstructor
public enum SerializationType {
	/**
	 * 各种序列化类型
	 */
	JSON("json"),
	FST("fst"),
	HESSIAN2("hessian2"),
	PROTO_STUFF("protoStuff"),
	KRYO("kryo");

	private String value;

}
