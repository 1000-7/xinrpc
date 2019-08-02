package info.unclewang.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author unclewang
 */
@Data
public class RpcRequest implements Serializable {
	private String id;
	private String className;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;
}