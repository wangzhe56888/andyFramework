package com.andy.myself.activity.animation;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.andy.myself.R;
import com.andy.myself.base.BaseHeaderActivity;

/**
 * @description: Android动画测试
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-10  上午10:17:28
 */
@SuppressLint("InflateParams")
public class AnimationActivity extends BaseHeaderActivity {

	private String[] menuStrings = {"ValueAnimator", "ObjectAnimator", "AnimatorSet", "XML配置", "ValueAnim高级", "ObjectAnim高级", "Interpolator"};
	private GridView gridView;
	private TextView resultTextView;
	private MyAnimView myAnimView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_animation);
		initView(savedInstanceState);
	}
	
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		resultTextView = (TextView) findViewById(R.id.animation_result_tv);
		gridView = (GridView) findViewById(R.id.animation_button_gridview_id);
		myAnimView = (MyAnimView) findViewById(R.id.animation_myanimview_id);
		
		gridView.setAdapter(new ButtonMenuAdapter(this, menuStrings));
		
		if (Build.VERSION.SDK_INT > 10) {
			gridView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					animationExcute(position);
				}
			});
		} else {
			resultTextView.setText("当前版本不支持属性动画，最低要求3.1版本");
		}
		
		
	}
	
	class ButtonMenuAdapter extends BaseAdapter {
		private String[] menuStrings;
		private LayoutInflater inflater;
		
		public ButtonMenuAdapter(Context context, String[] menuStrings) {
			this.menuStrings = menuStrings;
			inflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return menuStrings.length;
		}

		@Override
		public Object getItem(int position) {
			return menuStrings[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = inflater.inflate(R.layout.activity_animation_menu_item, null);
			TextView button = (TextView) view.findViewById(R.id.animation_item_btn);
			button.setText(menuStrings[position]);
			return view;
		}
	}
	
	@SuppressLint("NewApi")
	private void animationExcute(int position) {
		switch (position) {
		case 0:
		{
			ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
			animator.setDuration(500);
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					resultTextView.setText("打印结果请看LogCat");
					Log.e(LOG_TAG, "animatedValue: " + (Float)animation.getAnimatedValue());
				}
			});
			animator.start();
			break;
		}
		case 1:
		{
			/**
			 * propertyName: alpha, rotation, translationX, translationY, scaleX, scaleY等
			 * */
			ObjectAnimator animator = ObjectAnimator.ofFloat(resultTextView, "rotation", 0, 360);
			animator.setDuration(2000);
			animator.addListener(new AnimationAdapter("ObjectAnimator测试"));
			animator.start();
			break;
		}
		case 2:
		{
			/**
			 * 组合动画
			 * */
			float currentX = resultTextView.getTranslationX();
			ObjectAnimator translationX = ObjectAnimator.ofFloat(resultTextView, "translationX", currentX, -300, 300, currentX);
			ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(resultTextView, "alpha", 1f, 0f, 1f);
			ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(resultTextView, "scaleY", 3f, 0f, 1f);
			
			AnimatorSet animatorSet = new AnimatorSet();
			AnimatorSet animatorSet2 = new AnimatorSet();
			animatorSet2.play(alphaAnimator).with(scaleYAnimator);
			
			animatorSet.play(translationX).after(animatorSet2);
			animatorSet.setDuration(3000);
			animatorSet.addListener(new AnimationAdapter("属性组合动画"));
			animatorSet.start();
			break;
		}
		case 3:
		{
			Animator animatorSet = AnimatorInflater.loadAnimator(mActivity, R.anim.animator_set);
			animatorSet.setTarget(resultTextView);
			animatorSet.addListener(new AnimationAdapter("XML定义组合动画"));
			animatorSet.start();
			break;
		}
		case 4:
		{
			myAnimView.startValueAnimation();
			break;
		}
		case 5:
		{
			myAnimView.startObjectAnimator();
			break;
		}
		case 6:
		{
			myAnimView.startAnimationInterpolator();
			break;
		}
		default:
			break;
		}
	}
	
	@SuppressLint("NewApi")
	class AnimationAdapter extends AnimatorListenerAdapter {
		private String animationName;
		
		public AnimationAdapter(String animationName) {
			this.animationName = animationName;
		}
		@Override
		public void onAnimationStart(Animator animation) {
			resultTextView.setText("开始：" + animationName);
		}
		@Override
		public void onAnimationEnd(Animator animation) {
			resultTextView.setText("结束：" + animationName);
		}
	}
}
