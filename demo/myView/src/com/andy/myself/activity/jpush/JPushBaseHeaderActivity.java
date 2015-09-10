package com.andy.myself.activity.jpush;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andy.framework.util.AndyBarTintManager;
import com.andy.framework.util.AndyScreenUtil;
import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

import cn.jpush.android.api.InstrumentedActivity;

/**
 * @description: 
 * @author: andy
 * @mail: win58@qq.com
 * @date: 2015-9-9  下午5:43:35
 */
public class JPushBaseHeaderActivity extends InstrumentedActivity implements OnClickListener {
	protected Activity mActivity;
	protected TextView titleTextView;
	protected ImageView backImageView;
	
	protected ImageView titleRightView;
	protected EditText titleEidtView;
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		
		View barView = findViewById(R.id.common_title_bar_id);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			LayoutParams params = barView.getLayoutParams();
			params.height = AndyScreenUtil.getStatusHeight(mActivity);
			barView.setLayoutParams(params);
			barView.setVisibility(View.INVISIBLE);
		} else {
			barView.setVisibility(View.GONE);
		}
		
		titleTextView = (TextView) findViewById(R.id.common_title_view_id);
		backImageView = (ImageView) findViewById(R.id.common_back_view_id);
		titleRightView = (ImageView) findViewById(R.id.common_right_view_id);
		titleEidtView = (EditText) findViewById(R.id.common_title_edit_id);
		backImageView.setOnClickListener(this);
		
		if (getIntent() != null && getIntent().getExtras() != null) {
			String string = getIntent().getExtras().getString(BaseHeaderActivity.KEY_TITLE_SRC);
			
			if (string != null && string.length() > 0) {
				titleTextView.setText(string);
			} else {
				titleTextView.setText(R.string.activity_start_title);
			}
		} else {
			titleTextView.setText(R.string.activity_start_title);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.common_back_view_id) {
			finish();
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = this;
		AndyBarTintManager manager = new AndyBarTintManager(this);
		manager.setBarTintTransparent(true);
	}
}
