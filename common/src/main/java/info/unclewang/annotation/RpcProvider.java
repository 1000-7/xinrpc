package info.unclewang.annotation;

import info.unclewang.util.RpcUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author unclewang
 * @date 2019-07-28 13:57
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcProvider {
	String name() default RpcUtils.RPC_SERVER_NAME;
}
