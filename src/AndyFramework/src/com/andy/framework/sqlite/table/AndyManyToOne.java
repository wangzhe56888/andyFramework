package com.andy.framework.sqlite.table;
/**
 * @description: 
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:40:09
 */
public class AndyManyToOne extends AndyProperty {
	private Class<?> manyClass;

	public Class<?> getManyClass() {
		return manyClass;
	}

	public void setManyClass(Class<?> manyClass) {
		this.manyClass = manyClass;
	}
}
