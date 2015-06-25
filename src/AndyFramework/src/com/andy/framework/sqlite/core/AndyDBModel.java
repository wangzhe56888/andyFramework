package com.andy.framework.sqlite.core;

import java.util.HashMap;

/**
 * @description: DB模型
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-15  上午10:29:19
 */
public class AndyDBModel {
	private HashMap<String, Object> dataMap = new HashMap<String, Object>();
	
	public Object get(String column){
		return dataMap.get(column);
	}
	
	public String getString(String column){
		return String.valueOf(get(column));
	}
	
	public int getInt(String column){
		return Integer.valueOf(getString(column));
	}
	
	public boolean getBoolean(String column){
		return Boolean.valueOf(getString(column));
	}
	
	public double getDouble(String column){
		return Double.valueOf(getString(column));
	}
	
	public float getFloat(String column){
		return Float.valueOf(getString(column));
	}
	
	public long getLong(String column){
		return Long.valueOf(getString(column));
	}
	
	public void set(String key,Object value){
		dataMap.put(key, value);
	}
	
	public HashMap<String, Object> getDataMap(){
		return dataMap;
	}
}
