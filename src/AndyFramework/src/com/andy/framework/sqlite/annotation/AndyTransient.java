package com.andy.framework.sqlite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 忽略字段配置，通过该注解配置的字段将不会创建到表字段中
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:05:08
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AndyTransient {

}
