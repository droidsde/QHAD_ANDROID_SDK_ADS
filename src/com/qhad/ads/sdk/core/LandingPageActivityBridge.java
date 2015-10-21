package com.qhad.ads.sdk.core;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageListener;
import com.qhad.ads.sdk.interfaces.ActivityBridge;

/**
 * Created by chengsiy on 2015/5/27.
 */
public class LandingPageActivityBridge implements ActivityBridge {

    public static final String WINDOW_FLAGS = "WindowFlags";
    private static final int NOT_FOUND_INT = -1;
    public String url;
    public String advertiserid = "";
    public String campaignid = "";
    public String solutionid = "";
    public String bannerid = "";
    public String impid = "";
    public IQhLandingPageListener landingPageListener;
    private Activity activity;
    private LandingPageLayout layout;

    @Override
    public void onInit(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        int windowFlags = activity.getIntent().getIntExtra(WINDOW_FLAGS, NOT_FOUND_INT);
        if ((windowFlags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        layout = new LandingPageLayout(activity);
        layout.advertiserid = advertiserid;
        layout.campaignid = campaignid;
        layout.solutionid = solutionid;
        layout.bannerid = bannerid;
        layout.impid = impid;
        layout.landingPageListener = landingPageListener;
        activity.setContentView(layout);
        layout.showLanding(url);
    }

    @Override
    public void onDestroy() {
        landingPageListener = null;
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    layout.closeLanding();
                }
                return true;
            case KeyEvent.KEYCODE_MENU:
            default:
                break;
        }
        return false;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onStop() {

    }
}
