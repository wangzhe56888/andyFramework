package com.andy.framework.sqlite.core;

import java.util.LinkedList;

/**
 * @description: Sql info
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-15  上午10:40:48
 */
public class AndySqlInfo {
	private String sql;
	// 绑定参数
	private LinkedList<Object> bindArgs;
	
	public Object[] getBindArgsAsArray() {
		if (bindArgs != null)
			return bindArgs.toArray();
		return null;
	}
	
	public String[] getBindArgsAsStringArray() {
		if (bindArgs != null) {
			String[] strings = new String[bindArgs.size()];
			for (int i = 0; i < bindArgs.size(); i++) {
				strings[i] = bindArgs.get(i).toString();
			}
			return strings;
		}
		return null;
	}
	
	public void addValue(Object obj) {
		if (bindArgs == null) {
			bindArgs = new LinkedList<Object>();
		}
		bindArgs.add(obj);
	}
	
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public LinkedList<Object> getBindArgs() {
		return bindArgs;
	}
	public void setBindArgs(LinkedList<Object> bindArgs) {
		this.bindArgs = bindArgs;
	}
}
