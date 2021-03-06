package com.andy.framework.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;


/**
 * @description: 网络请求工具类
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-5-11  下午3:11:51
 */
public class AndyHttp {
	private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024; //8KB
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
    
    private static int maxConnections = 10; //http请求最大并发连接数
    private static int socketTimeout = 10 * 1000; //超时时间，默认10秒
    private static int maxRetries = 5; //错误尝试次数，错误异常表请在RetryHandler添加
    private static int httpThreadCount = 3; //http线程池数量
    
    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private String charset = "utf-8";
    
    private final Map<String, String> clientHeaderMap;
    
    private static final ThreadFactory threadFactory = new ThreadFactory() {
    	private final AtomicInteger mCount = new AtomicInteger(1);
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r, "AndyHttp # " + mCount.getAndIncrement());
			thread.setPriority(Thread.NORM_PRIORITY - 1);
			return thread;
		}
	};
	
	private static final Executor executor = Executors.newFixedThreadPool(httpThreadCount, threadFactory);
	
	
	public AndyHttp() {
		BasicHttpParams httpParams = new BasicHttpParams();
		
		ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
		ConnManagerParams.setTimeout(httpParams, socketTimeout);
		ConnManagerParams.setMaxTotalConnections(httpParams, maxConnections);
		
		HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);
		HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
		HttpConnectionParams.setTcpNoDelay(httpParams, true);
		
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		
		ThreadSafeClientConnManager tsccm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
		
		httpContext = new SyncBasicHttpContext(new BasicHttpContext());
		httpClient = new DefaultHttpClient(tsccm, httpParams);
		
		httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
				for (String headerString : clientHeaderMap.keySet()) {
					request.addHeader(headerString, clientHeaderMap.get(headerString));
				}
			}
		});
		
		httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context) {
				final HttpEntity entity = response.getEntity();
				if (entity == null) {
					return;
				}
				final Header header = entity.getContentEncoding();
				if (header != null) {
					for (HeaderElement element : header.getElements()) {
						if (ENCODING_GZIP.equalsIgnoreCase(element.getName())) {
							response.setEntity(new InflatingEntity(response.getEntity()));
							break;
						}
					}
				}
			}
		});
		
		httpClient.setHttpRequestRetryHandler(new AndyRetryHandler(maxRetries));
		
		clientHeaderMap = new HashMap<String, String>();
	}
	
	
	
	public DefaultHttpClient getHttpClient() {
		return httpClient;
	}

	public HttpContext getHttpContext() {
		return httpContext;
	}

/******************************* 配置信息  start **************************************************/
	/**
	 * 编码设置，默认UTF-8
	 * */
	public void configCharSet(String charSet) {
		if (charSet != null && charSet.trim().length() != 0) {
			this.charset = charSet;
		}
	}
	
	/**
	 * cookieStore配置
	 * */
	public void configCookieStore(CookieStore cookieStore) {
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}
	
	/**
	 * UserAgent配置
	 * */
	public void configUserAgent(String agent) {
		HttpProtocolParams.setUserAgent(httpClient.getParams(), agent);
	}
	
	/**
	 * 网络连接超时时间配置，默认10秒
	 * */
	public void configTimeout(int timeout) {
		final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, timeout);
        HttpConnectionParams.setSoTimeout(httpParams, timeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
	}
	
	/**
	 * 设置https请求时  的 SSLSocketFactory
	 * */
	public void configSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
		Scheme scheme = new Scheme("https", sslSocketFactory, 443);
		httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
	}
	
	/**
     * 配置错误重试次数
     */
    public void configRequestExecutionRetryCount(int count) {
    	this.httpClient.setHttpRequestRetryHandler(new AndyRetryHandler(count));
    }
    
   /**
    * 添加http请求头
    */
    public void addHeader(String header, String value) {
        clientHeaderMap.put(header, value);
    }
/******************************* 配置信息   end   **************************************************/
	
//------------------get 请求-----------------------
    public void get( String url, AndyHttpCallback<? extends Object> callBack) {
        get( url, null, callBack);
    }

    public void get( String url, AndyRequestParams params, AndyHttpCallback<? extends Object> callBack) {
        sendRequest(httpClient, httpContext, new HttpGet(getUrlWithQueryString(url, params)), null, callBack);
    }
    
    public void get( String url, Header[] headers, AndyRequestParams params, AndyHttpCallback<? extends Object> callBack) {
        HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
        if(headers != null) request.setHeaders(headers);
        sendRequest(httpClient, httpContext, request, null, callBack);
    }
    
    public Object getSync( String url) {
    	return getSync( url, null);
    }

    public Object getSync( String url, AndyRequestParams params) {
    	 HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
    	return sendSyncRequest(httpClient, httpContext, request, null);
    }
    
    
    public Object getSync( String url, Header[] headers, AndyRequestParams params) {
        HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
        if(headers != null) request.setHeaders(headers);
        return sendSyncRequest(httpClient, httpContext, request, null);
    }


