package com.andy.myself.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;

/**
 * @description: 分组的listview，完全使用了listview的adapter进行分组，如果数据分组较多并且数据分组不固定是不便使用
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-22  下午3:12:21
 */
public class GroupListView extends BaseActivity {

	private GroupListAdapter adapter = null;
    private ListView listView = null;
    private List<String> list = new ArrayList<String>();
    private List<String> listTag = new ArrayList<String>();
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_listview);
         
        initView(savedInstanceState);
    }
 
    @Override
    protected void initView(Bundle savedInstanceState) {
    	super.initView(savedInstanceState);
    	setData();
        adapter = new GroupListAdapter(this, list, listTag);
        listView = (ListView)findViewById(R.id.group_list);
        listView.setAdapter(adapter);
    }
    
    public void setData(){
        list.add("A");
        listTag.add("A");
        for(int i=0;i<3;i++){
            list.add("阿凡达"+i);
        }
        list.add("B");
        listTag.add("B");
        for(int i=0;i<3;i++){
            list.add("比特风暴"+i);
        }
        list.add("C");
        listTag.add("C");
        for(int i=0;i<30;i++){
            list.add("查理风云"+i);
        }
    }
    private static class GroupListAdapter extends ArrayAdapter<String>{
         
        private List<String> listTag = null;
        public GroupListAdapter(Context context, List<String> objects, List<String> tags) {
            super(context, 0, objects);
            this.listTag = tags;
        }
         
        @Override
        public boolean isEnabled(int position) {
            if(listTag.contains(getItem(position))){
                return false;
            }
            return super.isEnabled(position);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(listTag.contains(getItem(position))){
                view = LayoutInflater.from(getContext()).inflate(R.layout.activity_group_list_item_tag, null);
            }else{                   
                view = LayoutInflater.from(getContext()).inflate(R.layout.activity_group_list_item, null);
            }
            TextView textView = (TextView) view.findViewById(R.id.group_list_item_text);
            textView.setText(getItem(position));
            return view;
        }
    }
}
