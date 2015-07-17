package com.andy.myself.activity.andyframework;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.andy.framework.bitmap.AndyBitmap;
import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;

/**
 * @description: andy framework bitmap框架测试
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-13  下午2:22:35
 */
public class AndyBitmapActivity extends BaseActivity {

	private final String URL_IMG = "http://192.168.8.39:8080/AndroidService/img/";
	
	private Button bitmapButton, bitmapClearCacheButton;
	private ListView bitmapListView;
	private ImageListAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_framework_bitmap);
		initView(savedInstanceState);
	}
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		bitmapButton = (Button) findViewById(R.id.framework_bitmap_btn);
		bitmapClearCacheButton = (Button) findViewById(R.id.framework_bitmap_clear_cache_btn);
		bitmapListView = (ListView) findViewById(R.id.framework_bitmap_listview);
		
		bitmapButton.setOnClickListener(this);
		bitmapClearCacheButton.setOnClickListener(this);
		
		adapter = new ImageListAdapter(mActivity, getListData(0));
		bitmapListView.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.framework_bitmap_btn:
				int from = adapter.getCount();
				adapter.addData(getListData(from));
				adapter.notifyDataSetChanged();
				break;
			case R.id.framework_bitmap_clear_cache_btn:
				AndyBitmap andyBitmap = AndyBitmap.create(mActivity);
				andyBitmap.clearCache();
				break;
			default:
				break;
		}
		super.onClick(v);
	}
	
	private List<String> getListData(int from) {
		List<String> dataList = new ArrayList<String>();
		for (int i = from; i < from + 10; i++) {
			if (i > 17) {
				dataList.add(URL_IMG + "img_" + (i % 17) + ".jpg");
			} else {
				dataList.add(URL_IMG + "img_" + i + ".jpg");
			}
		}
		return dataList;
	}
	
	class ImageListAdapter extends BaseAdapter {
		private List<String> dataList;
		private LayoutInflater inflater;
		private Context context;
		private ImageListAdapter(Context context, List<String> dataList) {
			this.dataList = dataList;
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView != null) {
				holder = (Holder) convertView.getTag();
			} else {
				holder = new Holder();
				convertView = inflater.inflate(R.layout.activity_framework_bitmap_list_item, null);
				holder.imageView = (ImageView) convertView.findViewById(R.id.framework_bitmap_item_imageview);
				holder.textView = (TextView) convertView.findViewById(R.id.framework_bitmap_item_textview);
				convertView.setTag(holder);
			}
			
			String imageUrl = dataList.get(position);
			AndyBitmap bitmap = AndyBitmap.create(context);
			bitmap.display(holder.imageView, imageUrl);
			if (imageUrl != null) {
				holder.textView.setText(imageUrl.substring(imageUrl.lastIndexOf("/")));
			}
			return convertView;
		}
		
		public void addData(List<String> data) {
			dataList.addAll(data);
		}
		
		class Holder {
			public ImageView imageView;
			public TextView textView;
		}
	}
	
}
