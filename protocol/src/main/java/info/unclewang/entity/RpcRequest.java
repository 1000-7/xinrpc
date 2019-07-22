package info.unclewang.entity;

import lombok.Data;

/**
 * @author unclewang
 */
@Data
public class RpcRequest {
	private String id;
	private String className;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;
}