package info.unclewang.annotation;

import info.unclewang.util.SerializationType;

import java.lang.annotation.*;

/**
 * @author unclewang
 * @date 2019-08-02 15:55
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcBean {
	SerializationType serializeType() default SerializationType.JSON;
}
