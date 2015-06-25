package com.andy.framework.http;
/**
 * @description: Entity回调接口，response读取数据时回调
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-7  下午4:45:07
 */
public interface AndyEntityCallBack {
	/**
	 * @param long totalCount 数据总共长度
	 * @param long currentCount 当前读取的数据长度
	 * @param boolean mustNoticeUI 是否必须通知更新UI
	 * */
	void callBack(long totalCount, long currentCount, boolean mustNoticeUI);
}
