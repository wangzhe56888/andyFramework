package com.andy.myself.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.framework.http.AndyHttp;
import com.andy.framework.http.AndyHttpCallback;
import com.andy.framework.http.AndyRequestParams;
import com.andy.framework.util.network.AndyNetworkStateChangeListener;
import com.andy.framework.util.network.AndyNetworkStateReceiver;
import com.andy.framework.util.network.AndyNetworkUtil;
import com.andy.framework.util.network.AndyNetworkUtil.NetType;
import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;
import com.andy.myself.base.DefineProgressDialog;
import com.andy.myself.util.PromptUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * @description: 百度地理位置获取
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-17  下午2:33:23
 * 使用时需要在manifest文件中配置
 * <service
	    android:name="com.baidu.location.f"
	    android:enabled="true"
	    android:process=":remote" />
	    
	需要的权限：
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
	<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
	<uses-permission android:name="android.permission.READ_LOGS"/>
 */
public class LocationBaiduActivity extends BaseHeaderActivity {
	private Button refreshButton, commitButton;
	private TextView networkStateTV, locationStateTV, deviceIdTV;
	private DefineProgressDialog mProgress;
	
	private LocationBean locationBean;
	private LocationListenner locationListenner = new LocationListenner();
	private LocationClient mLocClient = null;
	
	private String deviceId = "";
	
	private AndyNetworkStateChangeListener stateChangeListener = new AndyNetworkStateChangeListener(){
		@Override
		public void onConnect(NetType type) {
			getNetworkStateMsg();
		}
		
		@Override
		public void onDisConnect() {
			networkStateTV.setTextColor(getResources().getColor(R.color.red));
			networkStateTV.setText("网络状态：已断开！");
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		
		// 获取设备号
		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
		deviceId = tm.getDeviceId();
		
		networkStateTV = (TextView) findViewById(R.id.network_state_tv);
		locationStateTV = (TextView) findViewById(R.id.location_state_tv);
		deviceIdTV = (TextView) findViewById(R.id.device_id_tv);
		
		refreshButton = (Button) findViewById(R.id.location_refresh_btn);
		commitButton = (Button) findViewById(R.id.location_commit_btn);
		
		deviceIdTV.setText("设备唯一标识：" + deviceId);
		
		refreshButton.setOnClickListener(this);
		commitButton.setOnClickListener(this);
		
		
		mProgress = new DefineProgressDialog(mActivity);
		mProgress.setCanceledOnTouchOutside(false);
		
		AndyNetworkStateReceiver.registerLisener(stateChangeListener);
		getNetworkStateMsg();
	}
	
	private void getNetworkStateMsg() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("网络状态： ");
		if (AndyNetworkUtil.getAPNType(this) == NetType.NONENET) {
			networkStateTV.setTextColor(getResources().getColor(R.color.red));
			stringBuffer.append("已断开！");
		} else {
			networkStateTV.setTextColor(getResources().getColor(R.color.content_text_color));
			stringBuffer.append(AndyNetworkUtil.getAPNType(this));
		}
		networkStateTV.setText(stringBuffer.toString());
		
		refreshLocation();
	}
	
