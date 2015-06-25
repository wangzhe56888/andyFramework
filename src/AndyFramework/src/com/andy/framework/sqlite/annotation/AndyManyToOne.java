package com.andy.framework.sqlite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 多对一配置
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午4:55:31
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AndyManyToOne {
	public String column() default "";
}
