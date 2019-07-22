package info.unclewang.entity;

import lombok.Data;

/**
 * @author unclewang
 */
@Data
public class RpcResponse {
	private String requestId;
	private Throwable throwable;
	private Object result;
	private boolean success;
}