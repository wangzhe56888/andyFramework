package com.andy.myself.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;
import com.andy.myself.util.PromptUtil;
import com.andy.view.circlelayout.CircleImageView;
import com.andy.view.circlelayout.CircleLayout;
import com.andy.view.circlelayout.CircleLayout.OnCircleItemClickListener;
import com.andy.view.circlelayout.CircleLayout.OnCircleItemSelectedListener;

public class CircleActivity extends BaseActivity implements OnCircleItemSelectedListener, OnCircleItemClickListener{
	private TextView selectedTextView;
	private CircleLayout circleLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_circle);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		titleTextView.setText(R.string.activity_circle_title);
		
		selectedTextView = (TextView) findViewById(R.id.show_selected_item_id);
		circleLayout = (CircleLayout) findViewById(R.id.circle_layout_id);
		circleLayout.setOnCircleItemClickListener(this);
		circleLayout.setOnCircleItemSelectedListener(this);
		
		selectedTextView.setText(((CircleImageView)circleLayout.getSelectedItem()).getName());
	}
	
	@Override
	public void onItemClick(View view, int position, long id, String name) {
		PromptUtil.showToast(mActivity, "position = " + position);
	}
	@Override
	public void onItemSelected(View view, int position, long id, String name) {
		selectedTextView.setText(name);
//		CircleImageView circleImg = (CircleImageView)view;
//		circleImg.setImageResource(R.drawable.logo);
	}
}
