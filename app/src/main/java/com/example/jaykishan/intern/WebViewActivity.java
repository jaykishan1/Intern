package com.example.jaykishan.intern;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private String webUrl;

    private String cacheDir;

    private ProgressBar progress;

    private final String LOG_TAG = WebViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent=getIntent();
        webUrl=intent.getStringExtra(Intent.ACTION_MAIN);

        Log.v(LOG_TAG,webUrl);



        progress=(ProgressBar) findViewById(R.id.progressBar2);
        progress.setMax(100);


        webView=(WebView) findViewById(R.id.webview);
        init();

        if(MainActivity.hasActiveInternetConnection(WebViewActivity.this)) {
            Log.v(LOG_TAG,"Has Internet");
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        }
        else
        {
            Log.v(LOG_TAG,"No Internet");
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
        }

        webView.loadUrl("https://www.youtube.com");

    }

    private void init() {

        String cacheDir = getDir("webview1", Context.MODE_PRIVATE).getAbsolutePath();


        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setWebChromeClient(new AppCacheWebChromeClient());
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // Set cache size to 8 mb by default. should be more than enough
        webView.getSettings().setAppCacheMaxSize(1024*1024*10);

        // UPDATE: no hardcoded path. Thanks to Kevin Hawkins
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        Log.e(LOG_TAG, "appCachePath = " + cacheDir);
        webView.getSettings().setAppCachePath(cacheDir);
        webView.getSettings().setAllowFileAccess(true);

        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new MyBrowser());


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }



    private class MyBrowser extends WebViewClient {

        private final String LOG_TAG = MyBrowser.class.getSimpleName();

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            Log.v(LOG_TAG,"Load Started");
            webView.setVisibility(View.INVISIBLE);
            return false;
        }



        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

            Toast.makeText(WebViewActivity.this, "Check Internet Connection "+ error, Toast.LENGTH_LONG).show();



        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            progress.setVisibility(View.VISIBLE);
            //progress.setProgress(0);
            super.onPageStarted(view, url, favicon);

            Log.v(LOG_TAG,"Loading");
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progress.setVisibility(View.GONE);
            //progress.setProgress(100);
            webView.setVisibility(View.VISIBLE);
            Log.v(LOG_TAG,"PageFinished");

        }
    }



    private class AppCacheWebChromeClient extends WebChromeClient {


        @SuppressWarnings("deprecation")
        @Override
        public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
            Log.e(LOG_TAG, "onReachedMaxAppCacheSize reached, increasing space: " + spaceNeeded);
            quotaUpdater.updateQuota(spaceNeeded * 2);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            progress.setProgress(newProgress);
        }
    }




}
