package com.andy.myself.activity.tencentmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.andy.myself.R;
import com.tencent.mapsdk.raster.model.BitmapDescriptor;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.TencentMap;
import com.tencent.tencentmap.mapsdk.map.TencentMap.InfoWindowAdapter;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnInfoWindowClickListener;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMapLongClickListener;
import com.tencent.tencentmap.mapsdk.map.TencentMap.OnMarkerDraggedListener;

public class TencentMapMarkerActivity extends MapActivity implements OnMarkerDraggedListener,
/*OnMarkerClickListener,*/ OnInfoWindowClickListener {
	private MapView mapView;
	private TencentMap tencentMap;
	private Marker markerFix;
	private Marker markerLongPress;

	private CustInfoWindow custInfoWindow;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.tencent_map_activity_marker);
		init();
	}

	private void init() {
		mapView = (MapView)findViewById(R.id.map);

		tencentMap = mapView.getMap();
		tencentMap.setZoom(5);
		//		//标注点击监听
		//		mapController.setOnMarkerClickListener(this);
		//标注拖动监听
		tencentMap.setOnMarkerDraggedListener(this);
		//InfoWindow点击监听
		tencentMap.setOnInfoWindowClickListener(this);

		//自定义infoWindow
		setCustInfoWindow();
		//地图长按监听
		tencentMap.setOnMapLongClickListener(mapLongPressListener);
		addMarkers();
		//		addHundredMarkers(mapView);
		Toast toast = Toast.makeText(this, "长按地图添加Marker", Toast.LENGTH_LONG);
		toast.show();
	}

	OnMapLongClickListener mapLongPressListener = new OnMapLongClickListener() {

		@Override
		public void onMapLongClick(LatLng arg0) {
			// TODO Auto-generated method stub
			if (markerLongPress == null) {
				markerLongPress = tencentMap.addMarker(
						new MarkerOptions()
						.draggable(true)
						.icon(BitmapDescriptorFactory
								.defaultMarker())
								.position(arg0)
								.title("longPressMarker")
								.snippet(arg0.toString()));
			} else {
				markerLongPress.setPosition(arg0);
				markerLongPress.setSnippet(arg0.toString());
			}

			markerLongPress.showInfoWindow();
		}
	};

	/**
	 * 通过添加OverlayItem添加标注物
	 * @param mapView
	 */
	private void addMarkers() {
		LatLng SHANGHAI = new LatLng(31.238068, 121.501654);// 上海市经纬度

		markerFix = tencentMap.addMarker(new MarkerOptions()
		.position(SHANGHAI)
		.title("上海")
		.snippet("自定义infowindo动画")
		.anchor(0.5f, 0.5f)
		.icon(BitmapDescriptorFactory
				.defaultMarker())
				.draggable(true));
		markerFix.showInfoWindow();// 设置默认显示一个infowinfow
		//设置此marker的infowindow显示动画
		markerFix.setInfoWindowShowAnimation(R.anim.tencent_map_show_infowindow_anim);
		markerFix.setInfoWindowHideAnimation(R.anim.tencent_map_hide_infowindow_anim);
		//BitmapDescriptorFactory相关方法使用
		tencentMap.addMarker(new MarkerOptions()
		.position(new LatLng(32.01, 100))
		.icon(BitmapDescriptorFactory.fromAsset("green_location.ico"))
		.title("from asset"));

		tencentMap.addMarker(new MarkerOptions()
		.position(new LatLng(32.01, 102.0))
		.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.
				decodeResource(getResources(), R.drawable.tencent_map_route_start)))
				.title("from bitmap"));

		tencentMap.addMarker(new MarkerOptions()
		.position(new LatLng(32.01, 104.0))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.tencent_map_red_location))
		.title("from resource"));

		File file = new File(getFilesDir(), "myicon.ico");
		BitmapDescriptor bitmapDescriptor = 
				BitmapDescriptorFactory.fromAsset("green_location.ico");
		Bitmap bmp = bitmapDescriptor.getBitmap();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		FileOutputStream fos;
		try {
			fos = openFileOutput(file.getName(), MODE_PRIVATE);
			try {
				fos.write(baos.toByteArray());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tencentMap.addMarker(new MarkerOptions()
		.position(new LatLng(32.01, 106.0))
		.icon(BitmapDescriptorFactory.fromPath(getFilesDir() + "/" + file.getName()))
		.title("from path"));

		tencentMap.addMarker(new MarkerOptions()
		.position(new LatLng(32.01, 108.0))
		.icon(BitmapDescriptorFactory.fromFile(file.getName()))
		.title("from file"));
	}

	private void addHundredMarkers(MapView mapView) {
		double lat = 20.5;
		double lng = 90.955;
		LatLng latLng = new LatLng(lat, lng);
		int i = 0;
		Marker marker;
		while (i < 100) {
			marker = tencentMap.addMarker(new MarkerOptions()
			.position(latLng)
			.draggable(false));
			marker.setTitle(marker.getId());
			latLng = new LatLng(lat += 0.5, lng += 0.5);
			i++;
		}
	}


	@Override
	public void onMarkerDrag(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMarkerDragEnd(Marker arg0) {
		// TODO Auto-generated method stub
		arg0.setSnippet(arg0.getPosition().toString());
		arg0.showInfoWindow();
	}

	@Override
	public void onMarkerDragStart(Marker arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub
		arg0.setSnippet("InfoWindow Clicked!");
		arg0.showInfoWindow();
	}

	//	@Override
	//	public boolean onMarkerClick(Marker arg0) {
	//		// TODO Auto-generated method stub
	//		arg0.showInfoWindow();
	//		return false;
	//	}

	private void setCustInfoWindow() {
		custInfoWindow = new CustInfoWindow();
		tencentMap.setInfoWindowAdapter(custInfoWindow);

		LatLng ZHENGZHOU = new LatLng(34.764179,113.777710);
		LatLng NANCHANG = new LatLng(28.700225,115.978271);
		LatLng KUNMING = new LatLng(25.042060,102.711182);
		LatLng GUANGZHOU = new LatLng(23.126216,113.260253);
		Marker marker1 = tencentMap.addMarker(new MarkerOptions().position(ZHENGZHOU).title("郑州"));
		Marker marker2 = tencentMap.addMarker(new MarkerOptions().position(GUANGZHOU).title("广州"));
		Marker marker3 = tencentMap.addMarker(new MarkerOptions().position(KUNMING).title("昆明").snippet("修改marker透明度"));
		Marker marker4 = tencentMap.addMarker(new MarkerOptions().position(NANCHANG).title("南昌"));
		custInfoWindow.addMarker(marker1, 1);
		custInfoWindow.addMarker(marker2, 1);
		custInfoWindow.addMarker(marker3, 2);
		custInfoWindow.addMarker(marker4, 3);
		marker1.showInfoWindow();
		marker2.showInfoWindow();
		marker3.showInfoWindow();
		marker4.showInfoWindow();
	}

	class CustInfoWindow implements InfoWindowAdapter {

		private HashMap<Marker, InfoWindowWrapper> map;

		public CustInfoWindow() {
			// TODO Auto-generated constructor stub
			map = new HashMap<Marker, InfoWindowWrapper>();
		}

		public void addMarker(Marker marker, int type) {
			InfoWindowWrapper wrapper = new InfoWindowWrapper();
			wrapper.type = type;
			map.put(marker, wrapper);
		}

		public void removeMarker(Marker marker) {
			map.remove(marker);
		}

		public void removeAll() {
			map.clear();
		}

		class InfoWindowWrapper {
			View infoWindow;
			View infoContent;
			Object holder;
			int type;
		}

		class WindowViewHolder1 {
			LinearLayout layout;
			TextView title;
			Button button;

			public WindowViewHolder1() {
				// TODO Auto-generated constructor stub
				layout = new LinearLayout(TencentMapMarkerActivity.this);
				layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT));
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setGravity(Gravity.CENTER);

				title = new TextView(TencentMapMarkerActivity.this);
				title.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT));
				layout.addView(title);

				button = new Button(TencentMapMarkerActivity.this);
				button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT));
				layout.addView(button);
			}
		}

		class WindowViewHolder2 {
			LinearLayout layout;
			TextView title;
			TextView snippet;
			SeekBar seekBar;

			public WindowViewHolder2() {
				// TODO Auto-generated constructor stub
				layout = new LinearLayout(TencentMapMarkerActivity.this);
				layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT));
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setGravity(Gravity.CENTER);

				title = new TextView(TencentMapMarkerActivity.this);
				title.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT));
				layout.addView(title);

				snippet = new TextView(TencentMapMarkerActivity.this);
				snippet.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT));
				layout.addView(snippet);

				seekBar = new SeekBar(TencentMapMarkerActivity.this);
				seekBar.setLayoutParams(new LayoutParams(350, LayoutParams.WRAP_CONTENT));
				seekBar.setMax(100);
				seekBar.setProgress(seekBar.getMax());
				layout.addView(seekBar);
			}
		}

		class InfoViewHolder {
			LinearLayout layout;
			ImageView image;
			public InfoViewHolder() {
				// TODO Auto-generated constructor stub
				layout = new LinearLayout(TencentMapMarkerActivity.this);
				layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT));
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setGravity(Gravity.CENTER);

				image = new ImageView(TencentMapMarkerActivity.this);
				image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 
						LayoutParams.WRAP_CONTENT));
				layout.addView(image);
			}
		}

		@Override
		public View getInfoWindow(final Marker arg0) {
			// TODO Auto-generated method stub
			final InfoWindowWrapper wrapper = map.get(arg0);
			if (wrapper == null) {
				return null;
			}
			if (wrapper.type == 1) {
				if (wrapper.infoWindow == null) {
					wrapper.holder = new WindowViewHolder1();
					((WindowViewHolder1)wrapper.holder).layout.setBackgroundColor(0xff11ee33);
					((WindowViewHolder1)wrapper.holder).title.setText(arg0.getTitle());
					((WindowViewHolder1)wrapper.holder).button.setText("修改title");
					((WindowViewHolder1)wrapper.holder).button.setOnClickListener(new OnClickListener() {
						int i = 0;
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							((WindowViewHolder1)wrapper.holder).title.setText("clicked " + i++ + " times");
						}
					});
					wrapper.infoWindow = ((WindowViewHolder1)wrapper.holder).layout;
				}
				return wrapper.infoWindow;
			}
			if (wrapper.type == 2) {
				if (wrapper.infoWindow == null) {
					wrapper.holder = new WindowViewHolder2();
					((WindowViewHolder2)wrapper.holder).layout.setBackgroundColor(0xff11ee33);
					((WindowViewHolder2)wrapper.holder).title.setText(arg0.getTitle());
					((WindowViewHolder2)wrapper.holder).snippet.setText(arg0.getSnippet());
					((WindowViewHolder2)wrapper.holder).seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onProgressChanged(SeekBar seekBar, int progress,
								boolean fromUser) {
							// TODO Auto-generated method stub
							arg0.setAlpha((float)(progress)/seekBar.getMax());
						}
					});

					wrapper.infoWindow = ((WindowViewHolder2)wrapper.holder).layout;
				}
				return wrapper.infoWindow;
			}
			return null;
		}

		@Override
		public void onInfoWindowDettached(Marker arg0, View arg1) {
			// TODO Auto-generated method stub

		}

	}
}
