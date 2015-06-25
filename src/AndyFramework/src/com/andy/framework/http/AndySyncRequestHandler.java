package com.andy.framework.http;

import java.io.IOException;
import java.net.UnknownHostException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

/**
 * @description: 
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-11  上午11:12:46
 */
public class AndySyncRequestHandler {

	private final AbstractHttpClient client;
	private final HttpContext httpContext;
	private final AndyStringReaderHandler stringReaderHandler = new AndyStringReaderHandler();
	
	private String charSet;
	// 执行次数
	private int executeCount = 0;
	
	public AndySyncRequestHandler(AbstractHttpClient client, HttpContext context, String charSet) {
		this.client = client;
		this.httpContext = context;
		this.charSet = charSet;
	}
	
	private Object makeRequestWithRetry(HttpUriRequest request) throws IOException {
		boolean retry = true;
		IOException exception = null;
		
		while (retry) {
			HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
			try {
				HttpResponse response = client.execute(request, httpContext);
				return stringReaderHandler.readData(response.getEntity(), null, charSet);
			} catch(UnknownHostException e) {
				exception = e;
				retryHandler.retryRequest(exception, ++executeCount, httpContext);
			} catch (IOException e) {
				exception = e;
				retryHandler.retryRequest(exception, ++executeCount, httpContext);
			} catch (NullPointerException e) {
				exception = new IOException("NullPointerException in httpClient " + e.getMessage());
				retryHandler.retryRequest(exception, ++executeCount, httpContext);
			} catch (Exception e) {
				exception = new IOException("Exception " + e.getMessage());
				retryHandler.retryRequest(exception, ++executeCount, httpContext);
			}
		}
		if (exception != null) {
			throw exception;
		} else {
			throw new IOException("未知网络错误");
		}
	}
	
	public Object sendRequest(HttpUriRequest... params) {
		try {
			return makeRequestWithRetry(params[0]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
