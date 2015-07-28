package com.andy.myself.activity;

import android.os.Bundle;

import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

public class DrawImageActivity extends BaseHeaderActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_draw_image);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		
		titleTextView.setText(R.string.activity_draw_image_title);
	}
}
