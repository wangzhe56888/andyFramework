package com.andy.framework.bitmap.download;
/**
 * @description: 下载接口
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-12  下午5:59:33
 */
public interface AndyDownLoader {
	/**
	 * 请求网络的inputStream填充outputStream
	 * @param urlString
	 * @return byte[]
	 */
	public byte[] download(String urlString);
}
