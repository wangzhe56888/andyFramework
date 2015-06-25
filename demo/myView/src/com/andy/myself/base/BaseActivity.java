package com.andy.myself.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andy.myself.AndyApplication;
import com.andy.myself.R;

public abstract class BaseActivity extends Activity implements OnClickListener{

	protected final String LOG_TAG = "LOG_TAG";
	protected final String KEY_TITLE_SRC = "KEY_TITLE_SRC";
	protected TextView titleTextView;
	protected ImageView backImageView;
	protected Activity mActivity;
	protected ImageView titleRightView;
	protected EditText titleEidtView;
	
	protected void initView(Bundle savedInstanceState) {
		mActivity = this;
		((AndyApplication)getApplication()).addActivity(mActivity);
		
		titleTextView = (TextView) findViewById(R.id.common_title_view_id);
		backImageView = (ImageView) findViewById(R.id.common_back_view_id);
		titleRightView = (ImageView) findViewById(R.id.common_right_view_id);
		titleEidtView = (EditText) findViewById(R.id.common_title_edit_id);
		backImageView.setOnClickListener(this);
		
		if (getIntent() != null && getIntent().getExtras() != null) {
			String string = getIntent().getExtras().getString(KEY_TITLE_SRC);
			
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
}
