package com.andy.myself.activity.animation;

import android.R.integer;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * @description: 属性动画运用测试view
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-10  下午5:47:20
 */
@SuppressLint("NewApi")
public class MyAnimView extends View {
	private final float RADIUS = 50f;
	
	private Paint paint;
	private Point point;
	private String color;
	
	public MyAnimView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLUE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (point == null) {
			point = new Point((int)RADIUS, (int)RADIUS);
			drawCircle(canvas);
		} else {
			drawCircle(canvas);
		}
	}
	
	private void drawCircle(Canvas canvas) {
		if (point != null) {
			canvas.drawCircle(point.x, point.y, RADIUS, paint);
		}
	}
	
	public void startValueAnimation() {
		Point startPoint = new Point((int)RADIUS, (int)RADIUS);
		Point endPoint = new Point((int)(getWidth() - RADIUS), (int)(getHeight() - RADIUS));
		ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				point = (Point) animation.getAnimatedValue();
				invalidate();
			}
		});
		valueAnimator.setDuration(5000);
		valueAnimator.start();
	}
	
	public void startObjectAnimator() {
		Point startPoint = new Point((int)RADIUS, (int)RADIUS);
		Point endPoint = new Point((int)(getWidth() - RADIUS), (int)(getHeight() - RADIUS));
		ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				point = (Point) animation.getAnimatedValue();
				invalidate();
			}
		});
		
		
		ObjectAnimator objectAnimator = ObjectAnimator.ofObject(this, "color", new ColorEvaluator(), "#0000FF", "#FF0000");
		
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.setDuration(5000);
		animatorSet.play(objectAnimator).with(valueAnimator);
		animatorSet.start();
	}
	
	public void startAnimationInterpolator() {
		Point startPoint = new Point((int)RADIUS, (int)RADIUS);
		Point endPoint = new Point((int)(getWidth() - RADIUS), (int)(getHeight() - RADIUS));
		ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
		valueAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				point = (Point) animation.getAnimatedValue();
				invalidate();
			}
		});
		valueAnimator.setDuration(5000);
		
		valueAnimator.setInterpolator(new MyInterpolator());
		
		valueAnimator.start();
	}
	/**
	 * ObjectAnimator高级测试，改变颜色
	 * */
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
		paint.setColor(Color.parseColor(color));
		invalidate();
	}
	
	class MyInterpolator implements TimeInterpolator {
		@Override
		public float getInterpolation(float input) {
			float result;
			if (input < 0.5) {
				result = input;
			} else {
				result = 1 - input;
			}
			return result;
		}
		
	}
	
	class PointEvaluator implements TypeEvaluator<Point> {
		@Override
		public Point evaluate(float fraction, Point startPoint, Point endpPoint) {
			float x = startPoint.x + fraction * (endpPoint.x - startPoint.x);
			float y = startPoint.y + fraction * (endpPoint.y - startPoint.y);
			
			Point evaluatePoint = new Point((int)x, (int)y);
			return evaluatePoint;
		}
	}
	
	public class ColorEvaluator implements TypeEvaluator<String> {
		private int mCurrentRed = -1, 
				mCurrentGreen = -1, 
				mCurrentBlue = -1;

		@Override
		public String evaluate(float fraction, String startValue, String endValue) {
			if (startValue == null || startValue.length() != 7) {
				startValue = "#0000FF";
			}
			if (endValue == null || endValue.length() != 7) {
				endValue = "#0000FF";
			}
			
			// 将16进制转成十进制
			int startRed = Integer.parseInt(startValue.substring(1, 3), 16);
			int startGreen = Integer.parseInt(startValue.substring(3, 5), 16);
			int startBlue = Integer.parseInt(startValue.substring(5, 7), 16);
			int endRed = Integer.parseInt(endValue.substring(1, 3), 16);
			int endGreen = Integer.parseInt(endValue.substring(3, 5), 16);
			int endBlue = Integer.parseInt(endValue.substring(5, 7), 16);
			
			if (mCurrentRed == -1) mCurrentRed = startRed;
			if (mCurrentGreen == -1) mCurrentGreen = startGreen;
			if (mCurrentBlue == -1) mCurrentBlue = startBlue;
			
			/** 计算初始值和结束值的差值*/
			int diffRed = Math.abs(endRed - startRed);
			int diffGreen = Math.abs(endGreen - startGreen);
			int diffBlue = Math.abs(endBlue - startBlue);
			int diffColor = diffRed + diffGreen + diffBlue;
			
			if (mCurrentRed != endRed) {
				mCurrentRed = getCurrentColor(startRed, endRed, diffColor, 0, fraction);
			}
			if (mCurrentGreen != endGreen) {
				mCurrentGreen = getCurrentColor(startGreen, endGreen, diffColor, diffRed, fraction);
			}
			if (mCurrentBlue != endBlue) {
				mCurrentBlue = getCurrentColor(startBlue, endBlue, diffColor, diffRed + diffGreen, fraction);
			}
			
			return "#" + toHexString(mCurrentRed) + toHexString(mCurrentGreen) + toHexString(mCurrentBlue);
		}
		
	}
	
	private int getCurrentColor(int startColor, int endColor, int diffColor, int offset, float fraction) {
		int currentColor;
		if (startColor > endColor) {
			currentColor = (int) (startColor - (fraction * diffColor - offset));
			if (currentColor < endColor) {
				currentColor = endColor;
			}
		} else {
			currentColor = (int) (startColor + (fraction * diffColor - offset));
			if (currentColor > endColor) {
				currentColor = endColor;
			}
		}
		return currentColor;
	}
	
	/**将int值转换成16进制的字符串*/
	private String toHexString(int value) {
		String hexString = Integer.toHexString(value);
		if (hexString.length() < 2) {
			hexString = "0" + hexString;
		}
		return hexString;
	}
}
