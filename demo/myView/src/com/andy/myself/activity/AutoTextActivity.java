package com.andy.myself.activity;

import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;
import com.andy.view.textAnim.AutoTextView;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AutoTextActivity extends BaseActivity {
 private Button mBtnNext;
 private Button mBtnPrev;
 private AutoTextView mTextView02;
 private static int sCount = 10;
 @Override
 protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_font_anim);
  initView(savedInstanceState);
 }
 
 @Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		mBtnNext = (Button) findViewById(R.id.next);
		mBtnPrev = (Button) findViewById(R.id.prev);
		mTextView02 = (AutoTextView) findViewById(R.id.switcher02);
		titleTextView.setText(R.string.activity_font_anim_title);
		mTextView02.setText("Hello world!");
		mBtnPrev.setOnClickListener(this);
		mBtnNext.setOnClickListener(this);
	}
 
 @Override
 public void onClick(View arg0) {
  // TODO Auto-generated method stub
	 super.onClick(arg0);
  switch (arg0.getId()) {
  case R.id.next:
   mTextView02.next();
   sCount++;
   break;
  case R.id.prev:
   mTextView02.previous();
   sCount--;
   break;
  }
  mTextView02.setText(sCount%2 == 0 ? "南无阿弥陀佛" : "SB 点右边");
  System.out.println("getH: ["+mTextView02.getHeight()+"]");

 }
}