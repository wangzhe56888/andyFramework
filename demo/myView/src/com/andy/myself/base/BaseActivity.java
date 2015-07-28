package com.andy.myself.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

import com.andy.framework.util.AndyBarTintManager;
import com.andy.myself.AndyApplication;
import com.andy.myself.R;

/**
 * @description: Activity基类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-7-28  下午2:39:50
 */
public class BaseActivity extends Activity {
	protected final String LOG_TAG = "LOG_TAG";
	protected Activity mActivity;
	
	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
//		((AndyApplication)getApplication()).addActivity(mActivity);
		
		AndyBarTintManager manager = new AndyBarTintManager(this);
		manager.setBarTintTransparent(true);
	}
	
	protected void initView(Bundle savedInstanceState) {
	}
}
