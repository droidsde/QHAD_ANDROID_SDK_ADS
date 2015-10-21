package com.qhad.ads.sdk.adsinterfaces;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.ViewGroup;

import com.qhad.ads.sdk.interfaces.ServiceBridge;

public interface IBridge {
    /**
     * General Interface
     */
    Object getInterstitial(Activity activity, String adSpaceid, Boolean isTest);

    Object getBanner(ViewGroup adContainer, Activity activity, String adSpaceid, Boolean isTest);

    Object getNativeBanner(ViewGroup adContainer, Activity activity, String adSpaceid, Boolean isTest);

    Object getFloatingBanner(Activity activity, String adSpaceid, Boolean isTest, Integer size, Integer location);

    void getSplashAd(ViewGroup adContainer, Activity activity, String adSpaceid, IQhAdEventListener listener, Boolean showCountdown, Boolean isTest);

    Object getNativeAdLoader(Activity activity, String adSpaceid, IQhNativeAdListener listener, Boolean isTest);

    Object getVideoAdLoader(Context context, String adSpaceid, IQhVideoAdListener listener, Boolean isTest);

    void activityDestroy(Activity activity);

    ServiceBridge getServiceBridge(Service service);

    void setLogSwitch(boolean state);

    void setLandingPageView(IQhLandingPageView landingPageView);
}