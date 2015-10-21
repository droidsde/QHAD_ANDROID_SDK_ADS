package com.qhad.ads.test;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qhad.ads.R;
import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageListener;

/**
 * Created by chengsiy on 2015/6/24.
 */
public class CustomLandingPageActivity extends Activity {

    public static String ldUrl;
    public static IQhLandingPageListener listener;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_landing_page);
        if (ldUrl == null) {
            finish();
            return;
        }
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setDatabasePath(webView.getContext().getFilesDir().getAbsolutePath() + "/databases/");
        }
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return listener.onAppDownload(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                listener.onPageLoadFinished();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                listener.onPageLoadFailed();
            }
        });
        webView.loadUrl(ldUrl);
    }

    @Override
    protected void onDestroy() {
        webView.loadUrl("about:blank");
        webView.destroy();
        webView = null;
        if (listener != null)
            listener.onPageClose();
        listener = null;
        super.onDestroy();
    }
}
