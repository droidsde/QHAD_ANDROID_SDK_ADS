package com.qhad.ads.sdk.core;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.ViewGroup;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IBridge;
import com.qhad.ads.sdk.adsinterfaces.IQhAdEventListener;
import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageView;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdListener;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdListener;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.interfaces.ServiceBridge;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.SwitchConfig;
import com.qhad.ads.sdk.service.QhAdServiceBridge;

public class Bridge implements IBridge, DynamicObject {
    /**
     * General Interface
     */
    @Override
    public final Object getInterstitial(Activity activity, String adSpaceid, Boolean isTest) {
        return BridgeMiddleware.initSimpleInterstitial(activity, adSpaceid, isTest);
    }

    @Override
    public final Object getBanner(ViewGroup adContainer, Activity activity, String adSpaceid, Boolean isTest) {
        return BridgeMiddleware.initSimpleBanner(adContainer, activity, adSpaceid, isTest);
    }

    @Override
    public Object getNativeBanner(ViewGroup adContainer, Activity activity, String adSpaceid, Boolean isTest) {
        return BridgeMiddleware.initSimpleNativeBanner(adContainer, activity, adSpaceid, isTest);
    }

    @Override
    public Object getFloatingBanner(Activity activity, String adSpaceid,
                                    Boolean isTest, Integer size, Integer location) {
        return BridgeMiddleware.initSimpleFloatbanner(activity, adSpaceid, isTest, size, location);
    }

    @Override
    public void getSplashAd(ViewGroup adContainer, Activity activity, String adSpaceid, IQhAdEventListener listener, Boolean showCountdown, Boolean isTest) {
        BridgeMiddleware.initSplash(adContainer, activity, adSpaceid, listener, showCountdown, isTest);
    }

    @Override
    public ServiceBridge getServiceBridge(Service service) {
        return new QhAdServiceBridge(service);
    }

    @Override
    public void setLogSwitch(boolean state) {
        SwitchConfig.LOG = state;
    }

    @Override
    public void setLandingPageView(IQhLandingPageView landingPageView) {
        QhAdModel.getInstance().setUserLandingPage(landingPageView);
    }

    @Override
    public final void activityDestroy(Activity activity) {
        BridgeMiddleware.activityDestroy(activity);
    }

    @Override
    public Object getNativeAdLoader(Activity activity, String adSpaceid,
                                    IQhNativeAdListener listener, Boolean isTest) {
        return BridgeMiddleware.initNativeAdLoader(activity, adSpaceid, listener, isTest);
    }


    @Override
    public Object getVideoAdLoader(Context context, String adSpaceid,
                                   IQhVideoAdListener listener, Boolean isTest) {
        return BridgeMiddleware.initVideoAdLoader(context, adSpaceid, listener, isTest);
    }


    @Override
    public Object invoke(int funcId, Object arg) {
        Object[] args;
        switch (funcId) {
            case _D.BRIDGE_getBanner:
                QHADLog.d("ADS", "BRIDGE_getBanner");
                args = (Object[]) arg;
                return getBanner((ViewGroup) args[0], (Activity) args[1], (String) args[2], (Boolean) args[3]);
            case _D.BRIDGE_getNativeBanner:
                QHADLog.d("ADS", "BRIDGE_getNativeBanner");
                args = (Object[]) arg;
                return getNativeBanner((ViewGroup) args[0], (Activity) args[1], (String) args[2], (Boolean) args[3]);
            case _D.BRIDGE_getInterstitial:
                QHADLog.d("ADS", "BRIDGE_getInterstitial");
                args = (Object[]) arg;
                return getInterstitial((Activity) args[0], (String) args[1], (Boolean) args[2]);
            case _D.BRIDGE_getFloatingBanner:
                QHADLog.d("ADS", "BRIDGE_getFloatingBanner");
                args = (Object[]) arg;
                return getFloatingBanner((Activity) args[0], (String) args[1], (Boolean) args[2], (Integer) args[3], (Integer) args[4]);
            case _D.BRIDGE_getSplashAd:
                QHADLog.d("ADS", "BRIDGE_getSplashAd");
                args = (Object[]) arg;
                getSplashAd((ViewGroup) args[0], (Activity) args[1], (String) args[2], new QhAdEventListenerProxy((DynamicObject) args[3]), (Boolean) args[4], (Boolean) args[5]);
                break;
            case _D.BRIDGE_getNativeAdLoader:
                QHADLog.d("ADS", "BRIDGE_getNativeAdLoader");
                args = (Object[]) arg;
                return getNativeAdLoader((Activity) args[0], (String) args[1], new QhNativeAdListenerProxy((DynamicObject) args[2]), (Boolean) args[3]);
            case _D.BRIDGE_getVideoAdLoader:
                QHADLog.d("ADS", "BRIDGE_getVideoAdLoader");
                args = (Object[]) arg;
                return getVideoAdLoader((Context) args[0], (String) args[1], new QhVideoAdListenerProxy((DynamicObject) args[2]), (Boolean) args[3]);
            case _D.BRIDGE_activityDestroy:
                QHADLog.d("ADS", "BRIDGE_activityDestroy");
                activityDestroy((Activity) arg);
                break;
            case _D.BRIDGE_getServiceBridge:
                QHADLog.d("ADS", "BRIDGE_getServiceBridge");
                return getServiceBridge((Service) arg);
            case _D.BRIDGE_setLogSwitch:
                QHADLog.d("ADS", "BRIDGE_setLogSwitch");
                setLogSwitch((boolean) arg);
                break;
            case _D.BRIDGE_setLandingPageView:
                QHADLog.d("ADS", "BRIDGE_setLandingPageView");
                setLandingPageView(new QhLandingPageViewProxy((DynamicObject) arg));
                break;
        }
        return null;
    }
}