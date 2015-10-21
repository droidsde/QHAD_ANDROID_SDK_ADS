package com.qhad.ads.sdk.adcore;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

import com.qhad.ads.sdk.interfaces.ActivityBridge;

/**
 * Created by chengsiy on 2015/4/29.
 */
public class QhAdActivity extends Activity {
    public static ActivityBridge activityBridge;

    public QhAdActivity() {
        if (activityBridge != null)
            activityBridge.onInit(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (activityBridge != null)
            activityBridge.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (activityBridge != null) {
            activityBridge.onDestroy();
            activityBridge = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (activityBridge != null)
            activityBridge.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activityBridge != null)
            activityBridge.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (activityBridge != null)
            activityBridge.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (activityBridge != null)
            activityBridge.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (activityBridge != null)
            activityBridge.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (activityBridge != null)
            activityBridge.onTrimMemory(level);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (activityBridge != null)
            return activityBridge.dispatchKeyEvent(event);
        else
            return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (activityBridge != null)
            activityBridge.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (activityBridge != null)
            activityBridge.onRestart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (activityBridge != null)
            activityBridge.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activityBridge != null)
            activityBridge.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activityBridge != null)
            activityBridge.onStop();
    }
}
