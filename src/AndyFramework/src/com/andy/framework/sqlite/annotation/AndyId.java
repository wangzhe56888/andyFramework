package com.andy.framework.sqlite.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: ID注解，使用与属性，不配置的时候默认找id或_id字段作为主键
 * column不配置的时候默认为字段名
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午4:49:11
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AndyId {
	public String column() default "";
}
