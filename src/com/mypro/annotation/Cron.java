package com.mypro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: Cron  
 * @Description: Cron任务注解  
 * @author 李飞  
 * @date 2015年8月23日 上午2:11:34
 * @since V1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Cron {
    String value();
}