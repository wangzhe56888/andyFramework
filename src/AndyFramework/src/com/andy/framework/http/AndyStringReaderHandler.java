package com.andy.framework.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

/**
 * @description: 字符串数据流读取工具类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-7  下午4:49:52
 */
public class AndyStringReaderHandler {

	/**
	 * 字符串读取
	 * @param entity 
	 * @param callBack
	 * @param charSet 字符编码
	 * 
	 * */
	public Object readData(HttpEntity entity, AndyEntityCallBack callBack, String charSet) {
		if (entity == null) {
			return null;
		}
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		
		long count = entity.getContentLength();
		long currentCount = 0;
		int len = -1;
		try {
			InputStream is = entity.getContent();
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
				currentCount += len;
				if (callBack != null) {
					callBack.callBack(count, currentCount, false);
				}
			}
			if (callBack != null) {
				callBack.callBack(count, currentCount, true);
			}
			byte[] data = os.toByteArray();
			os.close();
			is.close();
			
			return new String(data, charSet);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
