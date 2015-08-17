package com.andy.framework.view.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * @description: 带进度条的WebView
 * @author: andy  
 * @mail: win58@qq.com
 * @date: 2015-8-14  下午3:38:58
 */
public class AndyProgressWebView extends WebView {
	private ProgressBar progressbar;

    @SuppressWarnings("deprecation")
	public AndyProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3, 0, 0));
        addView(progressbar);
        setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
        setWebChromeClient(new AndyWebChromeClient(progressbar));
    }

    

    @SuppressWarnings("deprecation")
	@Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }



	public ProgressBar getProgressbar() {
		return progressbar;
	}

}
class AndyWebChromeClient extends WebChromeClient {
	private ProgressBar progressbar;
	
	public AndyWebChromeClient(ProgressBar progressbar) {
		super();
		this.progressbar = progressbar;
	}
	
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress == 100) {
            progressbar.setVisibility(View.GONE);
        } else {
            if (progressbar.getVisibility() == View.GONE)
                progressbar.setVisibility(View.VISIBLE);
            progressbar.setProgress(newProgress);
        }
        super.onProgressChanged(view, newProgress);
    }
}