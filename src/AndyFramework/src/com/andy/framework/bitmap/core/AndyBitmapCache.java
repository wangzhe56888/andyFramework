package com.andy.framework.bitmap.core;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.andy.framework.bitmap.core.AndyBytesBufferPool.AndyBytesBuffer;
import com.andy.framework.bitmap.core.AndyDiskCache.LookupRequest;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * @description: bitmap缓存
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  上午11:26:34
 */
public class AndyBitmapCache {
	//默认的内存缓存大小 8MB
    private static final int DEFAULT_MEMORY_CACHE_SIZE = 1024 * 1024 * 8;

    //默认的磁盘缓存大小 50M
    private static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 50;
	// 缓存的图片数量
    private static final int DEFAULT_DISK_CACHE_COUNT = 1000 * 10 ;

    //BitmapCache的一些默认配置
    private static final boolean DEFAULT_MEMORY_CACHE_ENABLED = true;
    private static final boolean DEFAULT_DISK_CACHE_ENABLED = true;
    
    private AndyDiskCache mDiskCache;
    private AndyIMemoryCache mMemoryCache;
    private AndyImageCacheParams mCacheParams;
    
    public AndyBitmapCache(AndyImageCacheParams cacheParams) {
    	init(cacheParams);
    }
    
    private void init(AndyImageCacheParams cacheParams) {
    	mCacheParams = cacheParams;
    	if (mCacheParams.memoryCacheEnabled) {
			if (mCacheParams.recycleImmediately) {
				mMemoryCache = new AndySoftMemoryCacheImpl(mCacheParams.memCacheSize);
			} else {
				mMemoryCache = new AndyBaseMemoryCacheImpl(mCacheParams.memCacheSize);
			}
		}
    	if (mCacheParams.diskCacheEnabled) {
			String pathString = mCacheParams.diskCacheDir.getAbsolutePath();
			try {
				mDiskCache = new AndyDiskCache(pathString, mCacheParams.diskCacheCount, mCacheParams.diskCacheSize, false);
			} catch (IOException e) {
				// 忽略
			}
		}
    }
    
    /**
     * 添加图片到内存缓存中
     * @param url Url 地址
     * @param bitmap 图片数据
     */
    public void addToMemoryCache(String url, Bitmap bitmap) {
        if (url == null || bitmap == null) {
            return;
        }
        mMemoryCache.put(url, bitmap);
    }
    
    /**
     * 添加 数据到sdcard缓存中
     * @param url url地址
     * @param data 数据信息
     */
    public void addToDiskCache(String url, byte[] data) {
        if (mDiskCache == null || url == null || data == null) {
            return;
        }
        //Add to disk cache
        byte[] key = AndyBitmapUtil.makeKey(url);
        long cacheKey = AndyBitmapUtil.crc64Long(key);
        ByteBuffer buffer = ByteBuffer.allocate(key.length + data.length);
        buffer.put(key);
        buffer.put(data);
        synchronized (mDiskCache) {
            try {
            	mDiskCache.insert(cacheKey, buffer.array());
            } catch (IOException ex) {
                // ignore.
            }
        }
    }
    
    /**
     * 从sdcard中获取内存缓存
     * @param url 图片url地址
     * @param buffer 填充缓存区
     * @return 是否获得图片
     */
    public boolean getImageData(String url, AndyBytesBuffer buffer){
    	if(mDiskCache == null)
    		return false;
    	
    	byte[] key = AndyBitmapUtil.makeKey(url);
        long cacheKey = AndyBitmapUtil.crc64Long(key);
        try {
            LookupRequest request = new LookupRequest();
            request.key = cacheKey;
            request.buffer = buffer.data;
            synchronized (mDiskCache) {
                if (!mDiskCache.lookup(request)) 
                	return false;
            }
            if (AndyBitmapUtil.isSameKey(key, request.buffer)) {
                buffer.data = request.buffer;
                buffer.offset = key.length;
                buffer.length = request.length - buffer.offset;
                return true;
            }
        } catch (IOException ex) {
            // ignore.
        }
        return false;
    }
    
    /**
     * 从内存缓存中获取bitmap.
     */
    public Bitmap getBitmapFromMemoryCache(String data) {
        if (mMemoryCache != null)
        	return mMemoryCache.get(data);
        return null;
    }

    /**
     * 清空内存缓存和sdcard缓存
     */
    public void clearCache() {
    	clearMemoryCache();
    	clearDiskCache();
    }
    
    public void clearDiskCache(){
    	if(mDiskCache!=null)
    		mDiskCache.delete();
    }
    
    public void clearMemoryCache(){
    	if (mMemoryCache != null) {
            mMemoryCache.removeAll();
        }
    }
    
    public void clearCache(String key) {
    	clearMemoryCache(key);
    	clearDiskCache(key);
    }
    
    public void clearDiskCache(String url){
    	addToDiskCache(url, new byte[0]);
    }
    
    public void clearMemoryCache(String key){
    	if (mMemoryCache != null) {
            mMemoryCache.remove(key);
        }
    }
    
    /**
     * 关闭磁盘缓存
     * 注意：该方法包含磁盘存取，所以不应该在主线程使用
     */
    public void close() {
    	if(mDiskCache != null)
    		mDiskCache.close();
    }
    
    
    /**
     * Image cache 的配置信息
     */
    public static class AndyImageCacheParams {
        public int memCacheSize = DEFAULT_MEMORY_CACHE_SIZE;
        public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
        public int diskCacheCount = DEFAULT_DISK_CACHE_COUNT;
        public File diskCacheDir;
        // 是否启动内存缓存
        public boolean memoryCacheEnabled = DEFAULT_MEMORY_CACHE_ENABLED;
        // 是否启动sdcard缓存
        public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
        // 是否立即回收内存
        public boolean recycleImmediately = true ;


        public AndyImageCacheParams(File diskCacheDir) {
            this.diskCacheDir = diskCacheDir;
        }
        
        public AndyImageCacheParams(String diskCacheDir) {
            this.diskCacheDir = new File(diskCacheDir);
        }

        /**
         * 设置缓存大小 
         * @param context
         * @param percent 百分比的范围是在 0.05 到 0.8之间
         */
        public void setMemCacheSizePercent(Context context, float percent) {
            if (percent < 0.05f || percent > 0.8f) {
                throw new IllegalArgumentException("setMemCacheSizePercent - percent must be "
                        + "between 0.05 and 0.8 (inclusive)");
            }
            memCacheSize = Math.round(percent * getMemoryClass(context) * 1024 * 1024);
        }
        
		public void setMemCacheSize(int memCacheSize) {
			this.memCacheSize = memCacheSize;
		}

		public void setDiskCacheSize(int diskCacheSize) {
			this.diskCacheSize = diskCacheSize;
		}
		
		private static int getMemoryClass(Context context) {
            return ((ActivityManager) context.getSystemService(
                    Context.ACTIVITY_SERVICE)).getMemoryClass();
        }

		public void setDiskCacheCount(int diskCacheCount) {
			this.diskCacheCount = diskCacheCount;
		}

		public void setRecycleImmediately(boolean recycleImmediately) {
			this.recycleImmediately = recycleImmediately;
		}
    }
}
