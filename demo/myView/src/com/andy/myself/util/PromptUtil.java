package com.andy.myself.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.myself.R;

public class PromptUtil {
	/**
	 * 显示提示信息
	 * @param context 
	 * @param tipMsg 提示信息，必须是String或者Integer
	 * @param duration 持续时间
	 * */
	public static void showToast(Context context, Object tipMsg, int duration) {
		// 测试的View，需求确定之后进行修改
		View view = LayoutInflater.from(context).inflate(R.layout.andy_toast_layout, null);
		
		TextView tipTextView = (TextView) view.findViewById(R.id.andy_toast_tip_textview);
		if (tipMsg instanceof String) {
			tipTextView.setText((String)tipMsg);
		} else if (tipMsg instanceof Integer) {
			tipTextView.setText((Integer)tipMsg);
		} else {
			throw new IllegalArgumentException("tipMsg must be String or Integer!");
		}
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(view);
		toast.setDuration(duration);
		toast.show();
	}
	
	public static void showToast(Context context, String tipMString) {
		PromptUtil.showToast(context, tipMString, Toast.LENGTH_SHORT);
	}
	
	public static void showToast(Context context, int tipRes) {
		PromptUtil.showToast(context, tipRes, Toast.LENGTH_SHORT);
	}
}
