package com.andy.framework.http;
/**
 * @description: 请求返回抽象类，主要提供请求周期的回调方法
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-8  下午4:18:37
 */
public abstract class AndyHttpCallback<T> {

	private boolean progress = true; // 是否需要加载进度
	private int rate = 1000; // 进度回调频率（毫秒）
	
	
	public boolean isProgress() {
		return progress;
	}
	public int getRate() {
		return rate;
	}
	
	/**
	 * 设置加载进度
	 * @param isProgress 是否需要加载进度，默认true
	 * @param rate 进度更新频率，默认1000毫秒
	 * */
	public AndyHttpCallback<T> progress(boolean isProgress, int rate) {
		this.progress = isProgress;
		this.rate = rate;
		return this;
	}
	
	
	/**
	 * @method onStart 开始请求回调
	 * @method onLoading 加载中回调
	 * @method onSuccess 成功回调
	 * @method onFailure 失败回调
	 * @method onEnd 结束回调
	 * */
	public void onStart() {}
	public void onLoading(long count,long current) {}
	public void onSuccess(T t) {}
	public void onFailure(Throwable throwable, int errorCode, String errorMsg) {}
	public void onEnd() {}
}
