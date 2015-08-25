package com.andy.framework.http;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import com.andy.framework.core.AndyAsyncTask;

import android.os.SystemClock;

/**
 * @description: AndyHttpHandler 继承AndyAsyncTask（修改了线程池属性，让并发线程按顺序执行）
 * @author: andy
 * @mail: win58@qq.com
 * @date: 2015-5-11 上午11:52:10
 */
public class AndyHttpHandler<T> extends AndyAsyncTask<Object, Object, Object>
		implements AndyEntityCallBack {
	private final static int UPDATE_START = 1;
	private final static int UPDATE_LOADING = 2;
	private final static int UPDATE_FAILURE = 3;
	private final static int UPDATE_SUCCESS = 4;

	private final AbstractHttpClient client;
	private final HttpContext httpContext;
	private final AndyStringReaderHandler stringReaderHandler = new AndyStringReaderHandler();
	private final AndyFileReaderHandler fileReaderHandler = new AndyFileReaderHandler();
	private final AndyHttpCallback<T> callback;
	// 字符编码
	private String charSet;

	private String targetUri = null; // 下载文件路径
	private boolean isResume = false; // 是否断点续传
	private int executeCount = 0; // 执行次数
	private long time; // 记录更新时间

	public AndyHttpHandler(AbstractHttpClient client, HttpContext context,
			AndyHttpCallback<T> callback, String charSetString) {
		this.client = client;
		this.httpContext = context;
		this.callback = callback;
		this.charSet = charSetString;
	}

	@Override
	protected Object doInBackground(Object... params) {
		if (params != null && params.length == 3) {
			targetUri = String.valueOf(params[1]);
			isResume = (Boolean) params[2];
		}
		try {
			publishProgress(UPDATE_START);
			makeRequestWithRetry((HttpUriRequest) params[0]);
		} catch (IOException e) {
			publishProgress(UPDATE_FAILURE, e, 0, e.getMessage());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onProgressUpdate(Object... values) {
		if (callback != null) {
			int updataStatus = Integer.parseInt(String.valueOf(values[0]));
			switch (updataStatus) {
			case UPDATE_START:
				callback.onStart();
				break;
			case UPDATE_LOADING:
				callback.onLoading(Long.valueOf(String.valueOf(values[1])),
						Long.valueOf(String.valueOf(values[2])));
				break;
			case UPDATE_SUCCESS:
				callback.onSuccess((T) values[1]);
				break;
			case UPDATE_FAILURE:
				callback.onFailure((Throwable) values[1], (Integer) values[2], (String) values[3]);
				break;
			default:
				break;
			}
		}
		super.onProgressUpdate(values);
	}

	@Override
	public void callBack(long totalCount, long currentCount,
			boolean mustNoticeUI) {
		if (callback != null && callback.isProgress()) {
			if (mustNoticeUI) {
				publishProgress(UPDATE_LOADING, totalCount, currentCount);
			} else {
				long thisTime = SystemClock.uptimeMillis();
				if (thisTime - time >= callback.getRate()) {
					time = thisTime;
					publishProgress(UPDATE_LOADING, totalCount, currentCount);
				}
			}
		}
	}

	/**
	 * @param stop 停止下载任务
	 */
	public boolean isStop() {
		return fileReaderHandler.isStop();
	}

	public void stop() {
		fileReaderHandler.setStop(true);
	}

	private void makeRequestWithRetry(HttpUriRequest request) throws IOException {
		// 断点续传并且路径不为空，文件下载
		if (isResume && targetUri != null) {
			File targetFile = new File(targetUri);
			long fileLen = 0;
			if (targetFile.isFile() && targetFile.exists()) {
				fileLen = targetFile.length();
			}
			if (fileLen > 0) {
				request.setHeader("RANGE", "bytes=" + fileLen + "-");
			}
		}
		boolean retry = true;
		IOException cause = null;
		HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
		while (retry) {
			try {
				if (!isCancelled()) {
					HttpResponse response = client.execute(request, httpContext);
					if (!isCancelled()) {
						handleResponse(response);
					}
				}
				return;
			} catch (UnknownHostException e) {
				publishProgress(UPDATE_FAILURE, e, 0, "unknownHostException：can't resolve host");
			} catch (IOException e) {
				cause = e;
				retry = retryHandler.retryRequest(cause, ++executeCount, httpContext);
			} catch (NullPointerException e) {
				cause = new IOException("NullPointerException in HttpClient " + e.getMessage());
				retry = retryHandler.retryRequest(cause, ++executeCount, httpContext);
			} catch (Exception e) {
				cause = new IOException("Exception " + e.getMessage());
				retry = retryHandler.retryRequest(cause, ++executeCount, httpContext);
			}
		}
		if (cause != null) {
			throw cause;
		} else {
			throw new IOException("未知网络错误");
		}
	}

	private void handleResponse(HttpResponse response) {
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() > 300) {
			String errorMsg = "response status error code:" + status.getStatusCode();
			if (status.getStatusCode() == 416 && isResume) {
				errorMsg += " \n maybe you have download complete.";
			}
			publishProgress(
					UPDATE_FAILURE,
					new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()), 
					status.getStatusCode(), errorMsg);
		} else {
			HttpEntity entity = response.getEntity();
			Object object = null;
			try {
				if (entity != null) {
					time = SystemClock.uptimeMillis();
					if (targetUri != null) {
						object = fileReaderHandler.readData(entity, this, targetUri, isResume);
					} else {
						object = stringReaderHandler.readData(entity, this, charSet);
					}
				}
				publishProgress(UPDATE_SUCCESS, object);
			} catch (IOException e) {
				publishProgress(UPDATE_FAILURE, e, 0, e.getMessage());
			}
		}
	}
}
