package com.andy.myself.activity.gaode;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.mapcore.q;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.andy.myself.R;

/**
 * 混合定位示例
 * */
public class MultyLocationActivity extends Activity implements LocationSource,
		AMapLocationListener, OnCheckedChangeListener {
	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private RadioGroup mGPSModeGroup;

	private TextView locationTextView;
	
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 不显示程序的标题栏
		setContentView(R.layout.activity_multy_location);
		
		mContext = this;
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		mGPSModeGroup = (RadioGroup) findViewById(R.id.gps_radio_group);
		mGPSModeGroup.setOnCheckedChangeListener(this);
		
		locationTextView = (TextView) findViewById(R.id.location_text);
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		aMap.setLocationSource(this);// 设置定位监听
		aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		aMap.moveCamera(CameraUpdateFactory.zoomTo(18)); // 设置缩放级别
		aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.gps_locate_button:
			// 设置定位的类型为定位模式
			aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
			break;
		case R.id.gps_follow_button:
			// 设置定位的类型为 跟随模式
			aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
			break;
		case R.id.gps_rotate_button:
			// 设置定位的类型为根据地图面向方向旋转
			aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
			break;
		}

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
				
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("当前位置：");
//				stringBuffer.append(amapLocation.getProvince());
//				stringBuffer.append(amapLocation.getCity());
				stringBuffer.append(amapLocation.getAddress());
				stringBuffer.append("\nPOI:").append(amapLocation.getPoiName());
				locationTextView.setText(stringBuffer.toString());
				
				
				// 附近POI搜索
				// 第一个参数表示搜索字符串，第二个参数表示POI搜索类型，二选其一
				// 第三个参数表示POI搜索区域的编码，必设
				final PoiSearch.Query query = new PoiSearch.Query("", "", amapLocation.getCityCode());
				query.setPageSize(5);
				query.setPageNum(0);
				
				final PoiSearch poiSearch = new PoiSearch(mContext, query);
				
				LatLonPoint latLonPoint = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
				
				poiSearch.setBound(new SearchBound(latLonPoint, 1000));
				
				poiSearch.setOnPoiSearchListener(new OnPoiSearchListener() {
					@Override
					public void onPoiSearched(PoiResult result, int rCode) {
						if (rCode != 0) {
							return;
						}
						if (result == null || result.getQuery() == null) {
							return;
						}
						// 是否是同一条
						if (result.getQuery().equals(query)) {
							List<PoiItem> poiItems = result.getPois();
							
							StringBuffer stringBuffer = new StringBuffer("\n附近POI：");
							for (PoiItem item : poiItems) {
								//Poi深度搜索
//								poiSearch.searchPOIDetailAsyn(item.getPoiId());
								stringBuffer.append(item.toString()).append(" ; ");
							}
							
							String text = locationTextView.getText().toString();
							locationTextView.setText(text + stringBuffer.toString());
							
//							if (poiItems != null && poiItems.size() > 0) {
//			                    aMap.clear();//清理之前的图标
//			                    PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
//			                    poiOverlay.removeFromMap();
//			                    poiOverlay.addToMap();
//			                    poiOverlay.zoomToSpan();
//			                }
						}
					}
					
					@Override
					public void onPoiItemDetailSearched(PoiItemDetail poiItemDetail, int arg1) {
//						String detailString = "";
//						if (arg1 == 0) {
//							detailString = "\n附近POI：";
//						}
//						detailString += poiItemDetail.getAdName();
//						String text = locationTextView.getText().toString();
//						locationTextView.setText(text + detailString);
					}
				});
				poiSearch.searchPOIAsyn();
			} else {
				Log.e("AmapErr","Location ERR:" + amapLocation.getAMapException().getErrorCode());
				locationTextView.setText("定位失败");
			}
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			// 在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
			mAMapLocationManager.requestLocationData(
					LocationProviderProxy.AMapNetwork, 60 * 1000, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

}
