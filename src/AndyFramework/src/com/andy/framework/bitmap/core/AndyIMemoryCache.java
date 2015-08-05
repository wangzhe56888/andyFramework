package com.andy.framework.bitmap.core;

import android.graphics.Bitmap;

/**
 * @description: 缓存设置接口
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-12  下午4:47:03
 */
public interface AndyIMemoryCache {
	/**
	 * 将Bitmap添加到缓存
	 * @param key
	 * @param bitmap
	 * */
	public void put(String key, Bitmap bitmap);
	/**
	 * 从缓存获取Bitmap
	 * @param key
	 * @return bitmap
	 * */
	public Bitmap get(String key);
	/**
	 * 删除所有缓存数据
	 * */
	public void removeAll();
	/**
	 * 根据key移除缓存数据
	 * */
	public void remove(String key);
}
