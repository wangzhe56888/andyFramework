package com.andy.myself.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.framework.util.network.AndyNetworkStateChangeListener;
import com.andy.framework.util.network.AndyNetworkStateReceiver;
import com.andy.framework.util.network.AndyNetworkUtil;
import com.andy.framework.util.network.AndyNetworkUtil.NetType;
import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

/**
 * @description: 网络状态相关测试
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-9  下午1:56:58
 */
public class NetWorkStateActivity extends BaseHeaderActivity {
	
	private Button refreshBtn;
	private TextView networkStateTV;
	
	private AndyNetworkStateChangeListener stateChangeListener = new AndyNetworkStateChangeListener(){
		@Override
		public void onConnect(NetType type) {
			Toast.makeText(NetWorkStateActivity.this, "网络已连接：" + type, Toast.LENGTH_LONG).show();
			getNetworkStateMsg();
		}
		
		@Override
		public void onDisConnect() {
			Toast.makeText(NetWorkStateActivity.this, "网络连接已断开", Toast.LENGTH_LONG).show();
			networkStateTV.setText("网络连接已断开");
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		
		refreshBtn = (Button) findViewById(R.id.network_refresh_btn);
		networkStateTV = (TextView) findViewById(R.id.network_state_tv);
		
		refreshBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getNetworkStateMsg();
			}
		});
		getNetworkStateMsg();
		AndyNetworkStateReceiver.registerLisener(stateChangeListener);
	}
	
	
	private void getNetworkStateMsg() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("当前网络信息：\n");
		
		/**
		 * 当前网络状态
		 * WIFI ： WiFi连接
		 * CMNET ：net连接
		 * CMWAP ：WAP
		 * NONENET ：无网络连接
		 * */
		stringBuffer.append("网络连接类型（自定义类型）：").append(AndyNetworkUtil.getAPNType(this));
		
		networkStateTV.setText(stringBuffer.toString());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AndyNetworkStateReceiver.unRegisterLisener(stateChangeListener);
	}
}
