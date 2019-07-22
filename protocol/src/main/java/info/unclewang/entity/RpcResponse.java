package info.unclewang.entity;

import lombok.Data;

/**
 * @author unclewang
 */
@Data
public class RpcResponse {
	// 调用编号
	private String requestId;
	// 抛出的异常
	private Throwable throwable;
	// 返回结果
	private Object result;
}