package com.qhad.ads.sdk.adsinterfaces;

import android.app.Activity;

public interface IQhNativeBannerAd {
    void closeAds();

    void showAds(Activity activity);

    void setAdEventListener(Object adEventListener);
}