package com.andy.framework.sqlite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 属性配置
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:00:22
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AndyProperty {
	public String column() default "";
	public String defaultValue() default "";
}
