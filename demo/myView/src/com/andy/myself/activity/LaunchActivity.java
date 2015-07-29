package com.andy.myself.activity;

import java.util.ArrayList;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andy.myself.R;
import com.andy.myself.StartActivity;
import com.andy.myself.base.BaseActivity;

/**
 * @description: 启动页面
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-7-28  下午3:22:22
 */
public class LaunchActivity extends BaseActivity implements AnimationListener{
	
	ArrayList<TextView> textViews = new ArrayList<TextView>(7);
	private AlphaAnimation textAnimation;
	
	private int index = 0;
	private AnimationListener textAnimationListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			textViews.get(index).setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}
		
		@Override
		public void onAnimationEnd(Animation animation) {
			textViews.get(index).setAnimation(null);
			index++;
			startNextAnimation();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		
		textViews.add((TextView) findViewById(R.id.launch_text_1));
		textViews.add((TextView) findViewById(R.id.launch_text_2));
		textViews.add((TextView) findViewById(R.id.launch_text_3));
		textViews.add((TextView) findViewById(R.id.launch_text_4));
		textViews.add((TextView) findViewById(R.id.launch_text_5));
		textViews.add((TextView) findViewById(R.id.launch_text_6));
		textViews.add((TextView) findViewById(R.id.launch_text_7));
		
		textAnimation = new AlphaAnimation(0.2f, 1.0f);
		textAnimation.setDuration(700);
		textAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		textAnimation.setAnimationListener(textAnimationListener);
		
		
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.launch_layout_id);
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
		alphaAnimation.setDuration(2500);
		alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		relativeLayout.setAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(this);
		alphaAnimation.start();
	}

	private void startNextAnimation() {
		if (index < textViews.size()) {
			TextView textView = textViews.get(index);
			textView.setAnimation(textAnimation);
			textAnimation.start();
		} else {
			startActivity(new Intent(mActivity, StartActivity.class));
			LaunchActivity.this.finish();
		}
	}
	
	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		startNextAnimation();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
