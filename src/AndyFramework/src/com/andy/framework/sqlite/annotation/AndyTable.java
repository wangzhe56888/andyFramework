package com.andy.framework.sqlite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 实体表配置，name为表名
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:02:56
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AndyTable {
	public String name();
}
