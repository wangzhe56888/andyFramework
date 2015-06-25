package com.andy.myself.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;

/**
 * 项目名称 ： 
 * 类名称 ： DragViewActivity
 * 类描述 ： 
 * 创建时间 ： 2014-9-2下午02:20:27
 * email : win58@qq.com
 * nickname : Andy
 * @author wangys
 */
public class DragViewActivity extends BaseActivity implements OnTouchListener {
	private Button btnOkc;
	private View titleView, tipView;
	private int lastX = 0, lastY = 0;
	private int screenWidth, screenHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drag_view);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		titleView = findViewById(R.id.title_id);
		tipView = findViewById(R.id.tip_text);
		
		btnOkc = (Button) findViewById(R.id.btn_okc);
		
		DisplayMetrics dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels - 20;
		screenHeight = dm.heightPixels - 110;
		
		Log.e(LOG_TAG, "屏幕宽度：" + screenWidth + " 高度：" + screenHeight);
		btnOkc.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			// 移动的距离
			int distanceX = (int)event.getRawX() - lastX;
			int distanceY = (int)event.getRawY() - lastY;
			
			int left = v.getLeft() + distanceX;
			int top = v.getTop() + distanceY;
			int right = v.getRight() + distanceX;
			int bottom = v.getBottom() + distanceY;
			
			if (left < 0) {
				left = 0;
				right = left + v.getWidth();
			}
			if (top < 0) {
				top = 0;
				bottom = top + v.getHeight();
			}
			if (right > screenWidth) {
				right = screenWidth;
				left = right - v.getWidth();
			}
			if (bottom > screenHeight) {
				bottom = screenHeight;
				top = bottom - v.getHeight();
			}
			v.layout(left, top, right, bottom);
			
			lastX = (int) event.getRawX();
			lastY = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return false;
	}
}
