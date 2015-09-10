package com.andy.myself;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.andy.framework.view.expandablelistview.AndyHeaderExpLVAdapter;
import com.andy.framework.view.expandablelistview.AndyHeaderExpandableListView;
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
import com.andy.myself.activity.andyframework.AndyDBActivity;
import com.andy.myself.activity.andyframework.AndyHttpActivity;
import com.andy.myself.activity.animation.AnimationActivity;
import com.andy.myself.activity.dragGridView.DragGridViewActivity;
import com.andy.myself.activity.gaode.GaodeStartActivity;
import com.andy.myself.activity.jpush.JpushActivity;
import com.andy.myself.activity.resideLayout.ResideLayoutActivity;
import com.andy.myself.activity.systemAppInfo.GetApplicationOfInstalled;
import com.andy.myself.activity.systemAppInfo.GetApplicationOfSystem;
import com.andy.myself.activity.tencentmap.TencentMapMainActivity;
import com.andy.myself.base.BaseHeaderActivity;
import com.andy.myself.util.PromptUtil;
import com.andy.view.slidingMenu.SlidingMenu;

public class StartActivity extends BaseHeaderActivity implements OnChildClickListener {

	private enum GroupType {
		group_all,
		group_framework,
		group_view,
		group_system,
		group_help,
		group_third_party,
		group_animator,
		group_other
	}
	
	private final String KEY_MENU_NAME = "menu_name";
	private final String KEY_MENU_CLASS = "class";
	private static boolean isExitFlag = false;
	private SlidingMenu slidingMenu;
	private LinearLayout mainLayout;
	private AndyHeaderExpandableListView expandableListView;
	private ExpListViewAdapter adapter;
	private ListView menuListView;
	private MenuListAdapter listviewAdapter;
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
		backImageView.setVisibility(View.VISIBLE);
		backImageView.setBackgroundResource(R.drawable.icon_menu);
		slidingMenu = (SlidingMenu) findViewById(R.id.start_menu_view_id);
		mainLayout = (LinearLayout) findViewById(R.id.start_main_view_id);
		expandableListView = (AndyHeaderExpandableListView) findViewById(R.id.start_expandable_listview_id);
		menuListView = (ListView) findViewById(R.id.start_menu_list_id);
		
		expandableListView.setHeaderView(getLayoutInflater().inflate(R.layout.activity_start_exp_lv_item,
				expandableListView, false));
		adapter = new ExpListViewAdapter(mActivity, getGroupBeanArray(GroupType.group_all));
		expandableListView.setAdapter(adapter);
		
		mainLayout.setOnClickListener(this);
		expandableListView.setOnChildClickListener(this);
		
		listviewAdapter = new MenuListAdapter(mActivity, GroupType.values());
		menuListView.setAdapter(listviewAdapter);
		
		menuListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				slidingMenu.closeMenu();
				adapter.setGroupBeans(getGroupBeanArray((GroupType)listviewAdapter.getItem(position)));
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	
	@Override
	public void onClick(View v) {
		if (v.getId() == mainLayout.getId()) {
			if (slidingMenu.isOpen()) {
				slidingMenu.closeMenu();
			}
		} else if (v.getId() == backImageView.getId()) {
			slidingMenu.toggle();
		} else {
			super.onClick(v);
		}
	}
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		@SuppressWarnings("unchecked")
		Map<String, String> data = (Map<String, String>) adapter.getChild(groupPosition, childPosition);
		
