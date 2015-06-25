package com.andy.framework.bitmap.display;

import android.graphics.Bitmap;
import android.view.animation.Animation;

/**
 * @description: bitmap显示配置类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  上午10:39:31
 */
public class AndyBitmapDisplayConfig {
	
	private int bitmapWidth;
	private int bitmapHeight;
	
	private Animation animation;
	private int animationType;
	
	private Bitmap loadingBitmap;
	private Bitmap loadfailBitmap;

	public int getBitmapWidth() {
		return bitmapWidth;
	}

	public void setBitmapWidth(int bitmapWidth) {
		this.bitmapWidth = bitmapWidth;
	}

	public int getBitmapHeight() {
		return bitmapHeight;
	}

	public void setBitmapHeight(int bitmapHeight) {
		this.bitmapHeight = bitmapHeight;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public int getAnimationType() {
		return animationType;
	}

	public void setAnimationType(int animationType) {
		this.animationType = animationType;
	}

	public Bitmap getLoadingBitmap() {
		return loadingBitmap;
	}

	public void setLoadingBitmap(Bitmap loadingBitmap) {
		this.loadingBitmap = loadingBitmap;
	}

	public Bitmap getLoadfailBitmap() {
		return loadfailBitmap;
	}

	public void setLoadfailBitmap(Bitmap loadfailBitmap) {
		this.loadfailBitmap = loadfailBitmap;
	}

	/**
	 * 动画类型
	 * */
	public class AnimationType {
		public static final int userDefined = 0;
		public static final int fadeIn = 1;
	}
}
