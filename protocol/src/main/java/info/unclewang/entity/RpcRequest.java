package info.unclewang.entity;

import info.unclewang.annotation.RpcBean;
import info.unclewang.util.SerializationType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author unclewang
 */
@RpcBean(serializeType = SerializationType.JSON)
@Data
public class RpcRequest implements Serializable {
	private String id;
	private String className;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;
}