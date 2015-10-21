package com.qhad.ads.sdk.interfaces;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * Created by chengsiy on 2015/4/29.
 */
public interface ActivityBridge {
    void onInit(Activity activity);

    void onCreate(Bundle savedInstanceState);

    void onDestroy();

    void onNewIntent(Intent intent);

    void onResume();

    void onPause();

    void onConfigurationChanged(Configuration newConfig);

    void onLowMemory();

    void onTrimMemory(int level);

    boolean dispatchKeyEvent(KeyEvent event);

    void onStart();

    void onRestart();

    void onRestoreInstanceState(Bundle savedInstanceState);

    void onSaveInstanceState(Bundle outState);

    void onStop();
}
