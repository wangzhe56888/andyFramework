package com.andy.framework.bitmap.core;

import android.graphics.Bitmap;

/**
 * @description: base memory cache实现
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-12  下午4:51:33
 */
public class AndyBaseMemoryCacheImpl implements AndyIMemoryCache {

	private final AndyLRUMemoryCache<String, Bitmap> mMemoryCache;
	
	public AndyBaseMemoryCacheImpl(int maxSize) {
		mMemoryCache = new AndyLRUMemoryCache<String, Bitmap>(maxSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return AndyBitmapUtil.getBitmapSize(value);
			}
		};
	}
	@Override
	public void put(String key, Bitmap bitmap) {
		mMemoryCache.put(key, bitmap);
	}

	@Override
	public Bitmap get(String key) {
		return mMemoryCache.get(key);
	}

	@Override
	public void removeAll() {
		mMemoryCache.removeAll();
	}

	@Override
	public void remove(String key) {
		mMemoryCache.remove(key);
	}

}
