package com.andy.framework.view.draggridview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * @description: 可拖拽排序的gridview
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-7-16  下午3:45:13
 */
@SuppressLint("ClickableViewAccessibility")
public class AndyDragItemGridView extends GridView {
	// 拖拽IMG显示
	private static final int DRAG_IMG_SHOW_YES = 0x0;
	// 拖拽IMG未显示
	private static final int DRAG_IMG_SHOW_NO = 0x1;
	// 拖拽IMG缩放比例
	private static final float DRAG_IMG_SCALING = 1.2f;
	
	// 拖动的时候显示的Imageview
	private ImageView dragImageView;
	// 设置拖拽Imageview的参数
	private WindowManager.LayoutParams dragImageViewParams;
	// 窗口管理对象，用于往window上添加拖拽的Imageview
	private WindowManager windowManager;
	private boolean isViewOnDrag = false; // 是否正在拖拽
	
	// 拖拽的目标位置
	private int dragTargetPosition = AdapterView.INVALID_POSITION;
	private int downX, downY;
	
	private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			dragTargetPosition = position;
			view.destroyDrawingCache();
			view.setDrawingCacheEnabled(true);
			// 获取长按view的Bitmap
			Bitmap dragBitmap = Bitmap.createBitmap(view.getDrawingCache());
			
			// 设置长按view的参数
			dragImageViewParams.gravity = Gravity.TOP | Gravity.LEFT;
			// 设置拖拽的view为原view的DRAG_IMG_SCALING倍
			dragImageViewParams.width = (int) (dragBitmap.getWidth() * DRAG_IMG_SCALING);
			dragImageViewParams.height = (int) (dragBitmap.getHeight() * DRAG_IMG_SCALING);
			// 设置触摸点为拖拽Imageview的中心点
			dragImageViewParams.x = downX - dragImageViewParams.width / 2;
			dragImageViewParams.y = downY - dragImageViewParams.height / 2;
			
			// 设置flag
			dragImageViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
					WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
			
			dragImageViewParams.format = PixelFormat.TRANSLUCENT;
			dragImageViewParams.windowAnimations = 0;
			
			// 如果dragImageView为被拖动item的容器，则清空dragImageView
			if (DRAG_IMG_SHOW_YES == (Integer)dragImageView.getTag()) {
				windowManager.removeView(dragImageView);
				dragImageView.setTag(DRAG_IMG_SHOW_NO);
			}
			
			// 设置拖动item并添加到window
			dragImageView.setImageBitmap(dragBitmap);
			windowManager.addView(dragImageView, dragImageViewParams);
			dragImageView.setTag(DRAG_IMG_SHOW_YES);
			isViewOnDrag = true;
			
			// 设置被长按的item不显示
			((AndyDragGridViewAdapter)getAdapter()).onHideView(position);
			return true;
		}
	};
	
	
	public AndyDragItemGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParam();
	}
	public AndyDragItemGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initParam();
	}
	public AndyDragItemGridView(Context context) {
		super(context);
		initParam();
	}

	/**
	 * 初始化相关参数
	 * */
	private void initParam() {
		setOnItemLongClickListener(longClickListener);
		dragImageView = new ImageView(getContext());
		dragImageView.setTag(DRAG_IMG_SHOW_NO);
		dragImageViewParams = new WindowManager.LayoutParams();
		windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// 获取触摸点相对于屏幕的坐标
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			downX = (int) ev.getRawX();
			downY = (int) ev.getRawY();
		}
		// 手指移动的时候更新拖拽view的位置并回调adapter
		else if (ev.getAction() == MotionEvent.ACTION_MOVE && isViewOnDrag) {
			// 设置触摸点为dragImageView的中心点
			dragImageViewParams.x = (int) (ev.getRawX() - dragImageViewParams.width / 2);
			dragImageViewParams.y = (int) (ev.getRawY() - dragImageViewParams.height / 2);
			
			// 更新窗口显示的dragImageView
			windowManager.updateViewLayout(dragImageView, dragImageViewParams);
			
			// 获取当前触摸点的position
			int currentTouchPosition = pointToPosition((int)ev.getX(), (int)ev.getY());
			
			if (currentTouchPosition != AdapterView.INVALID_POSITION && currentTouchPosition != dragTargetPosition) {
				((AndyDragGridViewAdapter)getAdapter()).onDraggingView(dragTargetPosition, currentTouchPosition);
				dragTargetPosition = currentTouchPosition;
			}
		}
		// 释放拖拽的imageview
		else if (ev.getAction() == MotionEvent.ACTION_UP && isViewOnDrag) {
			AndyDragGridViewAdapter adapter = (AndyDragGridViewAdapter) getAdapter();
			adapter.onShowHideView();
			if (DRAG_IMG_SHOW_YES == (Integer)dragImageView.getTag()) {
				windowManager.removeView(dragImageView);
				dragImageView.setTag(DRAG_IMG_SHOW_NO);
			}
			isViewOnDrag = false;
		}
		return super.onTouchEvent(ev);
	}
}
