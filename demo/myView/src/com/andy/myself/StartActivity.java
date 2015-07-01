package com.andy.myself;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.andy.myself.activity.AutoTextActivity;
import com.andy.myself.activity.CircleActivity;
import com.andy.myself.activity.DragViewActivity;
import com.andy.myself.activity.DrawImageActivity;
import com.andy.myself.activity.ExpandableLVActivity;
import com.andy.myself.activity.GifViewActivity;
import com.andy.myself.activity.GroupListView;
import com.andy.myself.activity.InstallActivity;
import com.andy.myself.activity.LocationBaiduActivity;
import com.andy.myself.activity.NetWorkStateActivity;
import com.andy.myself.activity.SlidingActivity;
import com.andy.myself.activity.SurfaceViewActivity;
import com.andy.myself.activity.ValidateActivity;
import com.andy.myself.activity.VolleyActivity;
import com.andy.myself.activity.WebServiceActivity;
import com.andy.myself.activity.WebViewActivity;
import com.andy.myself.activity.andyframework.AndyBitmapActivity;
import com.andy.myself.activity.andyframework.AndyHttpActivity;
import com.andy.myself.activity.animation.AnimationActivity;
import com.andy.myself.activity.gaode.GaodeStartActivity;
import com.andy.myself.activity.resideLayout.ResideLayoutActivity;
import com.andy.myself.activity.systemAppInfo.GetApplicationOfInstalled;
import com.andy.myself.activity.systemAppInfo.GetApplicationOfSystem;
import com.andy.myself.base.BaseActivity;
import com.andy.myself.util.PromptUtil;

public class StartActivity extends BaseActivity implements OnItemClickListener{

	private final String KEY_MENU_NAME = "menu_name";
	private final String KEY_MENU_CLASS = "class";
	private ListView menuList;
	private static boolean isExitFlag = false;
	
	private final Object[] activities = 
		{
			R.string.activity_circle_title, CircleActivity.class.getName(),
			R.string.activity_draw_image_title, DrawImageActivity.class.getName(),
			R.string.activity_font_anim_title, AutoTextActivity.class.getName(),
			R.string.activity_webview_progress_title, WebViewActivity.class.getName(),
			R.string.activity_drag_view_title, DragViewActivity.class.getName(),
			R.string.activity_install_states_title, InstallActivity.class.getName(),
			R.string.activity_volley_title, VolleyActivity.class.getName(),
			R.string.activity_andyframework_http_title, AndyHttpActivity.class.getName(),
			R.string.activity_andyframework_bitmap_title, AndyBitmapActivity.class.getName(),
			R.string.activity_webservice_title, WebServiceActivity.class.getName(),
			R.string.activity_group_listview_title, GroupListView.class.getName(),
			R.string.activity_app_installed_all_title, GetApplicationOfSystem.class.getName(),
			R.string.activity_app_installed_external_title, GetApplicationOfInstalled.class.getName(),
			R.string.activity_reside_layout_title, ResideLayoutActivity.class.getName(),
			R.string.activity_sliding_horizontal_title, SlidingActivity.class.getName(),
			R.string.activity_expandableListView_title, ExpandableLVActivity.class.getName(),
			R.string.activity_gifView_title, GifViewActivity.class.getName(),
			R.string.activity_network_state_title, NetWorkStateActivity.class.getName(),
			R.string.activity_validate_title, ValidateActivity.class.getName(),
			R.string.activity_animation_title, AnimationActivity.class.getName(),
			R.string.activity_surfaceview_title, SurfaceViewActivity.class.getName(),
			R.string.activity_location_title, LocationBaiduActivity.class.getName(),
			R.string.activity_location_gaode_title, GaodeStartActivity.class.getName()
		};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		initView(savedInstanceState);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		titleTextView.setText(R.string.activity_start_title);
		backImageView.setVisibility(View.GONE);
		
		menuList = (ListView) findViewById(R.id.menu_list_id);
		ActivityListAdapter adapter = new ActivityListAdapter(this, getMenuData());
		menuList.setAdapter(adapter);
		
		menuList.setOnItemClickListener(this);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		TextView classText = (TextView) view.findViewById(R.id.menu_class_path_id);
		String classStr = classText.getText().toString();
		
		TextView menuName = (TextView) view.findViewById(R.id.menu_show_text_id);
		try {
			Class cls = Class.forName(classStr);
			Intent intent = new Intent(StartActivity.this, cls);
			intent.putExtra(KEY_TITLE_SRC, menuName.getText().toString());
			startActivity(intent);
			
		} catch (ClassNotFoundException e) {
			PromptUtil.showToast(StartActivity.this, "Class Not Found");
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			final Timer timer = new Timer();
			if (!isExitFlag) {
				isExitFlag = true;
				PromptUtil.showToast(StartActivity.this, "再按一次退出");
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						isExitFlag = false;
						this.cancel();
						timer.cancel();
						timer.purge();
					}
				}, 2000);
			} else {
				((AndyApplication)getApplication()).exitApp();
			}
		}
		return false;
	}
	
	private List<Map<String, String>> getMenuData() {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		int len = activities.length;
		if (len > 1 && len % 2 == 0) {
			for (int i = 0; i < len; i += 2) {
				int menuSrc = (Integer) activities[i];
				String activityName = (String) activities[i + 1];
				
				Map<String, String> map = new HashMap<String, String>();
				map.put(KEY_MENU_NAME, mActivity.getResources().getString(menuSrc));
				map.put(KEY_MENU_CLASS, activityName);
				list.add(map);
			}
		}
		return list;
	}
	
	private class ActivityListAdapter extends BaseAdapter {

		private List<Map<String, String>> dataList;
		private LayoutInflater inflater;
		
		public ActivityListAdapter(Context context, List<Map<String, String>> dataList) {
			this.dataList = dataList;
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
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.activity_start_list_item, null);
			TextView menuName = (TextView) convertView.findViewById(R.id.menu_show_text_id);
			TextView menuClass = (TextView) convertView.findViewById(R.id.menu_class_id);
			TextView menuClassPath = (TextView) convertView.findViewById(R.id.menu_class_path_id);
			
			Map<String, String> map = dataList.get(position);
			
			String classNameString = map.get(KEY_MENU_CLASS);
			if (classNameString != null) {
				String arr = classNameString.substring(classNameString.lastIndexOf(".") + 1);
				menuClass.setText(arr);
			}
			menuName.setText(map.get(KEY_MENU_NAME));
			menuClassPath.setText(classNameString);
			
			return convertView;
		}
		
	}
}
