package com.andy.framework.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpEntity;

import android.text.TextUtils;

/**
 * @description: 文件流读取
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-7  下午5:23:35
 */
public class AndyFileReaderHandler {
	private boolean isStop = false;
	
	public boolean isStop() {
		return isStop;
	}
	
	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}
	
	/**
	 * 从entity读取数据
	 * @param entity
	 * @param callBack
	 * @param path 文件保存的路径
	 * @param isResume 是否断点续传
	 * @throws IOException 
	 * */
	@SuppressWarnings("resource")
	public Object readData(HttpEntity entity, AndyEntityCallBack callBack, String path, boolean isResume) throws IOException {
		if (TextUtils.isEmpty(path) || path.trim().length() == 0) {
			return null;
		}
		
		File targetFile = new File(path);
		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}
		
		if (isStop) {
			return targetFile;
		}
		
//		long currentCount = 0;
//		FileOutputStream fos = null;
//		if (isResume) {
//			fos = new FileOutputStream(targetFile, true);
//			currentCount = targetFile.length();
//		} else {
//			fos = new FileOutputStream(targetFile);
//		}
//		
//		if (isStop) {
//			return targetFile;
//		}
//		
//		InputStream iStream = entity.getContent();
//		long totalCount = entity.getContentLength() + currentCount;
//		
//		if (currentCount >= totalCount || isStop) {
//			return targetFile;
//		}
//		
//		int readLen = 0;
//		byte[] buffer = new byte[1024];
//		while (!isStop && currentCount < totalCount && (readLen = iStream.read(buffer, 0, 1024)) > 0) {
//			fos.write(buffer, 0, readLen);
//			currentCount += readLen;
//			if (callBack != null) {
//				callBack.callBack(totalCount, currentCount, false);
//			}
//		}
//		
//		if (callBack != null) {
//			callBack.callBack(totalCount, currentCount, true);
//		}
//		// 主动停止下载线程	
//		if (isStop && currentCount < totalCount) {
//			throw new IOException("user stop download file");
//		}
//		return targetFile;
		
		
		/**
		 * 解决断点续传，下载文件暂停后继续下载会不断增大的bug
		 * */
		long currentCount = 0;
		RandomAccessFile file = new RandomAccessFile(targetFile, "rw");
		if (isResume) {
			currentCount = file.length();
		}
		
		InputStream iStream = entity.getContent();
		long totalCount = entity.getContentLength() + currentCount;
		
		if (isStop) {
			file.close();
			return targetFile;
		}
		
		 // 在这里其实这样写是不对的，之所以如此是为了用户体验，谁都不想自己下载时进度条都走了一大半了，就因为一个暂停一下子少了一大串
        /**
         * 这里实际的写法应该是： <br>
         * current = input.skip(current); <br>
         * file.seek(current); <br>
         * 根据JDK文档中的解释：Inputstream.skip(long i)方法跳过i个字节，并返回实际跳过的字节数。<br>
         * 导致这种情况的原因很多，跳过 n 个字节之前已到达文件末尾只是其中一种可能。这里我猜测可能是碎片文件的损害造成的。
         */
        file.seek(iStream.skip(currentCount));
		
		int readLen = 0;
		byte[] buffer = new byte[1024];
		while (!isStop && currentCount < totalCount && (readLen = iStream.read(buffer, 0, 1024)) > 0) {
			file.write(buffer, 0, readLen);
			currentCount += readLen;
			if (callBack != null) {
				callBack.callBack(totalCount, currentCount, false);
			}
		}
		
		if (callBack != null) {
			callBack.callBack(totalCount, currentCount, true);
		}
		// 主动停止下载线程	
		if (isStop && currentCount < totalCount) {
			file.close();
			throw new IOException("user stop download file");
		}
		file.close();
		return targetFile;
	}
}
