package com.andy.framework.bitmap.core;

import android.graphics.Bitmap;

/**
 * @description: 缓存设置接口
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-12  下午4:47:03
 */
public interface AndyIMemoryCache {
	public void put(String key, Bitmap bitmap);
	public Bitmap get(String key);
	public void removeAll();
	public void remove(String key);
}
