package com.qhad.ads.sdk.adsinterfaces;

import android.app.Activity;

import org.json.JSONObject;

public interface IQhNativeAd {
    JSONObject getContent();

    void onAdShowed();

    void onAdClicked();

    void onAdClicked(Activity activity);
}
