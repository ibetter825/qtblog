package com.mypro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 排除控制器
 * 说明：Controller或Method过滤拦截器
 * @author yt
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface ClearInterceptor {
	@SuppressWarnings("rawtypes")
	Class[] interceptor() default {};
}
