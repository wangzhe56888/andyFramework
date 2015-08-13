package com.andy.myself.activity.andyframework;

import com.andy.framework.sqlite.annotation.AndyTable;

/**
 * @description: 
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-8-13  上午10:35:26
 */
@AndyTable(name="user")
public class DBUserBean {
	private int id;
	private String username;
	private String password;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
