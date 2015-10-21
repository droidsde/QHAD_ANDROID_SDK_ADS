package com.qhad.ads.sdk.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.provider.Browser;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageListener;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.utils.Utils;

import java.net.URISyntaxException;

@SuppressLint("SetJavaScriptEnabled")
public class LandingPageLayout extends RelativeLayout {

    public String advertiserid = "";
    public String campaignid = "";
    public String solutionid = "";
    public String bannerid = "";
    public String impid = "";
    public IQhLandingPageListener landingPageListener;
    private String navUrl;
    private WebView webView = null;
    private ProgressBar progressBar = null;

    @SuppressWarnings("deprecation")
    @TargetApi(8)
    public LandingPageLayout(Context context) {
        super(context);
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        double density = Utils.getDeviceDensity();
        int pbheight = (int) density * 5;
        LayoutParams pblp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                pbheight);
        pblp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        RelativeLayout ly = new RelativeLayout(context);


        LayoutParams weblp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        weblp.addRule(RelativeLayout.BELOW, 1);
        webView = new WebView(context);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setDatabasePath(webView.getContext().getFilesDir().getAbsolutePath() + "/databases/");
        }
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                landingPageListener.onPageLoadFinished();
                progressBar.setVisibility(View.GONE);
                view.loadUrl("javascript:document.body.style.margin=0");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                QHADLog.e(QhAdErrorCode.COMMON_ERROR, String.format("landingPage onReceivedError.errorCode:%s,desc:%s,failingUrl:%s", errorCode, description, failingUrl));
                landingPageListener.onPageLoadFailed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if (landingPageListener.onAppDownload(url))
                        return true;
                } catch (Exception e) {
                    QHADLog.e(QhAdErrorCode.COMMON_ERROR, "unable process tryStartDownload:" + url, e);
                }
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false;
                }

                Intent intent;
                // Perform generic parsing of the URI to turn it into an Intent.
                try {
                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                } catch (URISyntaxException ex) {
                    QHADLog.e(QhAdErrorCode.CLICK_LANDINGPAGE_DEEPLINK_CALL_ERROR, "DeepLink Bad URI:" + url, ex);
                    return true;
                }
                // Sanitize the Intent, ensuring web pages can not bypass browser
                // security (only access to BROWSABLE activities).
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setComponent(null);
                // Pass the package name as application ID so that the intent from the
                // same application can be opened in the same tab.
                intent.putExtra(Browser.EXTRA_APPLICATION_ID,
                        view.getContext().getPackageName());
                try {
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    QHADLog.e(QhAdErrorCode.CLICK_LANDINGPAGE_DEEPLINK_CALL_ERROR, "No application can handle " + url, ex);
                    return true;
                }
                return true;

            }


        });

        ly.addView(webView, weblp);
        ly.addView(progressBar, pblp);

        LinearLayout controlBar = new LinearLayout(context);
        LinearLayout controlarea = new LinearLayout(context);
        int cbheight = (int) (40 * density);
        LayoutParams lllpm = new LayoutParams(LayoutParams.MATCH_PARENT, cbheight);
        controlBar.setOrientation(LinearLayout.HORIZONTAL);
        controlBar.setBackgroundColor(Color.parseColor("#EAEAEC"));
        controlBar.setGravity(Gravity.CENTER_VERTICAL);
        lllpm.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        //noinspection ResourceType
        controlBar.setId(1);

        ImageButton button = new ImageButton(context);
        try {
            BitmapDrawable bd = new BitmapDrawable(this.getResources(), BitmapFactory.decodeStream(context.getAssets().open("qh_ad_back.jpg")));
            button.setBackgroundDrawable(bd);
        } catch (Exception e) {
            QHADLog.d("获取图片资源失败 资源:Close Error:" + e.getMessage());
        }
        button.setScaleType(ScaleType.FIT_XY);


        int bw = (int) (20 * density * 0.5);
        int bh = (int) (35 * density * 0.5);
        LinearLayout.LayoutParams btnllpm = new LinearLayout.LayoutParams(bw, bh);
        btnllpm.leftMargin = (int) (10 * density);
        controlarea.addView(button, btnllpm);

        TextView backTV = new TextView(context);
        backTV.setText("返回");
        backTV.setTextSize(20);
        backTV.setTextColor(Color.parseColor("#414143"));
        LinearLayout.LayoutParams backtvllpm = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        backtvllpm.leftMargin = 5;
        backTV.setGravity(Gravity.CENTER_VERTICAL);


        controlarea.setOrientation(LinearLayout.HORIZONTAL);
        controlarea.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams callpms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);


        controlarea.addView(backTV, backtvllpm);
        controlBar.addView(controlarea, callpms);


        controlarea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                closeLanding();
            }
        });

        ly.addView(controlBar, lllpm);


        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        this.addView(ly, params);

    }

    @Deprecated
    public LandingPageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Deprecated
    public LandingPageLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || Utils.isEmpty(str);
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        switch (event.getKeyCode()) {
//            case KeyEvent.KEYCODE_BACK:
//                if (event.getAction() == KeyEvent.ACTION_UP) {
//                    closeLanding();
//                }
//            case KeyEvent.KEYCODE_MENU:
//            default:
//                break;
//        }
//        return super.dispatchKeyEvent(event);
//    }

    void closeLanding() {
        Activity activity = (Activity) getContext();
        if (activity == null) {
            QHADLog.d("activity is null");
            return;
        }
        InputMethodManager methodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        methodManager.hideSoftInputFromInputMethod(webView.getWindowToken(), 0);
        webView.loadUrl("about:blank");
        removeAllViews();
        webView.destroy();
        activity.finish();
        if (landingPageListener != null) {
            landingPageListener.onPageClose();
        }
    }

    public void showLanding(String url) {
        navUrl = url;
        webView.loadUrl(url);
        QHADLog.d("打开落地页 URL=" + url);
    }
}
