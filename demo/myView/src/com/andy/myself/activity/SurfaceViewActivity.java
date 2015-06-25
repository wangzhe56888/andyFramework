package com.andy.myself.activity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.andy.framework.util.AndyScreenUtil;
import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;

/**
 * @description: SurfaceView测试
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-16  上午11:20:29
 */
public class SurfaceViewActivity extends BaseActivity {

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	
	private Point point = new Point(0, 0);
	
	private boolean stopDraw = false;
	private boolean isExpand = true;
	
	private int screenWidth;
	
	Paint paint = new Paint();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_surfaceview);
		initView(savedInstanceState);
	}
	
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		screenWidth = AndyScreenUtil.getScreenWidth(mActivity);
		paint.setAntiAlias(true);
		// 设置画笔为空心和画笔宽度
//		paint.setStyle(Style.STROKE);
//		paint.setStrokeWidth(5.f);
		
		surfaceView = (SurfaceView) findViewById(R.id.surfaceview_id);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolderCallBack());
		
	}
	
	private void draw() {
		Canvas canvas = surfaceHolder.lockCanvas();
		
		if (canvas == null) {
			Log.e(LOG_TAG, "canvas is null !");
			return;
		}
		// 先清屏
		canvas.drawColor(Color.BLACK);
		
		if (isExpand) {
			paint.setColor(Color.BLUE);
		} else {
			paint.setColor(Color.RED);
		}
		canvas.drawRect(new Rect(0, 0, point.x, point.y), paint);
		surfaceHolder.unlockCanvasAndPost(canvas);
	}
	
	class SurfaceHolderCallBack implements SurfaceHolder.Callback {
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			new Thread(new DrawThread()).start();
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			stopDraw = true;
		}
	}
	
	class DrawThread implements Runnable {

		@Override
		public void run() {
			while (!stopDraw) {
				if (point.x >= screenWidth) {
					isExpand = false;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (point.x <= 0) {
					isExpand = true;
				}
				if (isExpand) {
					point.x += 10;
					point.y += 10;
				} else {
					point.x -= 10;
					point.y -= 10;
				}
				draw();
			}
		}
	}
}
