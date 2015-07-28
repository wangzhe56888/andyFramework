package com.andy.myself.activity.gaode;

 

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;
/**
 * 高德地图首页
 * */
public class GaodeStartActivity extends BaseHeaderActivity {
	private TextView mCurrentWeatherReportTextView;// 实时天气预报
	private TextView mFutureWeatherReportTextView;// 未来三天天气预报
	private TextView mNetLocationTextView;// 网络定位
	private TextView mMultyLocationTextView;// 混合定位
	private TextView mGpsLocationTextView;// GPS定位
	private TextView mGeoFenceTextView;// 地理围栏

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gaode_start);
//		setTitle("定位SDK " + LocationManagerProxy.getVersion());
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		mCurrentWeatherReportTextView = (TextView) findViewById(R.id.current_weather_report_text);
		mFutureWeatherReportTextView = (TextView) findViewById(R.id.future_weather_report_text);
		mNetLocationTextView = (TextView) findViewById(R.id.location_net_method_text);
		mMultyLocationTextView = (TextView) findViewById(R.id.location_multy_method_text);
		mGpsLocationTextView = (TextView) findViewById(R.id.location_gps_method_text);
		mGeoFenceTextView = (TextView) findViewById(R.id.location_geofence_method_text);
		
		mCurrentWeatherReportTextView.setOnClickListener(this);
		mFutureWeatherReportTextView.setOnClickListener(this);
		mNetLocationTextView.setOnClickListener(this);
		mMultyLocationTextView.setOnClickListener(this);
		mGpsLocationTextView.setOnClickListener(this);
		mGeoFenceTextView.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.current_weather_report_text:
			//实时天气预报
			Intent intent=new Intent(GaodeStartActivity.this,CurrentWeatherReportActivity.class);
			startActivity(intent);
			break;
		case R.id.future_weather_report_text:
			//未来三天天气预报
			Intent forcastIntent=new Intent(GaodeStartActivity.this,FutureWeatherReportActivity.class);
			startActivity(forcastIntent);
			break;
		case R.id.location_net_method_text:
			//网络定位（Wifi+基站）
			Intent netIntent=new Intent(GaodeStartActivity.this,NetLocationActivity.class);
			startActivity(netIntent);
			break;
		case R.id.location_multy_method_text:
			//混合定位
			Intent multyIntent=new Intent(GaodeStartActivity.this,MultyLocationActivity.class);
			startActivity(multyIntent);
			break;
		case R.id.location_gps_method_text:
			//GPS 定位
			Intent gpsIntent=new Intent(GaodeStartActivity.this,GPSLocationActivity.class);
			startActivity(gpsIntent);
			break;
		case R.id.location_geofence_method_text:
			//地理围栏
			Intent geoFenceIntent=new Intent(GaodeStartActivity.this,GeoFenceActivity.class);
		    startActivity(geoFenceIntent);
			break;
		}
	}
}
