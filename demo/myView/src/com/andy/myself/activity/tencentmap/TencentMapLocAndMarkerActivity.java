package com.andy.myself.activity.tencentmap;

import android.os.Bundle;
import android.util.Log;

import com.andy.myself.R;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

/**
 * @description: 定位并标注当前地理位置
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-7-20  下午12:04:31
 */
public class TencentMapLocAndMarkerActivity extends MapActivity implements TencentLocationListener {

	private final String LOG_TAG = "TENCENT";
	private MapView mapView;
	private TencentMap tencentMap;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.tencent_map_activity_marker);
		
		mapView = (MapView) findViewById(R.id.map);
		
		tencentMap = mapView.getMap();
		tencentMap.setZoom(5);
		
		
		TencentLocationRequest request = TencentLocationRequest.create();
		//设置定位周期(位置监听器回调周期), 单位为 ms (毫秒),默认10秒
		request.setInterval(1000 * 5);
		// 设置定位的 request level
		request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_NAME);
		TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
		int error = locationManager.requestLocationUpdates(request, this);
		Log.e(LOG_TAG, "注册结果 ：" + error);
		if (error != 0) {
			// 注册监听失败
		}
	}
	
	@Override
	public void onLocationChanged(TencentLocation location, int error, String reason) {
		Log.e(LOG_TAG, "定位回调");
		if (TencentLocation.ERROR_OK == error) {
			tencentMap.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
			tencentMap.setZoom(18);
	        // 定位成功
			tencentMap.addMarker(new MarkerOptions()
			.position(new LatLng(location.getLatitude(), location.getLongitude()))
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.tencent_map_red_location))
			.title(location.getAddress()));
			
			// 定位成功后删除定位监听
			TencentLocationManager locationManager = TencentLocationManager.getInstance(this);
			locationManager.removeUpdates(this);
	    } else {
	        // 定位失败
	    }
		
	}

	@Override
	public void onStatusUpdate(String arg0, int arg1, String arg2) {
		
	}
}
