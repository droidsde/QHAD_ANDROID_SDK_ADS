package com.qhad.ads.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qhad.ads.R;
import com.qhad.ads.sdk.core.BridgeMiddleware;
import com.qhad.ads.sdk.res.SwitchConfig;

@SuppressWarnings("unused")
public class NativeBannerTest extends Activity {

    private LinearLayout.LayoutParams nativebanneradlp;
    private LinearLayout nativebannerAdContainer;
    private RelativeLayout rlContainer;
    private RadioButton rbAspRate;
    private RadioButton rbWh;
    private int containerWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SwitchConfig.LOG = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nativebanner);

        setTitle("单品测试页");


        rbAspRate = (RadioButton) findViewById(R.id.rbAspRate);
        rbWh = (RadioButton) findViewById(R.id.rbWh);
        rbWh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rbAspRate.setChecked(!isChecked);
            }
        });
        rbAspRate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rbWh.setChecked(!isChecked);
            }
        });
        rlContainer = (RelativeLayout) findViewById(R.id.rlContainer);
        rlContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                containerWidth = rlContainer.getMeasuredWidth();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jump();
            }
        });

        findViewById(R.id.btnLoad).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBannerAds();
            }
        });
    }

    private void addBannerAds() {

        boolean isAspRate = rbAspRate.isChecked();
        boolean isWh = rbWh.isChecked();
        int height, width;
        height = 0;
        width = 0;
        if (isAspRate) {
            EditText etAspW = (EditText) findViewById(R.id.etAspRateW);
            EditText etAspH = (EditText) findViewById(R.id.etAspRateH);
            double dW = Double.parseDouble(etAspW.getText().toString());
            double dH = Double.parseDouble(etAspH.getText().toString());
            width = containerWidth;
            height = (int) Math.floor(containerWidth * (dH / dW));
        }
        if (isWh) {
            EditText etWidth = (EditText) findViewById(R.id.etWidth);
            EditText etHeight = (EditText) findViewById(R.id.etHeight);
            String strWidth = etWidth.getText().toString();
            String strHeight = etHeight.getText().toString();
            width = Integer.parseInt(strWidth);
            height = Integer.parseInt(strHeight);
        }
        final LinearLayout nativebannerAdContainer = new LinearLayout(this);
        nativebannerAdContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                TextView tvInfo = (TextView) findViewById(R.id.tvViewInfo);
                tvInfo.setText(String.format("W: %s px,H: %s px", nativebannerAdContainer.getMeasuredWidth(), nativebannerAdContainer.getMeasuredHeight()));
            }
        });
        LinearLayout.LayoutParams nativebanneradlp = new LinearLayout.LayoutParams(width, height);
        initSimpleNativeBanner(nativebannerAdContainer);
        rlContainer.removeAllViews();
        rlContainer.addView(nativebannerAdContainer, nativebanneradlp);
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
        final String adSpaceid = "5uavuInDAl";
        BridgeMiddleware.initSimpleBanner(adContainer, this, adSpaceid, false);
    }

    private void initSimpleNativeBanner(ViewGroup adContainer) {
        final String adSpaceid = "PPub5d0djn";
        BridgeMiddleware.initSimpleNativeBanner(adContainer, this, adSpaceid, true);
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
        BridgeMiddleware.activityDestroy(this);
    }
}
