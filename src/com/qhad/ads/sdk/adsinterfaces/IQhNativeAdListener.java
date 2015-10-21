package com.qhad.ads.sdk.adsinterfaces;

import java.util.ArrayList;

public interface IQhNativeAdListener {
    void onNativeAdLoadSucceeded(ArrayList<IQhNativeAd> nativeAds);

    void onNativeAdLoadFailed();
}