		String clazz = data.get(KEY_MENU_CLASS);
		try {
			@SuppressWarnings("rawtypes")
			Class cls = Class.forName(clazz);
			Intent intent = new Intent(mActivity, cls);
			intent.putExtra(KEY_TITLE_SRC, data.get(KEY_MENU_NAME));
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			PromptUtil.showToast(StartActivity.this, "Class Not Found");
		}
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (slidingMenu.isOpen()) {
				slidingMenu.closeMenu();
				return true;
			}
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

	
/*********************************  数据的封装  ****************************************************/	
	private List<Map<String, String>> getMenuData(Object[] activities) {
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
	
	// 根据类型获取列表数组
	private GroupBean[] getGroupBeanArray(GroupType type) {
		// 框架相关
		Object[] frameworkActivities = 
			{
				R.string.activity_volley_title, VolleyActivity.class.getName(),
				R.string.activity_andyframework_http_title, AndyHttpActivity.class.getName(),
				R.string.activity_andyframework_bitmap_title, AndyBitmapActivity.class.getName(),
				R.string.activity_db_title, AndyDBActivity.class.getName()
			};
		// 控件相关
		Object[] viewActivities = 
			{
				R.string.activity_circle_title, CircleActivity.class.getName(),
				R.string.activity_draw_image_title, DrawImageActivity.class.getName(),
				R.string.activity_font_anim_title, AutoTextActivity.class.getName(),
				R.string.activity_webview_progress_title, WebViewActivity.class.getName(),
				R.string.activity_drag_view_title, DragViewActivity.class.getName(),
				R.string.activity_webservice_title, WebServiceActivity.class.getName(),
				R.string.activity_group_listview_title, GroupListView.class.getName(),
				R.string.activity_reside_layout_title, ResideLayoutActivity.class.getName(),
				R.string.activity_sliding_horizontal_title, SlidingActivity.class.getName(),
				R.string.activity_expandableListView_title, ExpandableLVActivity.class.getName(),
				R.string.activity_gifView_title, GifViewActivity.class.getName(),
				R.string.activity_surfaceview_title, SurfaceViewActivity.class.getName(),
				R.string.activity_drag_gridview_title, DragGridViewActivity.class.getName()
			};
		// application相关
		Object[] systemActivities = 
			{
				R.string.activity_install_states_title, InstallActivity.class.getName(),
				R.string.activity_app_installed_all_title, GetApplicationOfSystem.class.getName(),
				R.string.activity_app_installed_external_title, GetApplicationOfInstalled.class.getName()
			};
		
		// 帮助工具类相关
		Object[] helpActivities = 
			{
				R.string.activity_network_state_title, NetWorkStateActivity.class.getName(),
				R.string.activity_validate_title, ValidateActivity.class.getName()
			};
			
		// 第三方应用测试
		Object[] thirdPartyActivities = 
			{
				R.string.activity_location_title, LocationBaiduActivity.class.getName(),
				R.string.activity_location_gaode_title, GaodeStartActivity.class.getName(),
				R.string.activity_location_tencent_title, TencentMapMainActivity.class.getName(),
				R.string.activity_jpush_title, JpushActivity.class.getName()
			};
		// 动画相关
		Object[] animationActivities = 
			{
				R.string.activity_animation_title, AnimationActivity.class.getName()
			};
		
		// 其他相关
		Object[] otherActivities = 
			{
			};
		
		GroupBean[] groupBeans = null;
		if (GroupType.group_all == type) {
			groupBeans = new GroupBean[7];
			GroupBean bean0 = new GroupBean(
					getResources().getString(R.string.activity_start_group_framework_title), 
					getResources().getString(R.string.activity_start_group_framework_desc), 
					getMenuData(frameworkActivities));
			groupBeans[0] = bean0;
			
			GroupBean bean1 = new GroupBean(
					getResources().getString(R.string.activity_start_group_view_title), 
					getResources().getString(R.string.activity_start_group_view_desc), 
					getMenuData(viewActivities));
			groupBeans[1] = bean1;
			
			GroupBean bean2 = new GroupBean(
					getResources().getString(R.string.activity_start_group_help_title), 
					getResources().getString(R.string.activity_start_group_help_desc), 
					getMenuData(helpActivities));
			groupBeans[2] = bean2;
			
			GroupBean bean3 = new GroupBean(
					getResources().getString(R.string.activity_start_group_third_party_title), 
					getResources().getString(R.string.activity_start_group_third_party_desc), 
					getMenuData(thirdPartyActivities));
			groupBeans[3] = bean3;
			
			GroupBean bean4 = new GroupBean(
					getResources().getString(R.string.activity_start_group_application_title), 
					getResources().getString(R.string.activity_start_group_application_desc), 
					getMenuData(systemActivities));
			groupBeans[4] = bean4;
			
			GroupBean bean5 = new GroupBean(
					getResources().getString(R.string.activity_start_group_animator_title), 
					getResources().getString(R.string.activity_start_group_animator_desc), 
					getMenuData(animationActivities));
			groupBeans[5] = bean5;
			
			GroupBean bean6 = new GroupBean(
					getResources().getString(R.string.activity_start_group_other_title), 
					getResources().getString(R.string.activity_start_group_other_desc), 
					getMenuData(otherActivities));
			groupBeans[6] = bean6;
		} else if (GroupType.group_system == type) {
			groupBeans = new GroupBean[1];
			GroupBean bean = new GroupBean(
					getResources().getString(R.string.activity_start_group_application_title), 
					getResources().getString(R.string.activity_start_group_application_desc), 
					getMenuData(systemActivities));
			groupBeans[0] = bean;
		} else if (GroupType.group_framework == type) {
			groupBeans = new GroupBean[1];
			GroupBean bean = new GroupBean(
					getResources().getString(R.string.activity_start_group_framework_title), 
					getResources().getString(R.string.activity_start_group_framework_desc), 
					getMenuData(frameworkActivities));
			groupBeans[0] = bean;
		} else if (GroupType.group_help == type) {
			groupBeans = new GroupBean[1];
			GroupBean bean = new GroupBean(
					getResources().getString(R.string.activity_start_group_help_title), 
					getResources().getString(R.string.activity_start_group_help_desc), 
					getMenuData(helpActivities));
			groupBeans[0] = bean;
		} else if (GroupType.group_third_party == type) {
			groupBeans = new GroupBean[1];
			GroupBean bean = new GroupBean(
					getResources().getString(R.string.activity_start_group_third_party_title), 
					getResources().getString(R.string.activity_start_group_third_party_desc), 
					getMenuData(thirdPartyActivities));
			groupBeans[0] = bean;
		} else if (GroupType.group_view == type) {
			groupBeans = new GroupBean[1];
			GroupBean bean = new GroupBean(
					getResources().getString(R.string.activity_start_group_view_title), 
					getResources().getString(R.string.activity_start_group_view_desc), 
					getMenuData(viewActivities));
			groupBeans[0] = bean;
		} else if (GroupType.group_animator == type) {
			groupBeans = new GroupBean[1];
			GroupBean bean = new GroupBean(
					getResources().getString(R.string.activity_start_group_animator_title), 
					getResources().getString(R.string.activity_start_group_animator_desc), 
					getMenuData(animationActivities));
			groupBeans[0] = bean;
		} else if (GroupType.group_other == type) {
			groupBeans = new GroupBean[1];
			GroupBean bean = new GroupBean(
					getResources().getString(R.string.activity_start_group_other_title), 
					getResources().getString(R.string.activity_start_group_other_desc), 
					getMenuData(otherActivities));
			groupBeans[0] = bean;
		}
		return groupBeans;
	}
	
	
	@SuppressLint("InflateParams")
	private class ExpListViewAdapter extends BaseExpandableListAdapter implements AndyHeaderExpLVAdapter {
		private GroupBean[] groupBeans;
		private LayoutInflater inflater;
		public ExpListViewAdapter(Context context, GroupBean[] beans) {
			super();
			this.groupBeans = beans;
			inflater = LayoutInflater.from(context);
		}
		
		public void setGroupBeans(GroupBean[] groupBeans) {
			this.groupBeans = groupBeans;
		}
		
		@Override
		public int getGroupCount() {
			return groupBeans.length;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return groupBeans[groupPosition].childList.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupBeans[groupPosition];
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return groupBeans[groupPosition].childList.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// 是否指定分组视图及其子视图的ID对应的后台数据改变也会保持该ID
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// 指定位置的子视图是否可选择。
			return true;
		}
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ExpLVHolder holder = null;
			if (convertView != null) {
				holder = (ExpLVHolder) convertView.getTag();
			} else {
				convertView = inflater.inflate(R.layout.activity_start_exp_lv_item, null);
				holder = new ExpLVHolder();
				holder.iconImageView = (ImageView) convertView.findViewById(R.id.start_item_icon_id);
				holder.titleTextView = (TextView) convertView.findViewById(R.id.start_item_title_id);
				holder.descTextView = (TextView) convertView.findViewById(R.id.start_item_desc_id);
				convertView.setTag(holder);
			}
			
			GroupBean bean = groupBeans[groupPosition];
			holder.iconImageView.setImageResource(
					isExpanded ? R.drawable.icon_browser_normal : R.drawable.icon_browser_expand);
			holder.titleTextView.setText(bean.title);
			holder.descTextView.setText(bean.desc);
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ExpLVHolder holder = null;
			if (convertView != null) {
				holder = (ExpLVHolder) convertView.getTag();
			} else {
				convertView = inflater.inflate(R.layout.activity_start_exp_lv_item, null);
				holder = new ExpLVHolder();
				holder.iconImageView = (ImageView) convertView.findViewById(R.id.start_item_icon_id);
				holder.titleTextView = (TextView) convertView.findViewById(R.id.start_item_title_id);
				holder.descTextView = (TextView) convertView.findViewById(R.id.start_item_desc_id);
				convertView.setTag(holder);
			}
			
			convertView.setBackgroundResource(R.color.andy_color_white_gray);
			holder.iconImageView.setVisibility(View.INVISIBLE);
			
			Map<String, String> map = groupBeans[groupPosition].childList.get(childPosition);
			String classNameString = map.get(KEY_MENU_CLASS);
			if (classNameString != null) {
				String arr = classNameString.substring(classNameString.lastIndexOf(".") + 1);
				holder.descTextView.setText(arr);
			}
			holder.titleTextView.setText(map.get(KEY_MENU_NAME));
			holder.descTextView.setTag(classNameString);
			
			return convertView;
		}

		// interface methods
		@Override
		public int getHeaderState(int groupPosition, int childPosition) {
			final int childCount = getChildrenCount(groupPosition);
			if (childPosition == childCount - 1) {
				return ANDY_EXPANDABLE_HEADER_PUSHED_UP;
			} else if (childPosition == -1 && !expandableListView.isGroupExpanded(groupPosition)) {
				return ANDY_EXPANDABLE_HEADER_GONE;
			} else {
				return ANDY_EXPANDABLE_HEADER_VISIBLE;
			}
		}
	
		@Override
		public void configureHeader(View header, int groupPosition, int childPosition, int alpha) {
			GroupBean bean = groupBeans[groupPosition];
			ImageView iconImageView = (ImageView)header.findViewById(R.id.start_item_icon_id);
			TextView titleTextView = (TextView)header.findViewById(R.id.start_item_title_id);
			TextView descTextView = (TextView)header.findViewById(R.id.start_item_desc_id);
			iconImageView.setImageResource(R.drawable.icon_browser_normal);
			titleTextView.setText(bean.title);
			descTextView.setText(bean.desc);
		}
		
		private SparseIntArray groupStatusMap = new SparseIntArray(); 
		
		@Override
		public void setGroupClickStatus(int groupPosition, int status) {
			groupStatusMap.put(groupPosition, status);
		}

		@Override
		public int getGroupClickStatus(int groupPosition) {
			if (groupStatusMap.keyAt(groupPosition)>=0) {
				return groupStatusMap.get(groupPosition);
			} else {
				return 0;
			}
		}
	}
	
