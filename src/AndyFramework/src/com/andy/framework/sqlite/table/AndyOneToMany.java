package com.andy.framework.sqlite.table;
/**
 * @description: 
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:41:30
 */
public class AndyOneToMany extends AndyProperty {
	private Class<?> oneClass;

	public Class<?> getOneClass() {
		return oneClass;
	}

	public void setOneClass(Class<?> oneClass) {
		this.oneClass = oneClass;
	}
}
