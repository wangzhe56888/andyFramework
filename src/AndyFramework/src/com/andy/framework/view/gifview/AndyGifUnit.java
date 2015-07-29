package com.andy.framework.view.gifview;

import android.graphics.Bitmap;

/**
 * @description: GIF每帧的显示单元
 * @author: andy
 * @mail: win58@qq.com
 * @date: 2015-6-8  下午2:52:42
 */
public class AndyGifUnit {

	/** 显示位图 **/
	public Bitmap bitmap;
	/** 延迟时间 **/
	public int delay;
	/** 下一个显示单元 **/
	public AndyGifUnit nextUnit = null;
	
	/**
	 * GIF帧的显示单元构造函数
	 * @param bitmap 显示的位图
	 * @param delay 延迟时间
	 * */
	public AndyGifUnit(Bitmap bitmap, int delay) {
		this.bitmap = bitmap;
		this.delay = delay;
	}
}
