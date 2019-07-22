package info.unclewang.entity;

import lombok.Data;

/**
 * @author unclewang
 */
@Data
public class RpcRequest {
    // 调用编号
    private String requestId;
    // 类名
    private String className;
    // 方法名
    private String methodName;
    // 请求参数的数据类型
    private Class<?>[] parameterTypes;
    // 请求的参数
    private Object[] parameters;
}