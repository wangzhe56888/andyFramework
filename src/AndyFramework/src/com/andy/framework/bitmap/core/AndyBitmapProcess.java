package com.andy.framework.bitmap.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.andy.framework.bitmap.core.AndyBytesBufferPool.AndyBytesBuffer;
import com.andy.framework.bitmap.display.AndyBitmapDisplayConfig;
import com.andy.framework.bitmap.download.AndyDownLoader;

/**
 * @description: 
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  下午1:22:36
 */
public class AndyBitmapProcess {
	
	private static final int BYTESBUFFE_POOL_SIZE = 4;
	private static final int BYTESBUFFER_SIZE = 200 * 1024;

	private AndyDownLoader downLoader;
	private AndyBitmapCache bitmapCache;
    
    private AndyBytesBufferPool bytesBufferPool = new AndyBytesBufferPool(BYTESBUFFE_POOL_SIZE, BYTESBUFFER_SIZE);
    
    public AndyBitmapProcess(AndyDownLoader downLoader, AndyBitmapCache bitmapCache) {
    	this.downLoader = downLoader;
    	this.bitmapCache = bitmapCache;
    }
    
    public Bitmap getBitmap(String url, AndyBitmapDisplayConfig config) {
    	Bitmap bitmap = getFromDisk(url,config);
		if (bitmap == null) {
			byte[] data = downLoader.download(url);
			if(data != null && data.length > 0){
				if (config !=null) {
					bitmap =  AndyBitmapDecoder.decodeSampledBitmapFromByteArray(data,0,data.length,config.getBitmapWidth(),config.getBitmapHeight());
				} else {
					return BitmapFactory.decodeByteArray(data,0,data.length);
				}
				bitmapCache.addToDiskCache(url, data);
			}
		}
		return bitmap;
    }
    
    public Bitmap getFromDisk(String key, AndyBitmapDisplayConfig config) {
        AndyBytesBuffer buffer = bytesBufferPool.get();
        Bitmap b = null;
        try {
        	boolean found = bitmapCache.getImageData(key, buffer);
            if (found && buffer.length - buffer.offset > 0) {
    	        if ( config != null) {
    	            b = AndyBitmapDecoder.decodeSampledBitmapFromByteArray(buffer.data,buffer.offset, buffer.length ,config.getBitmapWidth(),config.getBitmapHeight());
    	        } else {
    	        	b = BitmapFactory.decodeByteArray(buffer.data, buffer.offset, buffer.length);
    	        }
            }
        } finally {
        	bytesBufferPool.recycle(buffer);
        }
        return b;
    }
}