	@Override
	protected void onStop() {
		mLocClient.stop();
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AndyNetworkStateReceiver.unRegisterLisener(stateChangeListener);
		mProgress.dismiss();
		mProgress.cancel();
		mProgress = null;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.location_refresh_btn:
			refreshLocation();
			break;
		case R.id.location_commit_btn:
			commitLocation();
			break;
		default:
			break;
		}
	}
	
	private void refreshLocation() {
		mProgress.setTipMessage("正在加载...");
		mProgress.show();
		
		mLocClient = new LocationClient(getApplicationContext());
		mLocClient.setAK("930718c68b27abddecabc605ba32825e");
		mLocClient.registerLocationListener(locationListenner);
		// 定位参数设置
		LocationClientOption option = new LocationClientOption();
		// 打开GPS
		option.setOpenGps(true);
		// 设置返回的坐标类型  返回国测局经纬度坐标系：gcj02 返回百度墨卡托坐标系 ：bd09 返回百度经纬度坐标系 ：bd09ll
		option.setCoorType("bd09ll");
		// 返回的定位结果包含地址信息， 如果不设置，location.getAddrStr();返回为null
		option.setAddrType("all"); 
		// 最多返回POI个数
		option.setPoiNumber(10);
		// 禁止启用缓存定位
		option.disableCache(true);
		// 设置发起定位请求的间隔时间
		option.setScanSpan(1000 * 60 * 5);
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setPoiDistance(1000); //poi查询距离
		option.setPoiExtraInfo(true); //是否需要POI的电话和地址等详细信息
		
		mLocClient.setLocOption(option);
		mLocClient.start();
		mLocClient.requestLocation();
	}
	
	private void commitLocation() {
		if (AndyNetworkUtil.getAPNType(this) == NetType.NONENET) {
			PromptUtil.showToast(mActivity, "未连接网络！");
			return;
		}
		if (locationBean == null) {
			PromptUtil.showToast(mActivity, "没有定位信息，请重新定位！");
			return;
		}
		
		String url_http = "http://www.hypt.net.cn/Rese/Qpage/testConnect.do";
		AndyHttp http = new AndyHttp();
		AndyRequestParams params = new AndyRequestParams();
		params.put("address", locationBean.address);
		params.put("latitude", String.valueOf(locationBean.latitude));
		params.put("longitude", String.valueOf(locationBean.longitude));
		params.put("deviceId", deviceId);
		
		http.post(url_http, params, new AndyHttpCallback<String>(){
			@Override
			public void onStart() {
				mProgress.setTipMessage("正在提交...");
				mProgress.show();
			}
			@Override
			public void onSuccess(String t) {
				mProgress.hide();
				PromptUtil.showToast(mActivity, "定位信息提交成功！");
				Log.e(LOG_TAG, "返回结果：" + t);
			}
			@Override
			public void onFailure(Throwable throwable, int errorCode, String errorMsg) {
				mProgress.hide();
				PromptUtil.showToast(mActivity, "提交失败，请重新提交！");
			}
		});
	}
	
	class LocationBean {
		public String province;
		public String city;
		public String area;
		public String address;
		public double latitude;
		public double longitude;
	}
	
	public class LocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			locationBean = new LocationBean();
			locationBean.latitude = location.getLatitude();
			locationBean.longitude = location.getLongitude();
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				locationBean.province = location.getProvince();
				locationBean.city = location.getCity();
				locationBean.area = location.getDistrict();
				locationBean.address = location.getAddrStr() + location.getPoi();
			}
			
			mProgress.hide();
			if (locationBean != null) {
				StringBuffer stringBuffer = new StringBuffer("定位结果：\n");
				stringBuffer
//				.append(locationBean.province)
//				.append(locationBean.city)
//				.append(locationBean.area)
				.append(locationBean.address).append("\n")
				.append("纬度：").append(locationBean.latitude).append("\n")
				.append("经度：").append(locationBean.longitude).append("\n")
				;
				locationStateTV.setText(stringBuffer.toString());
			}
		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			Log.e(LOG_TAG, "onReceivePoi 。。。 ");
			if (poiLocation == null) {
				return;
			}
//			poiLocation.getTime();  
//	        poiLocation.getLocType();  
//	        poiLocation.getLatitude();  
//	        poiLocation.getLongitude();  
//	        poiLocation.getRadius();  
//	        if (poiLocation.getLocType() == BDLocation.TypeGpsLocation){//卫星定位  
//	            poiLocation.getSpeed();  
//	            poiLocation.getSatelliteNumber();  
//	        } else if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation){//网络定位  
//	            poiLocation.getAddrStr();//LocationClientOption.setAddrType("all");需要设置  
//	        }  
		}
	}
}
