package com.andy.myself.activity.andyframework;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.andy.framework.http.AndyHttp;
import com.andy.framework.http.AndyHttpCallback;
import com.andy.framework.http.AndyRequestParams;
import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

/**
 * @description: andy framework http框架测试
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  下午2:22:35
 */
public class AndyHttpActivity extends BaseHeaderActivity {

	private final String URL_HTTP = "http://192.168.8.39:8080/AndroidService/outputData";
//	private final String URL_HTTP = "http://192.168.8.39:8080/AndroidService/img/1392601634709.jpg";
	private Button getButton, postButton;
	private TextView resultTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_framework_http);
		initView(savedInstanceState);
	}
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		titleTextView.setText(R.string.activity_andyframework_http_title);
		
		getButton = (Button) findViewById(R.id.framework_http_get_btn);
		postButton = (Button) findViewById(R.id.framework_http_post_btn);
		resultTextView = (TextView) findViewById(R.id.framework_http_result_tv);
		// 让textview滚动需要有这行代码的设置
		resultTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		
		getButton.setOnClickListener(this);
		postButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.framework_http_get_btn:
				doHttpGet();
				break;
			case R.id.framework_http_post_btn:
				doHttpPost();
				break;
			default:
				break;
		}
		super.onClick(v);
	}
	
	private void doHttpGet() {
		AndyHttp http = new AndyHttp();
		
		AndyRequestParams params = new AndyRequestParams();
		String inputString = "{\"header\":{\"code\":\"100001\",\"desc\":\"\",\"sessionId\":\"\",\"status\":\"\",\"time\":\"\",\"userLoginId\":\"\",\"version\":\"\"},\"userLoginId\":\"admin\",\"passWord\":\"admin\"}";
		params.put("inputParams", inputString);
		
		http.get(URL_HTTP, params, new AndyHttpCallback<String>(){
			@Override
			public void onStart() {
				resultTextView.setText("正在请求数据");
			}
			@Override
			public void onSuccess(String t) {
				resultTextView.setText("get请求返回结果：\n" + t);
			}

			@Override
			public void onFailure(Throwable throwable, int errorCode,
					String errorMsg) {
				resultTextView.setText("get请求失败：\n" + errorMsg);
			}
		});
	}
	
	private void doHttpPost() {
		AndyHttp http = new AndyHttp();
		AndyRequestParams params = new AndyRequestParams();
		String inputString = "{\"header\":{\"code\":\"100001\",\"desc\":\"\",\"sessionId\":\"\",\"status\":\"\",\"time\":\"\",\"userLoginId\":\"\",\"version\":\"\"},\"userLoginId\":\"admin\",\"passWord\":\"admin\"}";
		params.put("inputParams", inputString);
		
		http.post(URL_HTTP, params, new AndyHttpCallback<String>(){
			@Override
			public void onStart() {
				resultTextView.setText("正在请求数据");
			}
			@Override
			public void onSuccess(String t) {
				resultTextView.setText("post请求返回结果：\n" + t);
			}

			@Override
			public void onFailure(Throwable throwable, int errorCode,
					String errorMsg) {
				resultTextView.setText("post请求失败：\n" + errorMsg);
			}
		});
	}
}
