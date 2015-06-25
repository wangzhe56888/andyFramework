package com.andy.framework.bitmap.core;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.graphics.Bitmap;

/**
 * @description: 强引用实现cache
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-12  下午5:19:49
 */
public class AndySoftMemoryCacheImpl implements AndyIMemoryCache {

	private final HashMap<String, SoftReference<Bitmap>> mMemoryCache;
	
	public AndySoftMemoryCacheImpl(int size) {
		mMemoryCache = new HashMap<String, SoftReference<Bitmap>>();
	}
	
	@Override
	public void put(String key, Bitmap bitmap) {
		mMemoryCache.put(key, new SoftReference<Bitmap>(bitmap));
	}

	@Override
	public Bitmap get(String key) {
		SoftReference<Bitmap> softReference = mMemoryCache.get(key);
		if (softReference != null) {
			return softReference.get();
		}
		return null;
	}

	@Override
	public void removeAll() {
		mMemoryCache.clear();
	}

	@Override
	public void remove(String key) {
		mMemoryCache.remove(key);
	}

}
