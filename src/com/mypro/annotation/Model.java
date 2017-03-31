package com.mypro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Model注解
 * @author ibett
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Model {
	/**
	 * Model对应的数据库
	 * @return
	 */
    String dataSourceName();
    
    /**
     * Model对应的表
     * @return
     */
    String tableName();

    /**
     * Model的主键列名称
     * 描述：列明可以不是no，但是在建立model的时候那就必须用pkName="xxx"注明
     * @return
     */
    String pkName() default "no";
}
