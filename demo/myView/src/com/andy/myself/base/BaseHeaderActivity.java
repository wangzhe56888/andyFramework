package com.andy.myself.base;

import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andy.framework.util.AndyScreenUtil;
import com.andy.myself.R;
/**
 * @description: 带有header的Activity基类
 * @author: andy  
 * @mail: win58@qq.com
 */
public abstract class BaseHeaderActivity extends BaseActivity implements OnClickListener{

	protected final String KEY_TITLE_SRC = "KEY_TITLE_SRC";
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
