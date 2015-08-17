package com.andy.myself.activity.tencentmap;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.CameraPosition;
import com.tencent.mapsdk.raster.model.Circle;
import com.tencent.mapsdk.raster.model.CircleOptions;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.mapsdk.raster.model.Polygon;
import com.tencent.mapsdk.raster.model.PolygonOptions;
import com.tencent.mapsdk.raster.model.Polyline;
import com.tencent.mapsdk.raster.model.PolylineOptions;
import com.tencent.tencentmap.mapsdk.map.CancelableCallback;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.QSupportMapFragment;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapCameraChangeListener;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapClickListener;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapLoadedListener;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapLongClickListener;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMarkerDraggedListener;

public class TencentMapFragmentActivity extends FragmentActivity{
	private QSupportMapFragment qMapFragment;
	private MapView mapView;
	private TencentMap tencnetMap;
	private TextView tvMonitor;
	private Button btnAnimate;
	private Button btnMarker;
	private Button btnGeometry;
	private LinearLayout mainLayout;
	private FrameLayout mapFrame;
	
	private LatLng mZhongGuanCun;
	private LatLng mLatLng;
	private Marker mMarker;
	private Polyline mPolyline;
	private Polygon mpPolygon;
	private Circle mCircle;
	int id = 0x7f071001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mainLayout = new LinearLayout(this);
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(0xffffffff);
		mainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		setContentView(mainLayout);
		init();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mapView == null) {
			mapView = qMapFragment.getMapView();
			tencnetMap = mapView.getMap();
		}
		bindLinstener();
		
		Log.e("get zoom level", Integer.toString(tencnetMap.getZoomLevel()));
	}
	
	private void init() {
		LinearLayout lineOne = new LinearLayout(this);
		lineOne.setOrientation(LinearLayout.HORIZONTAL);
		mainLayout.addView(lineOne);
		
		btnAnimate = new Button(this);
		btnAnimate.setText("移动到中关村");
		lineOne.addView(btnAnimate);
		
		btnMarker = new Button(this);
		btnMarker.setText("添加Marker");
		lineOne.addView(btnMarker);
		
		btnGeometry = new Button(this);
		btnGeometry.setText("添加图形");
		lineOne.addView(btnGeometry);
		
		tvMonitor = new TextView(this);
		tvMonitor.setTextColor(0xff000000);
		tvMonitor.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mainLayout.addView(tvMonitor);
		
		if (qMapFragment != null) {
			return;
		}
		qMapFragment = QSupportMapFragment.newInstance();
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		mapFrame = new FrameLayout(this);
		
		mapFrame.setId(id);
		fragmentTransaction.add(id, qMapFragment);
		fragmentTransaction.commit();
		mainLayout.addView(mapFrame);
	}
	
	private void bindLinstener() {
		final LatLng latLng1 = new LatLng(39.925961,116.388171);
		final LatLng latLng2 = new LatLng(39.735961,116.488171);
		final LatLng latLng3 = new LatLng(39.635961,116.268171);

		final PolylineOptions lineOpt = new PolylineOptions();
		lineOpt.add(latLng1);
		lineOpt.add(latLng2);
		lineOpt.add(latLng3);
		
		final LatLng latLng4 = new LatLng(39.935961,116.388171);
		final LatLng latLng5 = new LatLng(40.035961,116.488171);
		final LatLng latLng6 = new LatLng(40.095961,116.498171);
		final LatLng latLng7 = new LatLng(40.135961,116.478171);
		final LatLng latLng8 = new LatLng(40.095961,116.398171);
		final PolygonOptions polygonOp = new PolygonOptions();
		polygonOp.fillColor(0x55000077);
		polygonOp.strokeWidth(4);
		polygonOp.add(latLng4);
		polygonOp.add(latLng5);
		polygonOp.add(latLng6);
		polygonOp.add(latLng7);
		polygonOp.add(latLng8);
		
		final LatLng latLng9 = new LatLng(39.735961,116.788171);
		final CircleOptions circleOp = new CircleOptions();
		circleOp.center(latLng9);
		circleOp.radius(5000);
		circleOp.strokeColor(0xff0000ff);
		circleOp.strokeWidth(5);
		circleOp.fillColor(0xff00ff00);
		mZhongGuanCun = new LatLng(39.980484, 116.311302);//中关村
		
		btnAnimate.setOnClickListener(new OnClickListener() {
			
			CancelableCallback callback = new CancelableCallback() {
				
				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					btnAnimate.setText("移动到中关村");
				}
				
				@Override
				public void onCancel() {
					// TODO Auto-generated method stub
					btnAnimate.setText("移动到中关村");
				}
			};
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btnAnimate.getText().toString().equals("移动到中关村")) {
					tencnetMap.animateTo(mZhongGuanCun/*, 4000, callback*/);
					btnAnimate.setText("停止动画");
				} else {
					tencnetMap.stopAnimation();
				}
			}
		});
		
		btnMarker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btnMarker.getText().toString().equals("添加Marker")) {
					mLatLng = new LatLng(39.984122, 116.307484);
					mMarker = tencnetMap.addMarker(new MarkerOptions()
					.icon(BitmapDescriptorFactory.defaultMarker())
					.position(mLatLng)
					.draggable(true));
					
					btnMarker.setText("删除Marker");
				} else {
					mMarker.remove();
					btnMarker.setText("添加Marker");
				}
				
			}
		});
		
		btnGeometry.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (btnGeometry.getText().toString().equals("添加图形")) {
					mpPolygon = tencnetMap.addPolygon(polygonOp);
					mPolyline = tencnetMap.addPolyline(lineOpt);
					mCircle = tencnetMap.addCircle(circleOp);
					btnGeometry.setText("删除图形");
				} else {
					mpPolygon.remove();
					mPolyline.remove();
					mCircle.remove();
					btnGeometry.setText("添加图形");
				}
				
			}
		});
		tencnetMap.setOnMapLoadedListener(new OnMapLoadedListener() {
			
			@Override
			public void onMapLoaded() {
				// TODO Auto-generated method stub
				Log.e("Listener", "Map Loaded");
			}
		});
		tencnetMap.setOnMapCameraChangeListener(new OnMapCameraChangeListener() {
			
			@Override
			public void onCameraChangeFinish(CameraPosition arg0) {
				// TODO Auto-generated method stub
				tvMonitor.setText( "Camera Change Finish:" + 
						"Target:" + arg0.getTarget().toString() + 
						"zoom level:" + arg0.getZoom());
				btnAnimate.setText("移动到中关村");
			}
			
			@Override
			public void onCameraChange(CameraPosition arg0) {
				// TODO Auto-generated method stub
				Log.e("Listener", "Camera Change");
			}
		});
		tencnetMap.setOnMapClickListener(new OnMapClickListener() {
			
			@Override
			public void onMapClick(LatLng arg0) {
				// TODO Auto-generated method stub
				Log.e("Listener", "Map Click");
			}
		});
		tencnetMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				Log.e("Listener", "Map Long Press");
			}
		});
		
		tencnetMap.setOnMarkerDraggedListener(new OnMarkerDraggedListener() {

			@Override
			public void onMarkerDrag(Marker arg0) {
				// TODO Auto-generated method stub
				tvMonitor.setText("Marker Dragging");
			}

			@Override
			public void onMarkerDragEnd(Marker arg0) {
				// TODO Auto-generated method stub
				tvMonitor.setText("Marker Drag End");
			}

			@Override
			public void onMarkerDragStart(Marker arg0) {
				// TODO Auto-generated method stub
				tvMonitor.setText("Marker Drag Start");
			}
		});
	}
}
