package com.andy.myself.activity.tencentmap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.andy.myself.R;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.CircleOptions;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;

public class TencentMapMarkerAttributeActivity extends MapActivity {

	public MapView mMapView;
	public TencentMap tencentMap;
	
	private Marker marker;
	
	private SeekBar bearingSetter;
	private SeekBar anchorUSetter;
	private SeekBar anchorVSetter;

	private float anchorU = 0;
	private float anchorV = 0;
	
	private static final LatLng NE = new LatLng(39.890000, 116.350777);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tencent_map_activity_marker_attr);
		mMapView = (MapView) findViewById(R.id.mapviewOverlay);
		tencentMap = mMapView.getMap();
		bearingSetter = (SeekBar) findViewById(R.id.bearing);
		anchorUSetter = (SeekBar) findViewById(R.id.anchoru);
		anchorVSetter = (SeekBar) findViewById(R.id.anchorv);

		tencentMap.setZoom(9);

		tencentMap.addCircle(new CircleOptions().center(NE).radius(5));
		marker = tencentMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.tencent_map_red_location)).position(NE));
		
		bearingSetter.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				marker.setRotation(progress);
			}
		});

		anchorUSetter.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				anchorU = progress * 1.0f / 360;
				marker.setAnchor(anchorU, anchorV);
			}
		});

		anchorVSetter.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				anchorV = progress * 1.0f / 360;
				marker.setAnchor(anchorU, anchorV);
			}
		});

		Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tencentMap.clearAllOverlays();
			}
		});
	}
}
