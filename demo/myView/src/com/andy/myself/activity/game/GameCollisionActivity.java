package com.andy.myself.activity.game;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;
import com.andy.view.game.CollisionSurfaceView;
import com.andy.view.game.CollisionSurfaceView.CollisionType;

/**
 * @description: 碰撞测试
 * @author: andy
 * @mail: win58@qq.com
 * @date: 2015-11-2 下午2:25:52
 */
public class GameCollisionActivity extends BaseHeaderActivity {
	private RadioGroup mRadioGroup;
	private CollisionSurfaceView mCollisionSurfaceView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_collision_surfaceview);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
		mCollisionSurfaceView = (CollisionSurfaceView) findViewById(R.id.surfaceview_id);
		
		((RadioButton)mRadioGroup.getChildAt(0)).setChecked(true);
		
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton radioButton = (RadioButton) findViewById(checkedId);
				int tag = (Integer) radioButton.getTag();
				switch (tag) {
				case 1:
					mCollisionSurfaceView.setCollisionType(CollisionType.COLLISION_RECT);
					break;
				case 2:
					mCollisionSurfaceView.setCollisionType(CollisionType.COLLISION_CIRCLE);
					break;
				case 3:
					mCollisionSurfaceView.setCollisionType(CollisionType.COLLISION_PIXEL);
					break;
				default:
					break;
				}
			}
		});
	}
}
