package com.andy.view.game;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;

/**
 * @description: 
 * @author: andy
 * @mail: win58@qq.com
 * @date: 2015-11-2 下午2:29:32
 */
public class CollisionSurfaceView extends SurfaceView implements Callback , OnTouchListener {

	public enum CollisionType {
		COLLISION_RECT, // 矩形碰撞
		COLLISION_CIRCLE, // 圆形碰撞
		COLLISION_PIXEL // 像素碰撞
	}
	private SurfaceHolder mSurfaceHolder;
	private Paint mPaint;
	private Canvas mCanvas;
	private CollisionType mCollisionType = CollisionType.COLLISION_RECT;
	private int startX, startY;
	
	private Rect firstRect, secondRect;
	
	public CollisionSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	
	public void setCollisionType(CollisionType type) {
		this.mCollisionType = type;
	}
	
	private void initView() {
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.WHITE);
		
		setOnTouchListener(this);
	}
	
	// 初始化图形
	private void initDraw() {
		int rectSide = 30;
		mCanvas = mSurfaceHolder.lockCanvas();
		mPaint.setColor(Color.BLUE);
		
		firstRect = new Rect(0, 0, rectSide, rectSide);
		mCanvas.drawRect(firstRect, mPaint);
		
		mPaint.setColor(Color.RED);
		secondRect = new Rect((getWidth() - rectSide) / 2, rectSide * 3, (getWidth() + rectSide) / 2, rectSide * 4);
		mCanvas.drawRect(secondRect, mPaint);
		
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
	}
	
	private void moveRect(int newLeft, int newTop, int newRight, int newBottom) {
		Log.e("ANDY", "first : " + firstRect.left + " | " + firstRect.top + " | " + firstRect.right + " | " + firstRect.bottom);
		Log.e("ANDY", "newRect : " + newLeft + " | " + newTop + " | " + newRight + " | " + newBottom);
		
		mSurfaceHolder.lockCanvas(firstRect);
		mPaint.setColor(Color.BLACK);
		mCanvas.drawRect(firstRect, mPaint);
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
		
		mCanvas = mSurfaceHolder.lockCanvas();
		firstRect.set(newLeft, newTop, newRight, newBottom);
		mPaint.setColor(Color.BLUE);
		mCanvas.drawRect(firstRect, mPaint);
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startX = (int) event.getX();
			startY = (int) event.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			if (CollisionType.COLLISION_RECT == mCollisionType) {
				int x = (int) (event.getX() - startX);
				int y = (int) (event.getY() - startY);
				if ((x + firstRect.left < 0) || (x + firstRect.right > getWidth())) {
					x = 0;
				}
				if ((y + firstRect.top < 0) || (y + firstRect.bottom > getHeight())) {
					y = 0;
				}
				moveRect(firstRect.left + x, firstRect.top + y, firstRect.right + x, firstRect.bottom + y);
			}
			break;
			
		default:
			break;
		}
		return true;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initDraw();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
}
