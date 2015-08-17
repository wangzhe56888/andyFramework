package com.andy.myself.activity.tencentmap;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.myself.R;
import com.tencent.mapsdk.raster.model.CameraPosition;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapCameraChangeListener;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapClickListener;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapLoadedListener;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapLongClickListener;
import com.tencent.tencentmap.mapsdk.map.UiSettings;

public class TencentMapControlActivity extends MapActivity implements 
OnMapCameraChangeListener, OnMapLoadedListener, OnMapClickListener, OnMapLongClickListener{
	MapView mapView;
	CheckBox cbSatellite;
	CheckBox cbScale;
	EditText etZoomLevel;
	LinearLayout linearLayout;
	LinearLayout llLog;
	Button btnLb;
	Button btnRb;
	Button btnRt;
	Button btnLt;
	Button btnCb;
	Button btnCt;
	Button btnLbs;
	Button btnRbs;
	Button btnCbs;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.tencent_map_activity_control_map);
		init();
	}

	private void init() {
		linearLayout = (LinearLayout)findViewById(R.id.ll_panel);
		mapView = (MapView)findViewById(R.id.map);
		cbSatellite = (CheckBox)findViewById(R.id.cb_satelite);
		cbScale = (CheckBox)findViewById(R.id.cb_scale);
		etZoomLevel = (EditText)findViewById(R.id.et_zoom);
		llLog = (LinearLayout)findViewById(R.id.ll_showLog);
		btnLb = (Button)findViewById(R.id.lb);
		btnRb = (Button)findViewById(R.id.rb);
		btnRt = (Button)findViewById(R.id.rt);
		btnLt = (Button)findViewById(R.id.lt);
		btnCb = (Button)findViewById(R.id.cb);
		btnCt = (Button)findViewById(R.id.ct);
		btnLbs = (Button)findViewById(R.id.lbs);
		btnRbs = (Button)findViewById(R.id.rbs);
		btnCbs = (Button)findViewById(R.id.cbs);
		
		final InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		final TencentMap tencentMap = mapView.getMap();

		etZoomLevel.setHint(new String("缩放级别" + 
				tencentMap.getMinZoomLevel() + "-" + tencentMap.getMaxZoomLevel()));

		tencentMap.setSatelliteEnabled(false);

		tencentMap.setOnMapCameraChangeListener(this);
		tencentMap.setOnMapLoadedListener(this);
		tencentMap.setOnMapClickListener(this);
		tencentMap.setOnMapLongClickListener(this);

		//卫星图开关
		cbSatellite.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				mapView.getMap().setSatelliteEnabled(isChecked);
			}
		});
		
		//内置比例尺开关
		cbScale.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				mapView.getUiSettings().setScaleControlsEnabled(isChecked);
			}
		});

		linearLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				etZoomLevel.clearFocus();
				return false;
			}
		});

		
		//设置缩放级别
		etZoomLevel.setOnFocusChangeListener(new OnFocusChangeListener() {
			int zoomLevel = 0;

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {
					imm.hideSoftInputFromWindow(etZoomLevel.getWindowToken(), 0);
					try {
						zoomLevel = Integer.valueOf(etZoomLevel.getText().toString());
					} catch (NumberFormatException e) {
						// TODO: handle exception
						Toast toast = Toast.makeText(TencentMapControlActivity.this, 
								"缩放级别应为" + tencentMap.getMinZoomLevel() + "~" + tencentMap.getMaxZoomLevel() + "的整数",
								Toast.LENGTH_SHORT);
						toast.show();

					}
					if (zoomLevel < tencentMap.getMinZoomLevel() || 
							zoomLevel > tencentMap.getMaxZoomLevel()) {
						Toast toast = Toast.makeText(TencentMapControlActivity.this, 
								"缩放级别应为" + tencentMap.getMinZoomLevel() + "~" + tencentMap.getMaxZoomLevel() + "的整数",
								Toast.LENGTH_SHORT);
						toast.show();
						return;
					}
					tencentMap.setZoom(zoomLevel);
				}
			}
		});
		
		//设置地图logo和比例尺的位置
		OnClickListener positionClickListener = new OnClickListener() {
			UiSettings uiSettings = mapView.getUiSettings();
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.lb:
					uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_LEFT_BOTTOM);
					break;
				case R.id.cb:
					uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_CENTER_BOTTOM);
					break;
				case R.id.rb:
					uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_RIGHT_BOTTOM);
					break;
				case R.id.lt:
					uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_LEFT_TOP);
					break;
				case R.id.ct:
					uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_CENTER_TOP);
					break;
				case R.id.rt:
					uiSettings.setLogoPosition(UiSettings.LOGO_POSITION_RIGHT_TOP);
					break;
				case R.id.lbs:
					uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_LEFT_BOTTOM);
					break;
				case R.id.cbs:
					uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_CENTER_BOTTOM);
					break;
				case R.id.rbs:
					uiSettings.setScaleViewPosition(UiSettings.SCALEVIEW_POSITION_RIGHT_BOTTOM);
					break;
				default:
					break;
				}
			}
		};
		btnLb.setOnClickListener(positionClickListener);
		btnCb.setOnClickListener(positionClickListener);
		btnRb.setOnClickListener(positionClickListener);
		btnLt.setOnClickListener(positionClickListener);
		btnCt.setOnClickListener(positionClickListener);
		btnRt.setOnClickListener(positionClickListener);
		btnLbs.setOnClickListener(positionClickListener);
		btnCbs.setOnClickListener(positionClickListener);
		btnRbs.setOnClickListener(positionClickListener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapView.getUiSettings().setScaleControlsEnabled(false);
	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
		addLogToView("Map Loaded");
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub
		addLogToView("Camera Chaging");
	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		// TODO Auto-generated method stub
		addLogToView("Camera Change Finish");
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		if (etZoomLevel.isFocused()) {
			//点击地图时，清除editZoomLevel焦点
			etZoomLevel.clearFocus();
		}
		addLogToView("Map Clicked");
	}

	public void addLogToView(String log) {
		TextView textView = new TextView(this);
		textView.setTextColor(0xff000000);
		textView.setText(log);
		llLog.addView(textView);
		if (llLog.getChildCount() > 8) {
			llLog.removeViewAt(0);
		}
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		// TODO Auto-generated method stub
		addLogToView("Map Long Pressed");
	}
}