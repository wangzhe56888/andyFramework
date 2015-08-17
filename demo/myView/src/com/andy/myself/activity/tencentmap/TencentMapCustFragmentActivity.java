package com.andy.myself.activity.tencentmap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.andy.myself.R;
import com.tencent.tencentmap.mapsdk.map.MapView;

public class TencentMapCustFragmentActivity extends FragmentActivity {
	MapView mapView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tencent_map_activity_cust_mapfragment);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		TencentMapCustFragment mapFragment = (TencentMapCustFragment)fragmentManager.findFragmentById(R.id.map_fragment);
	}
	
	
}
