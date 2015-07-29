package com.andy.framework.view.gifview;
/**
 * @description: GIF解码动作接口
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-6-8  下午2:48:31
 */
public interface AndyGifParseListener {
	/**
	 * gif解码监听
	 * @param parseIsSuccess 解码是否成功，成功会为true
	 * @param frameIndex 当前解码的第几帧，当全部解码成功后，这里为-1
	 */
	public void parseOk(boolean parseIsSuccess, int frameIndex);
}
