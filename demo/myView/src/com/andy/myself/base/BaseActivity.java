package com.andy.myself.base;

import android.app.Activity;
import android.os.Bundle;

import com.andy.framework.util.AndyBarTintManager;

/**
 * @description: Activity基类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-7-28  下午2:39:50
 */
public class BaseActivity extends Activity {
	protected final String LOG_TAG = "LOG_TAG";
	protected Activity mActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		AndyBarTintManager manager = new AndyBarTintManager(this);
		manager.setBarTintTransparent(true);
	}
	
	protected void initView(Bundle savedInstanceState) {
	}
}
