package com.andy.framework.sqlite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 一对多配置
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午4:58:04
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AndyOneToMany {
	public String manyColumn();
}
