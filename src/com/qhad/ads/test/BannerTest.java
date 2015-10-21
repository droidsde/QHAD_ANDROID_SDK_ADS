package com.qhad.ads.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.qhad.ads.sdk.core.BridgeMiddleware;
import com.qhad.ads.sdk.core.QhNativeAdLoader;
import com.qhad.ads.sdk.logs.QHADLog;

import java.util.HashMap;

@SuppressWarnings("unused")
public class BannerTest extends Activity {

    private QhNativeAdLoader ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LinearLayout rl = new LinearLayout(this);
        rl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        rl.setOrientation(LinearLayout.VERTICAL);

        setContentView(rl);

        setTitle("横幅测试页");


        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rl.addView(scrollView, scrollp);


        LinearLayout mrl = new LinearLayout(this);
        LinearLayout.LayoutParams mrllp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mrl.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(mrl, mrllp);


        Button btn = new Button(this);
        btn.setText("返回主页");

        final Context context = this;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                jump();
            }
        });
        LinearLayout.LayoutParams btnlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnlp.setMargins(0, 10000, 0, 0);
        mrl.addView(btn, btnlp);

        LinearLayout adContainer = new LinearLayout(this);
        LinearLayout.LayoutParams adlp = new LinearLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


        initSimpleBanner(adContainer);

        mrl.addView(adContainer, adlp);
        initSimpleInterstitial();
        initFloatBanner();

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("a", "b");
        map.put("d", "3");

    }

    private void jump() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initFloatBanner() {
        final String adSpaceid = "5uavuInDAl";
        BridgeMiddleware.initSimpleFloatbanner(this, adSpaceid, false, 4, 1);
    }

    private void initSimpleBanner(ViewGroup adContainer) {
        final String adSpaceid = "PaP6u9PXg4";
        BridgeMiddleware.initSimpleBanner(adContainer, this, adSpaceid, false);
    }

    private void initSimpleInterstitial() {
        final String adSpaceid = "5PaG5JFiha";
        BridgeMiddleware.initSimpleInterstitial(this, adSpaceid, false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QHADLog.e("onDestroy");
        BridgeMiddleware.activityDestroy(this);
    }
}