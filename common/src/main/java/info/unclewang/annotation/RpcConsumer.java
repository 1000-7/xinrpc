package info.unclewang.annotation;

import info.unclewang.util.RpcUtils;

import java.lang.annotation.*;

/**
 * @author unclewang
 * @date 2019-07-28 14:50
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcConsumer {
	String call() default RpcUtils.RPC_SERVER_NAME;
}
