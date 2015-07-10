package com.andy.myself.activity.dragGridView;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.GridView;

import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;


public class DragGridViewActivity extends BaseActivity {
    private List<String> strList;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_gridview);
        initData();
        initView(savedInstanceState);
    }

    public void initData(){
        strList = new ArrayList<String>();
        for(int i = 0; i < 100; i++){
            strList.add("Channel " + i);
        }
    }
	@Override
	protected void initView(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		gridView = (GridView)findViewById(R.id.drag_grid_view);
	    GridViewAdapter adapter = new GridViewAdapter(this, strList);
	    gridView.setAdapter(adapter);
	}
}
