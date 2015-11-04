package com.andy.view.game;

import com.andy.myself.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * @description: 绘制
 * @author: andy
 * @mail: win58@qq.com
 * @date: 2015-10-29 下午5:38:44
 */
public class DrawSurfaceView extends SurfaceView implements Callback{

	private SurfaceHolder mSurfaceHolder;
	private Paint mPaint;
	private Canvas mCanvas;
	
	public DrawSurfaceView(Context context) {
		super(context);
		init();
	}
	
	public DrawSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	// 初始化属性
	private void init() {
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(30);
	}
	
	// 绘制
	private void drawBisic() {
		mCanvas = mSurfaceHolder.lockCanvas();
		mCanvas.drawText("游戏开发初级", 20, 40, mPaint);
		
		mCanvas.drawLine(0, 60, getWidth(), 60, mPaint);
		
		mPaint.setColor(Color.RED);
		mCanvas.drawPoints(new float[]{0, 60f, getWidth() - 2, 60f}, mPaint);
		
		mCanvas.drawCircle(5, 60, 5, mPaint);
		mCanvas.drawCircle(getWidth() - 5, 60, 5, mPaint);
		
		mPaint.setColor(Color.BLUE);
		RectF rectF = new RectF(10, 70, getWidth() / 2 - 10, 70 + getWidth() / 2 - 20);
		RectF rectF2 = new RectF(getWidth() / 2 + 10, 70, getWidth() - 10, 70 + getWidth() / 2 - 20);
		RectF ovalRectF = new RectF(getWidth() / 2 + 10, 70 + 20, getWidth() - 50, 70 + 50);
		mCanvas.drawRect(rectF, mPaint);
		mCanvas.drawRect(rectF2, mPaint);
		
		mPaint.setColor(Color.RED);
		mCanvas.drawArc(rectF, -45, 90, true, mPaint);
		mCanvas.drawArc(rectF2, -45, 90, false, mPaint);
		
		mCanvas.drawOval(ovalRectF, mPaint);
		
		Path mPath = new Path();
		mPath.moveTo(40, 100);
		mPath.lineTo(70, 150);
		mPath.lineTo(70, 180);
		mPath.lineTo(50, 130);
		mPath.close();
		mCanvas.drawPath(mPath, mPaint);
		
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
	}

	private void drawBitmap() {
		mPaint.setColor(Color.BLUE);
		Rect bitmapRect = new Rect(10, 70 + getWidth() / 2, getWidth() - 10, 70 + getWidth());
		mCanvas = mSurfaceHolder.lockCanvas(bitmapRect);
		mCanvas.drawRect(bitmapRect, mPaint);
		
		mCanvas.save();
		mCanvas.rotate(-20);
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
		mCanvas.drawBitmap(mBitmap, 30, 120 + getWidth() / 2, mPaint);
		mCanvas.restore();
		
		mCanvas.save();
		mCanvas.rotate(20);
		mCanvas.translate(getWidth() / 2, -mBitmap.getHeight());
		mCanvas.drawBitmap(mBitmap, 30, 120 + getWidth() / 2, mPaint);
		mCanvas.restore();
		
		mCanvas.save();
		mCanvas.scale(-1f, 1.5f, mCanvas.getWidth() / 2, mCanvas.getHeight() / 2);
		mCanvas.drawBitmap(mBitmap, 10, 70 + getWidth() / 2, mPaint);
		mCanvas.restore();
		
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
	}
	
	private void drawClipCanvas() {
		mCanvas = mSurfaceHolder.lockCanvas(new Rect(10, 70, getWidth() / 2 - 10, 70 + getWidth() / 2 - 20));
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.andy_photo);
		
		mCanvas.save();
		Path mPath = new Path();
		mPath.addCircle(80, 120, 40, Direction.CCW);
		mCanvas.clipPath(mPath);
		mCanvas.drawBitmap(mBitmap, 10, 70, mPaint);
		mCanvas.restore();
		
		mSurfaceHolder.unlockCanvasAndPost(mCanvas);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawBisic();
		drawBitmap();
		drawClipCanvas();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
	}
}
