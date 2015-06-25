package com.andy.myself.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andy.framework.util.AndyValidateUtil;
import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;

/**
 * @description: 验证测试
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-9  下午4:45:57
 */
public class ValidateActivity extends BaseActivity {
	
	private TextView resultTextView;
	private EditText validaText;
	private Button mailBtn, mobileBtn, urlBtn, passwordBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_validate);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		
		resultTextView = (TextView) findViewById(R.id.validate_result_tv);
		validaText = (EditText) findViewById(R.id.validate_text_ET);
		
		mailBtn = (Button) findViewById(R.id.validate_email_btn);
		mobileBtn = (Button) findViewById(R.id.validate_mobile_btn);
		urlBtn = (Button) findViewById(R.id.validate_url_btn);
		passwordBtn = (Button) findViewById(R.id.validate_password_btn);
		
		
		mailBtn.setOnClickListener(this);
		mobileBtn.setOnClickListener(this);
		urlBtn.setOnClickListener(this);
		passwordBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		String string = validaText.getText().toString();
		switch (v.getId()) {
			case R.id.validate_email_btn:
				setResultText(AndyValidateUtil.validateEmail(string));
				break;
			case R.id.validate_mobile_btn:
				setResultText(AndyValidateUtil.validateMobileNumber(string));
				break;
			case R.id.validate_url_btn:
				setResultText(AndyValidateUtil.validateURL(string));
				break;
			case R.id.validate_password_btn:
				setResultText(AndyValidateUtil.validatePassword(string));
				break;
			default:
				super.onClick(v);
		}
	}
	
	private void setResultText(boolean result) {
		if (result) {
			resultTextView.setText("验证结果：true");
		} else {
			resultTextView.setText("验证结果：false");
		}
	}
}
