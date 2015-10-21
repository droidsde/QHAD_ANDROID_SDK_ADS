package com.qhad.ads.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.qhad.ads.R;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAd;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdListener;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdLoader;
import com.qhad.ads.sdk.core.BridgeMiddleware;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.SwitchConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HtmlActivity extends Activity {

    HashMap<String, Object> adlist = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);

        final WebView webView = (WebView) findViewById(R.id.webViewAd);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.loadUrl("http://mba-sandbox.fenxi.com/t.html");
        webView.addJavascriptInterface(new QHJavaScriptInterface(), "qhad");

        final String adSpaceid = "PPub5d0djn";
        SwitchConfig.LOG = true;
        IQhNativeAdLoader nativeAdLoader = BridgeMiddleware.initNativeAdLoader(this, adSpaceid, new IQhNativeAdListener() {

            @Override
            public void onNativeAdLoadSucceeded(ArrayList<IQhNativeAd> nativeAds) {
                // TODO Auto-generated method stub

                for (IQhNativeAd ad : nativeAds) {
                    JSONObject obj = ad.getContent();
                    String hc = String.valueOf(ad.hashCode());
                    try {
                        obj.put("id", hc);
                        adlist.put(hc, ad);
                        webView.loadUrl("javascript:addad(" + obj.toString() + ")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onNativeAdLoadFailed() {
                QHADLog.e("NativeAd Loader Failed");
            }
        }, false);


        nativeAdLoader.loadAds();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_html, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class QHJavaScriptInterface {

        @JavascriptInterface
        public void onimp(String id) {
            if (adlist.containsKey(id)) {
                IQhNativeAd ad = (IQhNativeAd) adlist.get(id);
                ad.onAdShowed();
            }
        }

        @JavascriptInterface
        public void onClick(String id) {
            QHADLog.d("click:" + id);
            if (adlist.containsKey(id)) {
                QHADLog.d("found:" + id);
                IQhNativeAd ad = (IQhNativeAd) adlist.get(id);
                ad.onAdClicked();
            } else {
                QHADLog.d("not found:" + id);
            }
        }

    }
}
