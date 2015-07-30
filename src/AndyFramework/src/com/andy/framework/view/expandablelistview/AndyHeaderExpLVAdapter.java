package com.andy.framework.view.expandablelistview;

import android.view.View;

/**
 * @description: AndyHeaderExpandableListView的adapter接口
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-7-29  下午5:56:30
 */
public interface AndyHeaderExpLVAdapter {
	public static final int ANDY_EXPANDABLE_HEADER_GONE = 0;
	public static final int ANDY_EXPANDABLE_HEADER_VISIBLE = 1;
	public static final int ANDY_EXPANDABLE_HEADER_PUSHED_UP = 2;
	
	/**
	 * 获取 Header 的状态
	 * @param groupPosition
	 * @param childPosition
	 * @return 其中之一
	 * 		ANDY_EXPANDABLE_HEADER_GONE 
	 * 		ANDY_EXPANDABLE_HEADER_VISIBLE
	 * 		ANDY_EXPANDABLE_HEADER_PUSHED_UP
	 */
	int getHeaderState(int groupPosition, int childPosition);

	/**
	 * 配置 Header, 让 Header 知道显示的内容
	 * @param header
	 * @param groupPosition
	 * @param childPosition
	 * @param alpha
	 */
	void configureHeader(View header, int groupPosition,int childPosition, int alpha);

	/**
	 * 设置组按下的状态 
	 * @param groupPosition
	 * @param status
	 */
	void setGroupClickStatus(int groupPosition, int status);

	/**
	 * 获取组按下的状态
	 * @param groupPosition
	 * @return
	 */
	int getGroupClickStatus(int groupPosition);
}
