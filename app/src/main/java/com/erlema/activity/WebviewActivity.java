package com.erlema.activity;

import android.annotation.SuppressLint;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.erlema.bean.MySubject;
import com.example.erlema.R;

public class WebviewActivity extends BaseActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private String url = "http://bxtyfufff.3vhost.net/jieshao/index.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_webview);
        url = getBundle().getString("url");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initViews() {
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.wv_web);
        webView.loadUrl(url);
        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        MyWebChromeClient myWebChromeClient = new MyWebChromeClient();
        webView.setWebChromeClient(myWebChromeClient);
    }

    @Override
    public void initListeners() {

    }

    @Override
    public void initData() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                WebviewActivity.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyWebChromeClient extends WebChromeClient {

        //获得网页的加载进度，显示在右上角的TextView控件中
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress < 100) {
                progressBar.setProgress(newProgress);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }

        //获得网页的标题，作为应用程序的标题进行显示
        public void onReceivedTitle(WebView view, String title) {
            getSupportActionBar().setTitle(title);

        }

    }
}
