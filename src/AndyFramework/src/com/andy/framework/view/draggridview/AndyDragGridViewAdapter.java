package com.andy.framework.view.draggridview;

import android.widget.AdapterView;
import android.widget.BaseAdapter;

/**
 * @description: 可拖拽gridview的adapter
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-7-16  下午5:28:54
 */
public abstract class AndyDragGridViewAdapter extends BaseAdapter {
	protected int hidePosition = AdapterView.INVALID_POSITION;
	
	/**
	 * 隐藏position位置的item
	 * */
	public void onHideView(int position){
		hidePosition = position;
		notifyDataSetChanged();
	};

	/**
	 * 显示隐藏的item
	 * */
    public void onShowHideView() {
    	hidePosition = AdapterView.INVALID_POSITION;
    	notifyDataSetChanged();
    };

    /**
     * 移除position位置的item
     * */
    public void onRemoveView(int position) {
    	notifyDataSetChanged();
    };
    
    /**
     * 拖动时更新view
     * @param draggedPos 拖拽的item的position
     * @param targetPos 拖拽到的目标position
     * 
     * 注意：重写该方法时hidePosition = draggedPos;应该放在数据处理之后，否则会有错位问题
     * */
    public void onDraggingView(int draggedPos, int targetPos) {
    	hidePosition = targetPos;
    	notifyDataSetChanged();
    };
}
