package com.qhad.ads.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.qhad.ads.sdk.adsinterfaces.IQhAdEventListener;
import com.qhad.ads.sdk.core.BridgeMiddleware;
import com.qhad.ads.sdk.logs.QHADLog;

import java.util.HashMap;

/**
 * Created by Administrator on 2015/9/11.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout rl = new RelativeLayout(this);
        rl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(rl);

        setTitle("开屏测试页");

        Button btn = new Button(this);
        btn.setText("返回主页");

        final Context context = this;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                jump();
            }
        });

        RelativeLayout.LayoutParams btnlp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rl.addView(btn, btnlp);

        LinearLayout adContainer = new LinearLayout(this);
        RelativeLayout.LayoutParams adlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        adlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        initSplash(adContainer);

        rl.addView(adContainer, adlp);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("a", "b");
        map.put("d", "3");

    }

    private void jump() {
        finish();
    }

    private void initFloatBanner() {
        final String adSpaceid = "5uavuInDAl";
        BridgeMiddleware.initSimpleFloatbanner(this, adSpaceid, false, 4, 1);
    }

    private void initSimpleBanner(ViewGroup adContainer) {
        final String adSpaceid = "5uavuInDAl";
        BridgeMiddleware.initSimpleBanner(adContainer, this, adSpaceid, false);
    }

    private void initSplash(ViewGroup adContainer) {

        IQhAdEventListener listener = new IQhAdEventListener() {
            @Override
            public void onAdviewGotAdSucceed() {

            }

            @Override
            public void onAdviewGotAdFail() {

            }

            @Override
            public void onAdviewRendered() {
                QHADLog.e("Splash onAdviewRendered was called");
            }

            @Override
            public void onAdviewIntoLandpage() {

            }

            @Override
            public void onAdviewDismissedLandpage() {

            }

            @Override
            public void onAdviewClicked() {

            }

            @Override
            public void onAdviewClosed() {
                QHADLog.e("Splash Over!!!!!!!!!");
                jump();
            }

            @Override
            public void onAdviewDestroyed() {

            }
        };

        final String adSpaceid = "uF5l59kX2V";
        BridgeMiddleware.initSplash(adContainer, this, adSpaceid, listener, true, false);
    }

    private void initSimpleInterstitial() {
        final String adSpaceid = "u5aQadl4Xp";
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