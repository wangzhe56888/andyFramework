package com.andy.myself.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.andy.myself.R;

/**
 * @description: 加载提示框
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-17  下午3:38:29
 */
public class DefineProgressDialog extends ProgressDialog {
	private String message;
	private TextView messageTv;
	private Context context;
	
	public DefineProgressDialog(Context context, String message) {
		super(context);
		this.context = context;
		this.message = message;
	}
	
	public DefineProgressDialog(Context context) {
		super(context);
		this.context = context;
		this.message = "加载中...";
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		int width = context.getResources().getDisplayMetrics().widthPixels * 2 / 5;
		LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
		setContentView(view, layoutParams);
		messageTv = (TextView) findViewById(R.id.message_tv);
		messageTv.setText(message);
	}
	
	public void setTipMessage(String msg) {
		this.message = msg;
		if (messageTv != null) {
			messageTv.setText(message);
		}
	}
}
