package com.andy.framework.bitmap.display;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * @description: 默认displayer
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  上午10:44:41
 */
public class AndySimpleDisplayer implements AndyDisplayer {

	@Override
	public void loadCompletedisplay(View imageView, Bitmap bitmap, AndyBitmapDisplayConfig config) {
		switch (config.getAnimationType()) {
			case AndyBitmapDisplayConfig.AnimationType.fadeIn:
				displayWithFadeIn(imageView, bitmap);
				break;
			case AndyBitmapDisplayConfig.AnimationType.userDefined:
				displayWithAnimation(imageView, bitmap, config.getAnimation());
				break;
			default:
				break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void loadFailDisplay(View imageView, Bitmap bitmap) {
		if (imageView instanceof ImageView) {
			((ImageView)imageView).setImageBitmap(bitmap);
		} else {
			imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
	}

	/**
	 * 默认显示动画
	 * */
	@SuppressWarnings("deprecation")
	private void displayWithFadeIn(View imageView, Bitmap bitmap) {
		Drawable[] drawables = new Drawable[] {
				new ColorDrawable(android.R.color.transparent),
				new BitmapDrawable(imageView.getResources(), bitmap)
			};
		final TransitionDrawable td = new TransitionDrawable(drawables);
		if (imageView instanceof ImageView) {
			((ImageView)imageView).setImageDrawable(td);
		} else {
			imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
		td.startTransition(300);
		
	}
	/**
	 * 自定义显示动画
	 * */
	@SuppressWarnings("deprecation")
	private void displayWithAnimation(View imageView, Bitmap bitmap, Animation animation) {
		animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());		
        if (imageView instanceof ImageView) {
			((ImageView)imageView).setImageBitmap(bitmap);
		} else {
			imageView.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
        imageView.startAnimation(animation);
	}
}