	class MenuListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private GroupType[] types;
		public MenuListAdapter(Context context, GroupType[] types) {
			inflater = LayoutInflater.from(context);
			this.types = types;
		}
		@Override
		public int getCount() {
			return types.length;
		}

		@Override
		public Object getItem(int position) {
			return types[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.activity_start_left_menu_item, null);
			}
			TextView textView = (TextView) convertView.findViewById(R.id.start_menu_item_tv_id);
			switch (types[position]) {
			case group_all:
				textView.setText(R.string.activity_start_group_all_title);
				break;
			case group_framework:
				textView.setText(R.string.activity_start_group_framework_title);
				break;
			case group_animator:
				textView.setText(R.string.activity_start_group_animator_title);
				break;
			case group_help:
				textView.setText(R.string.activity_start_group_help_title);
				break;
			case group_system:
				textView.setText(R.string.activity_start_group_application_title);
				break;
			case group_third_party:
				textView.setText(R.string.activity_start_group_third_party_title);
				break;
			case group_view:
				textView.setText(R.string.activity_start_group_view_title);
				break;
			default:
				break;
			}
			return convertView;
		}
	}
}

class ExpLVHolder {
	public ImageView iconImageView;
	public TextView titleTextView;
	public TextView descTextView;
}

/**
 * @field title标题
 * @field desc描述
 * @field childArr子目录数组
 * */
class GroupBean {
	public String title;
	public String desc;
	public List<Map<String, String>> childList;
	
	public GroupBean() {
	}
	public GroupBean(String title, String desc, List<Map<String, String>> childArr) {
		this.title = title;
		this.desc = desc;
		this.childList = childArr;
	}
}
