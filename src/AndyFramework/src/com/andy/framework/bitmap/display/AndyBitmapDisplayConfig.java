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
	/** 图片宽度 **/
	private int bitmapWidth;
	/** 图片高度 **/
	private int bitmapHeight;
	/** 显示动画 **/
	private Animation animation;
	/** 动画类型 **/
	private int animationType;
	/** 正在下载要显示的图片 **/
	private Bitmap loadingBitmap;
	/** 加载失败显示的图片 **/
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
	 * 图片显示动画类型
	 * */
	public class AnimationType {
		public static final int TYPE_NO_ANIMATION = 0;
		public static final int TYPE_USER_DEFINED = 1;
		public static final int TYPE_DEFAULT_FADEIN = 2;
	}
}
