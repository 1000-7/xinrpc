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
public class RpcResponse implements Serializable {
	private String requestId;
	private Throwable throwable;
	private Object result;
	private Boolean success;
}