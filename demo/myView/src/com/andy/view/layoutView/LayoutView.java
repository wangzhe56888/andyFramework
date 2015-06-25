package com.andy.view.layoutView;

import com.andy.myself.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * @description: 
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-21  下午2:16:29
 */
public class LayoutView extends FrameLayout {
	
	
	public LayoutView(Context context) {
		super(context);
	}

	public LayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(context).inflate(R.layout.activity_start, this, true);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		
	}
	
}
