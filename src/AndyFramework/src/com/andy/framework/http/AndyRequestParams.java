package com.andy.framework.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

/**
 * @description: 请求参数封装类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-8  上午9:17:20
 */
public class AndyRequestParams {

	// 默认编码方式
	private final String ENCODE = "UTF-8";
	
	private String encode = ENCODE;
	protected ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<String, String>();
	protected ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap<String, FileWrapper>();
	
	/**
	 * 构造函数
	 * */
	public AndyRequestParams() {}
	
	/**
	 * 构造函数，通过map初始化
	 * @param paramMap
	 * */
	public AndyRequestParams(Map<String, String> paramMap) {
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	/** 
	 * 单个参数 key-value 
	 * @param key
	 * @param value
	 **/
	public AndyRequestParams(String key, String value) {
		put(key, value);
	}
	/** 多参数传递 kay-value,key-value... */
	public AndyRequestParams(Object...objects) {
		int objectsLength = objects.length;
		if (objectsLength % 2 != 0) {
			new IllegalArgumentException("param is invalidate, must be key-value...");
		}
		for (int i = 0; i < objectsLength; i += 2) {
			put(String.valueOf(objects[i]), String.valueOf(objects[i+1]));
		}
	}
	
	/** 
	 * put参数的方法  key-value都为String
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
		if (key != null && value != null) {
			urlParams.put(key, value);
		}
	}
	
	/** 
	 * put 文件的方法
	 * @param key 
	 * @param inputStream
	 * @param fileName
	 * @param contentType
	 */
	public void put(String key, InputStream inputStream, String fileName, String contentType) {
		if (key != null && inputStream != null) {
			fileParams.put(key, new FileWrapper(inputStream, fileName, contentType));
		}
	}
	
	public void put(String key, InputStream inputStream, String fileName) {
		put(key, inputStream, fileName, null);
	}
	
	public void put(String key, File file) throws FileNotFoundException {
		put(key, new FileInputStream(file), file.getName());
	}
	
	public void put(String key, InputStream inputStream) {
		put(key, inputStream, null);
	}
	
	/**
	 * 根据key移除参数
	 * */
	public void remove(String key) {
		urlParams.remove(key);
		fileParams.remove(key);
	}
	
	/**
	 * 重写toString方法，根据传递的参数拼接成url参数
	 * */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue());
		}
		for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(entry.getKey()).append("=").append("FILE");
		}
		return sb.toString();
	}
	
	/**
	 * get httpEntity实例
	 * 如果fileParams为空则创建UrlEncodedFormEntity实例
	 * 否则创建AndyMultipartEntity
	 * */
	public HttpEntity getHttpEntity() {
		HttpEntity entity = null;
		if (!fileParams.isEmpty()) {
			AndyMultipartEntity multipartEntity = new AndyMultipartEntity();
			
			for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
				multipartEntity.addPart(entry.getKey(), entry.getValue());
			}
			
			int currentIndex = 1;
			int index = fileParams.size();
			
			for(ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
				FileWrapper fileWrapper = entry.getValue();
				if (fileWrapper.inputStream != null) {
					boolean isLast = currentIndex == index;
					if (fileWrapper.contentType != null) {
						multipartEntity.addPart(entry.getKey(), fileWrapper.fileName, fileWrapper.inputStream, fileWrapper.contentType, isLast);
					} else {
						multipartEntity.addPart(entry.getKey(), fileWrapper.fileName, fileWrapper.inputStream, isLast);
					}
				}
				currentIndex ++;
			}
			entity = multipartEntity;
		} else {
			try {
				entity = new UrlEncodedFormEntity(getParamsList(), encode);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		return entity;
	}
	
	protected List<BasicNameValuePair> getParamsList() {
		List<BasicNameValuePair> basicNameValuePairs = new LinkedList<BasicNameValuePair>();
		for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
			basicNameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return basicNameValuePairs;
	}
	
	public String getParamString() {
		return URLEncodedUtils.format(getParamsList(), encode);
	}
	
	public void setEncode(String encode) {
		this.encode = encode;
	}

	/**
	 * 文件封装类
	 * */
	class FileWrapper {
		public InputStream inputStream;
		public String fileName;
		public String contentType;
		
		public FileWrapper(InputStream inputStream, String fileName, String contentType) {
			this.inputStream = inputStream;
			this.fileName = fileName;
			this.contentType = contentType;
		}
		
		public String getFileName() {
			if (fileName != null) {
				return fileName;
			} else {
				return "nofilename";
			}
		}
	}
}