//------------------post 请求-----------------------
    public void post(String url, AndyHttpCallback<? extends Object> callBack) {
        post(url, null, callBack);
    }

    public void post(String url, AndyRequestParams params, AndyHttpCallback<? extends Object> callBack) {
        post(url, paramsToEntity(params), null, callBack);
    }

    public void post( String url, HttpEntity entity, String contentType, AndyHttpCallback<? extends Object> callBack) {
        sendRequest(httpClient, httpContext, addEntityToRequestBase(new HttpPost(url), entity), contentType, callBack);
    }

    public <T> void post( String url, Header[] headers, AndyRequestParams params, String contentType,AndyHttpCallback<T> callBack) {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        if(params != null) request.setEntity(paramsToEntity(params));
        if(headers != null) request.setHeaders(headers);
        sendRequest(httpClient, httpContext, request, contentType, callBack);
    }

    public void post( String url, Header[] headers, HttpEntity entity, String contentType,AndyHttpCallback<? extends Object> callBack) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPost(url), entity);
        if(headers != null) request.setHeaders(headers);
        sendRequest(httpClient, httpContext, request, contentType, callBack);
    }
    
    
    public Object postSync(String url) {
    	return postSync(url, null);
    }
    
    public Object postSync(String url, AndyRequestParams params) {
    	return postSync(url, paramsToEntity(params), null);
    }
    
    public Object postSync( String url, HttpEntity entity, String contentType) {
    	return sendSyncRequest(httpClient, httpContext, addEntityToRequestBase(new HttpPost(url), entity), contentType);
    }
    
    
    public Object postSync( String url, Header[] headers, AndyRequestParams params, String contentType) {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        if(params != null) request.setEntity(paramsToEntity(params));
        if(headers != null) request.setHeaders(headers);
        return sendSyncRequest(httpClient, httpContext, request, contentType);
    }
    
    public Object postSync( String url, Header[] headers, HttpEntity entity, String contentType) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPost(url), entity);
        if(headers != null) request.setHeaders(headers);
        return sendSyncRequest(httpClient, httpContext, request, contentType);
    }
    

//------------------put 请求-----------------------

    public void put(String url, AndyHttpCallback<? extends Object> callBack) {
        put(url, null, callBack);
    }
   
    public void put( String url, AndyRequestParams params, AndyHttpCallback<? extends Object> callBack) {
        put(url, paramsToEntity(params), null, callBack);
    }

    public void put( String url, HttpEntity entity, String contentType, AndyHttpCallback<? extends Object> callBack) {
        sendRequest(httpClient, httpContext, addEntityToRequestBase(new HttpPut(url), entity), contentType, callBack);
    }
    
    public void put(String url,Header[] headers, HttpEntity entity, String contentType, AndyHttpCallback<? extends Object> callBack) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPut(url), entity);
        if(headers != null) request.setHeaders(headers);
        sendRequest(httpClient, httpContext, request, contentType, callBack);
    }
    
    public Object putSync(String url) {
    	return putSync(url, null);
    }
    
    public Object putSync( String url, AndyRequestParams params) {
        return putSync(url, paramsToEntity(params),null);
    }
    
    public Object putSync(String url, HttpEntity entity, String contentType) {
        return putSync(url,null, entity, contentType);
    }
    
    
    public Object putSync(String url,Header[] headers, HttpEntity entity, String contentType) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPut(url), entity);
        if(headers != null) request.setHeaders(headers);
        return sendSyncRequest(httpClient, httpContext, request, contentType);
    }

//------------------delete 请求-----------------------
    public void delete( String url, AndyHttpCallback<? extends Object> callBack) {
        final HttpDelete delete = new HttpDelete(url);
        sendRequest(httpClient, httpContext, delete, null, callBack);
    }
    
    public void delete( String url, Header[] headers, AndyHttpCallback<? extends Object> callBack) {
        final HttpDelete delete = new HttpDelete(url);
        if(headers != null) delete.setHeaders(headers);
        sendRequest(httpClient, httpContext, delete, null, callBack);
    }
    
    public Object deleteSync(String url) {
        return deleteSync(url,null);
    }
    
    public Object deleteSync( String url, Header[] headers) {
        final HttpDelete delete = new HttpDelete(url);
        if(headers != null) delete.setHeaders(headers);
        return sendSyncRequest(httpClient, httpContext, delete, null);
    }
    
//---------------------下载---------------------------------------
    /**
     * @param url： 下载地址
     * @param params：请求参数
     * @param target：保存路径
     * @param isResume：是否断点续传
     * @param callback：下载回调
     * */
    public AndyHttpHandler<File> download(String url,String target,AndyHttpCallback<File> callback){
    	return download(url, null, target, false, callback);
    }
    

    public AndyHttpHandler<File> download(String url,String target,boolean isResume,AndyHttpCallback<File> callback){
    	 return download(url, null, target, isResume, callback);
    }
    
    public AndyHttpHandler<File> download(String url,AndyRequestParams params, String target, AndyHttpCallback<File> callback) {
   	 	return download(url, params, target, false, callback);
    }
    
    public AndyHttpHandler<File> download(String url,AndyRequestParams params, String target,boolean isResume, AndyHttpCallback<File> callback) {
    	final HttpGet get =  new HttpGet(getUrlWithQueryString(url, params));
    	AndyHttpHandler<File> handler = new AndyHttpHandler<File>(httpClient, httpContext, callback,charset);
    	handler.executeOnExecutor(executor,get,target,isResume);
    	return handler;
    }


    protected <T> void sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, AndyHttpCallback<T> AndyHttpCallback) {
        if(contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        new AndyHttpHandler<T>(client, httpContext, AndyHttpCallback,charset).executeOnExecutor(executor, uriRequest);
    }
    
    protected Object sendSyncRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType) {
        if(contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }
        return new AndySyncRequestHandler(client, httpContext,charset).sendRequest(uriRequest);
    }

    public static String getUrlWithQueryString(String url, AndyRequestParams params) {
        if(params != null) {
            String paramString = params.getParamString();
            url += "?" + paramString;
        }
        return url;
    }

//----------------------  private method -------------------------------------------
    private HttpEntity paramsToEntity(AndyRequestParams params) {
        HttpEntity entity = null;

        if(params != null) {
            entity = params.getHttpEntity();
        }

        return entity;
    }

    private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase, HttpEntity entity) {
        if(entity != null){
            requestBase.setEntity(entity);
        }

        return requestBase;
    }

    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}
