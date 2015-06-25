package com.andy.framework.bitmap.display;

import android.graphics.Bitmap;
import android.view.View;

/**
 * @description: 图片下载结果回调接口
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  上午10:37:43
 */
public interface AndyDisplayer {
	/**
	 * 图片加载完成 回调的函数
	 * @param imageView
	 * @param bitmap
	 * @param config
	 */
	public void loadCompletedisplay(View imageView, Bitmap bitmap, AndyBitmapDisplayConfig config);
	
	/**
	 * 图片加载失败回调的函数
	 * @param imageView
	 * @param bitmap
	 */
	public void loadFailDisplay(View imageView, Bitmap bitmap);
}
