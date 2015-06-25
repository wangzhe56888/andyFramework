package com.andy.myself.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.andy.myself.R;
import com.andy.myself.base.BaseActivity;
import com.andy.view.webView.ProgressWebView;

/**
 * 项目名称 ： 
 * 类名称 ： WebViewActivity
 * 类描述 ： 
 * 创建时间 ： 2014-8-7下午08:53:24
 * email : win58@qq.com
 * nickname : Andy
 * @author wangys
 */
public class WebViewActivity extends BaseActivity {

	private ProgressWebView progressWebview;
	private String url = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_webview);
		
		initView(savedInstanceState);
	}
	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		titleEidtView.setVisibility(View.VISIBLE);
		titleRightView.setVisibility(View.VISIBLE);
		url = titleEidtView.getText().toString();
		
		titleTextView.setText(R.string.activity_webview_progress_title);
		titleTextView.setVisibility(View.GONE);
		
		progressWebview = (ProgressWebView) findViewById(R.id.progress_webview_id);
		progressWebview.getSettings().setJavaScriptEnabled(true);
		progressWebview.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);  
				return true;
			}
		});
		
		progressWebview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                if (url != null && url.startsWith("http://"))
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });

		progressWebview.loadUrl(url);
		
		titleRightView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyBoard(mActivity, v);
				url = titleEidtView.getText().toString();
				progressWebview.loadUrl(url);
			}
		});
	}
	
	public void hideKeyBoard(Context context, View view) {
		try {
			InputMethodManager im = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
			IBinder windowToken = view.getWindowToken();
			if (windowToken != null) {
				im.hideSoftInputFromWindow(windowToken, 0);
			}
		} catch (Exception e) {

		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (progressWebview.canGoBack()) {
				progressWebview.goBack();
				 return true;
			} else {
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
