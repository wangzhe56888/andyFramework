package com.andy.myself.activity.tencentmap;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andy.myself.R;
import com.tencent.mapsdk.raster.model.GeoPoint;
import com.tencent.tencentmap.mapsdk.map.ItemizedOverlay;
import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;
import com.tencent.tencentmap.mapsdk.map.OverlayItem;
import com.tencent.tencentmap.mapsdk.map.Projection;

public class TencentMapItemizedOverlayActivity extends MapActivity {

	private MapView mapView;
	private ViewGroup custInfowindow;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		init();
		
	}
	
	protected void init() {
		setContentView(R.layout.tencent_map_activity_itemized_overlay);
		mapView = (MapView)findViewById(R.id.map);
		Drawable drawable = getResources().getDrawable(R.drawable.tencent_map_red_location);
		final int y = drawable.getIntrinsicHeight();
		drawable.setBounds(0, 0, 
				drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		TestOverlay testOverlay = new TestOverlay(drawable, this);

		custInfowindow = setCustInfowindow();
		testOverlay.setOnTapListener(new OnTapListener() {

			TextView title = (TextView)custInfowindow.findViewById(1);
			TextView snippet = (TextView)custInfowindow.findViewById(2);
			
			@Override
			public void onTap(OverlayItem itemTap) {
				// TODO Auto-generated method stub
				if (custInfowindow == null || itemTap == null) {
					return;
				}
				MapView.LayoutParams lp = new MapView.LayoutParams(
						MapView.LayoutParams.WRAP_CONTENT, 
						MapView.LayoutParams.WRAP_CONTENT, 
						itemTap.getPoint(), 
						0, -y, MapView.LayoutParams.BOTTOM_CENTER);
				title.setText(itemTap.getTitle());
				snippet.setText(itemTap.getSnippet());
				if (mapView.indexOfChild(custInfowindow) == -1) {
					mapView.addView(custInfowindow, lp);
				} else {
					mapView.updateViewLayout(custInfowindow, lp);
				}
			}
			
			@Override
			public void onEmptyTap(GeoPoint pt) {
				// TODO Auto-generated method stub
				if (mapView.indexOfChild(custInfowindow) >= 0) {
					mapView.removeView(custInfowindow);
				}
			}
		});
		mapView.addOverlay(testOverlay);
		
	}
	
	protected ViewGroup setCustInfowindow() {
		LayoutParams lp = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LinearLayout root = new LinearLayout(this);
		root.setBackgroundDrawable(getResources().getDrawable(R.drawable.tencent_map_infowindow_bg));
		root.setLayoutParams(lp);
		root.setOrientation(LinearLayout.VERTICAL);
		root.setGravity(Gravity.CENTER);
		TextView tvTitle = new TextView(this);
		tvTitle.setId(1);
		tvTitle.setLayoutParams(lp);
		tvTitle.setGravity(Gravity.CENTER);
		tvTitle.setTextColor(0xff000000);
		TextView tvSnipeet = new TextView(this);
		tvSnipeet.setLayoutParams(lp);
		tvSnipeet.setId(2);
		tvSnipeet.setGravity(Gravity.CENTER);
		tvSnipeet.setTextColor(0xff000000);
		root.addView(tvTitle);
		root.addView(tvSnipeet);
		return root;
	}
	

	interface OnTapListener {
		public void onTap(OverlayItem itemTap);
		public void onEmptyTap(GeoPoint pt);
	}
	
	class TestOverlay extends ItemizedOverlay<OverlayItem> {

		private List<OverlayItem> overlayItems;
		private OnTapListener onTapListener;
		
		public TestOverlay(Drawable drawable, Context context) {
			// TODO Auto-generated constructor stub
			super(boundCenterBottom(drawable));
			overlayItems = new ArrayList<OverlayItem>();
			GeoPoint gp1 = new GeoPoint(39911766, 116305456);
			GeoPoint gp2 = new GeoPoint(40118165, 116170304);
			GeoPoint gp3 = new GeoPoint(39794996, 116546586);
			
			overlayItems.add(new OverlayItem(gp1, "39.911766, 116.305456", "snippet"));
			overlayItems.add(new OverlayItem(gp2, "40.118165, 116.170304", "snippet"));
			OverlayItem item = new OverlayItem(gp3, "39.794996, 116.546586", "可拖动");
			item.setDragable(true);
			overlayItems.add(item);
			populate();
		}

		@Override
		protected OverlayItem createItem(int arg0) {
			// TODO Auto-generated method stub
			return overlayItems.get(arg0);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return overlayItems.size();
		}
		
		@Override
		public void draw(Canvas arg0, MapView arg1) {
			// TODO Auto-generated method stub
			super.draw(arg0, arg1);
			Projection projection = arg1.getProjection();
			Paint paint = new Paint();
			paint.setColor(0xff000000);
			paint.setTextSize(15);
			float width;
			float textHeight = paint.measureText("Yy");
			for (int i = 0; i < overlayItems.size(); i++) {
				Point point = new Point();
				projection.toPixels(overlayItems.get(i).getPoint(), point);
				width = paint.measureText(Integer.toString(i));
				arg0.drawText(Integer.toString(i), 
						point.x - width / 2, point.y + textHeight, paint);
			}
			
		}
		
		@Override
		protected boolean onTap(int arg0) {
			// TODO Auto-generated method stub
			OverlayItem  overlayItem = overlayItems.get(arg0);
			setFocus(overlayItem);
			if (onTapListener != null) {
				onTapListener.onTap(overlayItem);
			}
			return true;
		}
		
		@Override
		public void onEmptyTap(GeoPoint arg0) {
			// TODO Auto-generated method stub
			if (onTapListener != null) {
				onTapListener.onEmptyTap(arg0);
			}
		}
		
		public void setOnTapListener(OnTapListener onTapListener) {
			this.onTapListener = onTapListener;
		}
	}
}