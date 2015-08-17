package com.andy.myself.activity.tencentmap;


import android.os.Bundle;

import com.andy.myself.R;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

public class TencentMapBasicActivity extends MapActivity {
	MapView mMapView;
	TencentMap tencentMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tencent_map_activity_basic_map);
		mMapView = (MapView)findViewById(R.id.map);
	}
}