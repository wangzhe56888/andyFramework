package com.andy.framework.sqlite.table;

import com.andy.framework.sqlite.core.AndyFieldUtils;

/**
 * @description: key-value
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-14  下午5:36:13
 */
public class AndyKeyValue {

	private String key;
	private Object value;
	
	public AndyKeyValue() {
		
	}
	
	public AndyKeyValue(String key, Object value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Object getValue() {
		if(value instanceof java.util.Date || value instanceof java.sql.Date){
			return AndyFieldUtils.SDF.format(value);
		}
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
