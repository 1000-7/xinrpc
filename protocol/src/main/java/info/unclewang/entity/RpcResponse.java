package info.unclewang.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author unclewang
 */
@Data
public class RpcResponse implements Serializable {
	private String requestId;
	private Throwable throwable;
	private Object result;
	private Boolean success;
}