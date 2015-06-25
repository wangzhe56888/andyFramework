package com.andy.tools.sharedpreference;

import android.content.SharedPreferences;

/**
 * 项目名称 ： 
 * 类名称 ： IStorage
 * 类描述 ： 
 * 创建时间 ： 2014-8-28上午11:11:49
 * email : win58@qq.com
 * nickname : Andy
 * @author wangys
 */
public interface IStorage {

	/**
	 * 序列化存储数据
	 **/
	public void serialize(SharedPreferences sp);
	
	/**
	 * 反序列化取出存储数据
	 **/
	public void unSerialize(SharedPreferences sp);
	
	/**
	 * 删除存储数据
	 **/
	public void delete(SharedPreferences sp);
	
	/**
	 *获取标识符 
	 **/
	public String getIdentifer();
}
