package com.andy.myself.activity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

/**
 * @description: WebService测试 soap协议
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  下午5:35:44
 */
public class WebServiceActivity extends BaseHeaderActivity {
	private Button getButton;
	private TextView resultTextView;
	private EditText phoneNumET;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			getButton.setClickable(true);
			Bundle bundle = msg.getData();
			resultTextView.setText("返回结果：" + bundle.getString("result"));
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webservice);
		initView(savedInstanceState);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		titleTextView.setText(R.string.activity_webservice_title);
		
		getButton = (Button) findViewById(R.id.webservice_http_get_btn);
		resultTextView = (TextView) findViewById(R.id.webservice_result_tv);
		phoneNumET = (EditText) findViewById(R.id.webservice_phone_num_et);
		
		getButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch (v.getId()) {
		case R.id.webservice_http_get_btn:
			getButton.setClickable(false);
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
			resultTextView.setText("正在请求数据....");
			new Thread(new Runnable() {
				@Override
				public void run() {
					webserviceRequest();
				}
			}).start();
			
			break;
		default:
			break;
		}
	}
	
	private void webserviceRequest() {
		// 天气
		// 命名空间
        String nameSpace = "http://WebXml.com.cn/";  
        // 调用的方法名称  
        String methodName = "getMobileCodeInfo";
        // EndPoint
        String endPoint = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx";
        // SOAP Action
        String soapAction = "http://WebXml.com.cn/getMobileCodeInfo";  
       
  
        // 指定WebService的命名空间和调用的方法名  
        SoapObject rpc = new SoapObject(nameSpace, methodName);  
  
        String numberString = phoneNumET.getText().toString();
        
        // 设置需调用WebService接口需要传入的参数  
        rpc.addProperty("mobileCode", numberString);  
        rpc.addProperty("userId", "");
        
        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本  
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);  
  
        // 设置是否调用的是dotNet开发的WebService  
        envelope.dotNet = true;
        envelope.bodyOut = rpc;  
        // 等价于envelope.bodyOut = rpc;  
//        envelope.setOutputSoapObject(rpc);  
  
        HttpTransportSE transport = new HttpTransportSE(endPoint);  
        try {  
            // 调用WebService  
            transport.call(soapAction, envelope);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        if (envelope == null || envelope.bodyIn == null) {
			return;
		}
        // 获取返回的数据  
        SoapObject object = (SoapObject) envelope.bodyIn;  
        // 获取返回的结果  
        String result = object.getProperty(0).toString();
        // 将WebService返回的结果显示在TextView中  
        Log.e("ANDY", result);
        Bundle bundle = new Bundle();
        bundle.putString("result", result);
        Message message = new Message();
        message.setData(bundle);
        handler.sendMessage(message);
	}
}
