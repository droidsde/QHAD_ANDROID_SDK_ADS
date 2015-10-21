package com.qhad.ads.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qhad.ads.sdk.core.BridgeMiddleware;

@SuppressWarnings("unused")
public class InterTest extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout rl = new LinearLayout(this);
        rl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        rl.setOrientation(LinearLayout.VERTICAL);

        setContentView(rl);

        setTitle("插屏测试页");

        Button btn = new Button(this);
        btn.setText("返回主页");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                jump();
            }
        });
        LinearLayout.LayoutParams btnlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        btnlp.setMargins(0, 200, 0, 0);
        rl.addView(btn, btnlp);

        RelativeLayout adContainer = new RelativeLayout(this);
        ViewGroup.LayoutParams adclp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.addView(adContainer, adclp);

//        initSimpleBanner(adContainer);
        initSimpleInterstitial();
//		initFloatBanner();
    }

    private void jump() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initFloatBanner() {
        final String adSpaceid = "Pk5vPms1iR";
        BridgeMiddleware.initSimpleFloatbanner(this, adSpaceid, false, 2, 0);
    }

    private void initSimpleBanner(ViewGroup adContainer) {
        final String adSpaceid = "5uavuInDAl";
        BridgeMiddleware.initSimpleBanner(adContainer, this, adSpaceid, false);
    }

    private void initSimpleInterstitial() {
        final String adSpaceid = "FFabFd8Xp7";
        BridgeMiddleware.initSimpleInterstitial(this, adSpaceid, false);
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
//		BridgeMiddleware.activityDestroy(this);
    }
}
