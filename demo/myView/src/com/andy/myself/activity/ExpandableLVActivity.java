package com.andy.myself.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

/**
 * @description: 
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-1  下午4:28:37
 */
public class ExpandableLVActivity extends BaseHeaderActivity {

	private ExpandableListView elv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_expandablelistview_layout);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		
		ELVAdapter adapter = new ELVAdapter(this);
		elv = (ExpandableListView) findViewById(R.id.expandable_list_id);
		elv.setGroupIndicator(null); 
		elv.setAdapter(adapter);
		
		elv.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				
				return false;
			}
		});
	}
	
	private class ELVAdapter extends BaseExpandableListAdapter {

		private List<String> groupList;
		private List<List<String>> childList;
		private LayoutInflater inflater;
		private Context context;
		
		public ELVAdapter(Context context) {
			groupList = new ArrayList<String>();
			childList = new ArrayList<List<String>>();
			
			for (int i = 1; i < 6; i++) {
				groupList.add("group_" + i);
			}
			
			for (int i = 1; i < 6; i++) {
				List<String> tempList = new ArrayList<String>();
				for (int j = 0; j < 4; j++) {
					tempList.add("child_" + i + "_" + j);
				}
				childList.add(tempList);
			}
			this.context = context;
			inflater = LayoutInflater.from(context);
		}
		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childList.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childList.get(groupPosition).get(childPosition);
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
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.activity_expandablelistview_item_layout, null);
			TextView textView = (TextView) convertView.findViewById(R.id.item_id);
			textView.setTextColor(context.getResources().getColor(R.color.red));
			textView.setText(groupList.get(groupPosition));
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			convertView = inflater.inflate(R.layout.activity_expandablelistview_item_layout, null);
			TextView textView = (TextView) convertView.findViewById(R.id.item_id);
			textView.setTextColor(context.getResources().getColor(R.color.black));
			textView.setText(childList.get(groupPosition).get(childPosition));
			textView.setGravity(Gravity.CENTER);
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}
}
