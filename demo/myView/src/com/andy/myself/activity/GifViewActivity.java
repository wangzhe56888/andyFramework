package com.andy.myself.activity;

import android.os.Bundle;
import android.view.View;

import com.andy.framework.gifview.AndyGifView;
import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

/**
 * @description: GIF动画显示的控件测试
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-9  上午9:56:47
 */
public class GifViewActivity extends BaseHeaderActivity {
	
	private AndyGifView gifView, angelView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gifview);
		initView(savedInstanceState);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		
		gifView = (AndyGifView) findViewById(R.id.andyGifView_id);
		angelView = (AndyGifView) findViewById(R.id.andyGifView_angel_id);
		
		gifView.setGifImage(R.drawable.gif_girl);
		angelView.setGifImage(R.drawable.gif_angel);
		
		angelView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				angelView.convertAnimation();
			}
		});
	}


}
