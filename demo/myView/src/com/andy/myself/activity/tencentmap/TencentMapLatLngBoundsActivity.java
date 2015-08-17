package com.andy.myself.activity.tencentmap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.andy.myself.R;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.LatLngBounds;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.mapsdk.raster.model.PolygonOptions;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.Projection;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapClickListener;

public class TencentMapLatLngBoundsActivity extends MapActivity {
	private TencentMap tencentMap;
	private Projection mProjection;
	private Button btnCalc;
	private Button btnGetBounds;
	private Button btnGetCustomBounds;
	private LatLngBounds mLatLngBounds;
	private LatLngBounds mCustomBounds;

	private LatLng latLng1;
	private LatLng latLng2;
	private Marker marker1;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.tencent_map_activity_latlngbounds);
		init();
	}

	private void init() {
		MapView mapView = (MapView)findViewById(R.id.map);
		mProjection = mapView.getProjection();
		tencentMap = mapView.getMap();
		btnCalc = (Button)findViewById(R.id.btn_calc);
		btnGetBounds = (Button)findViewById(R.id.btn_add_bounds);
		btnGetCustomBounds = (Button)findViewById(R.id.btn_get_custom_bounds);

		LatLng mapCenter = mapView.getMap().getMapCenter();
		double offset = 0.05; 
		final LatLng swLatLng = new LatLng(mapCenter.getLatitude() - offset, 
				mapCenter.getLongitude() - offset);
		final LatLng neLatLng = new LatLng(mapCenter.getLatitude() + offset, 
				mapCenter.getLongitude() + offset);
		//LatLng[0]为西南角，LatLng[1]为东北角
		final LatLng[] custBounds = new LatLng[2];

		btnCalc.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btnCalc.getText().toString().equals("计算距离")) {
					showToast("点击地图选取两点");
					btnCalc.setText("计算");
				} else {
					if (latLng1 == null || latLng2 == null) {
						showToast("请选点");
						return;
					}
					btnCalc.setText("计算距离");
					showToast("两点间距离为：" + mProjection.
							distanceBetween(latLng1, latLng2));
					latLng1 = null;
					latLng2 = null;
					tencentMap.clearAllOverlays();
				}

			}
		});

		btnGetBounds.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btnCalc.getText().toString().equals("计算距离")) {
					if (btnGetBounds.getText().toString().equals("获取区域")) {
						mLatLngBounds = new LatLngBounds(swLatLng, neLatLng);
						drawBounds(swLatLng, neLatLng);
						showToast("点击地图，判断所点击的点是否在区域内");
						btnGetCustomBounds.setEnabled(true);
						btnGetBounds.setText("清除区域");
					} else {
						tencentMap.clearAllOverlays();
						marker1 = null;
						custBounds[0] = null;
						custBounds[1] = null;
						btnGetCustomBounds.setEnabled(false);
						btnGetBounds.setText("获取区域");
						btnGetCustomBounds.setText("选择自定区域");
					}
				}
			}
		});

		btnGetCustomBounds.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btnGetCustomBounds.getText().toString().equals("选择自定区域")) {
					showToast("点击地图，获取包含所有点的区域，确认后判断区域间关系");
					
					btnGetCustomBounds.setText("确定");
				} else {
					if (mLatLngBounds.intersects(mCustomBounds)) {
						showToast("区域相交");
					} else if (mLatLngBounds.contains(mCustomBounds)) {
						showToast("包含自定义区域");
					} else {
						showToast("区域间无交点");
					}
					btnGetCustomBounds.setEnabled(false);
					btnGetCustomBounds.setText("选择自定区域");
				}
			}
		});

		tencentMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng latlng) {
				// TODO Auto-generated method stub
				if (btnCalc.getText().toString().equals("计算")) {
					if (latLng1 == null) {
						latLng1 = latlng;
						tencentMap.addMarker(new MarkerOptions().position(latlng));
						showToast("latLng1: " + latLng1.toString());
						return;
					} else if (latLng2 == null) {
						latLng2 = latlng;
						tencentMap.addMarker(new MarkerOptions().position(latlng));
						showToast("latLng2: " + latLng2.toString());
						return;
					}
				} else if (btnGetBounds.getText().toString().equals("清除区域")
						 && btnGetCustomBounds.getText().toString().equals("选择自定区域")) {
					if (marker1 == null) {
						marker1 = tencentMap.addMarker(new MarkerOptions().position(latlng));
					} else {
						marker1.setPosition(latlng);
					}
					if (mLatLngBounds.contains(latlng)) {
						showToast("在区域内");
					} else {
						showToast("在区域外");
					}
				} else if (btnGetCustomBounds.getText().toString().equals("确定")) {
					if (custBounds[0] == null) {
						custBounds[0] = latlng;
						showToast("选择另一点");
					} else if (custBounds[1] == null) {
						if (isLatLngInSw(custBounds[0], latlng)) {
							custBounds[1] = latlng;
						} else if (isLatLngInNe(custBounds[0], latlng)) {
							custBounds[1] = new LatLng(custBounds[0].getLatitude(), custBounds[0].getLongitude());
							custBounds[0] = latlng;
						} else if (isLatLngInSe(custBounds[0], latlng)) {
							custBounds[1] = new LatLng(latlng.getLatitude(), custBounds[0].getLongitude());
							custBounds[0] = new LatLng(custBounds[0].getLatitude(), latlng.getLongitude());
						} else if (isLatLngInNw(custBounds[0], latlng)) {
							custBounds[1] = new LatLng(custBounds[0].getLatitude(), latlng.getLongitude());
							custBounds[0] = new LatLng(latlng.getLatitude(), custBounds[0].getLongitude());
						} else {
							showToast("请不要选择相对与第一点正南、正北、正东、正西方向的点");
							return;
						}
						mCustomBounds = new LatLngBounds(custBounds[0], custBounds[1]);
						drawBounds(custBounds[0], custBounds[1]);
					}
				}

			}
		});
	}

	/**
	 * 绘制区域
	 */
	private void drawBounds(LatLng sw, LatLng ne) {
		LatLng nw = new LatLng(ne.getLatitude(), sw.getLongitude());
		LatLng se = new LatLng(sw.getLatitude(), ne.getLongitude());
		tencentMap.addPolygon(new PolygonOptions()
		.add(nw)
		.add(ne)
		.add(se)
		.add(sw)
		.strokeWidth(0f)
		.fillColor(0x550000ff));
	}

	/**
	 * 判断latLng1是否在latLng2的西南方
	 */
	private boolean isLatLngInSw(LatLng latLng1, LatLng latLng2) {
		if (latLng1.getLatitude() < latLng2.getLatitude() 
				&& latLng1.getLongitude() < latLng2.getLongitude()) {
			return true;
		}
		return false;
	}

	/**
	 * 判断latLng1是否在latLng2的东北方
	 */
	private boolean isLatLngInNe(LatLng latLng1, LatLng latLng2) {
		if (latLng1.getLatitude() > latLng2.getLatitude()
				&& latLng1.getLongitude() > latLng2.getLongitude()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断latLng1是否在latLng2的西北方
	 */
	private boolean isLatLngInNw(LatLng latLng1, LatLng latLng2) {
		if (latLng1.getLatitude() > latLng2.getLatitude()
				&& latLng1.getLongitude() < latLng2.getLongitude()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断latLng1是否在latLng2的东南方
	 */
	private boolean isLatLngInSe(LatLng latLng1, LatLng latLng2) {
		if (latLng1.getLatitude() < latLng2.getLatitude()
				&& latLng1.getLongitude() > latLng2.getLongitude()) {
			return true;
		}
		return false;
	}

	private void showToast(String msg) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
}
