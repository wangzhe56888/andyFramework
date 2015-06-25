package com.andy.framework.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

/**
 * @description: HttpRequestRetryHandler
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-11  上午10:29:26
 */
public class AndyRetryHandler implements HttpRequestRetryHandler {
	// 重试睡眠时间
	private static final int RETRY_SLEEP_TIME_MILLIS = 1000;
	// 重试白名单，网络异常重试
	private static HashSet<Class<?>> exceptionWhiteSet = new HashSet<Class<?>>();
	// 重试黑名单，用户主动取消
	private static HashSet<Class<?>> exceptionBlackSet = new HashSet<Class<?>>();
	// 重试次数
	private final int maxRetries;
	
	static {
		exceptionWhiteSet.add(NoHttpResponseException.class);
		exceptionWhiteSet.add(SocketException.class);
		exceptionWhiteSet.add(UnknownHostException.class);
		
		exceptionBlackSet.add(InterruptedIOException.class);
		exceptionBlackSet.add(SSLHandshakeException.class);
	}
	
	public AndyRetryHandler(int maxRetries) {
		this.maxRetries = maxRetries;
	}
	
	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		boolean retryFlag = true;
		
		Boolean b = (Boolean) context.getAttribute(ExecutionContext.HTTP_REQ_SENT);
		boolean sentedFlag = b != null && b.booleanValue();
		
		if (executionCount > maxRetries) {
			retryFlag = false;
		} else if (exceptionBlackSet.contains(exception.getClass())) {
			retryFlag = false;
		} else if (exceptionWhiteSet.contains(exception.getClass())) {
			retryFlag = true;
		} else if (!sentedFlag) {
			retryFlag = true;
		}
		
		if (retryFlag) {
			HttpUriRequest request = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
			retryFlag = request != null && !"POST".equals(request.getMethod());
		}
		
		if (retryFlag) {
			SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
		} else {
			exception.printStackTrace();
		}
		return retryFlag;
	}
}
