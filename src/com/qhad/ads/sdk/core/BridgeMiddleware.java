package com.qhad.ads.sdk.core;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import com.qhad.ads.sdk.adsinterfaces.IQhAdEventListener;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdListener;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdListener;
import com.qhad.ads.sdk.core.QhFloatbannerAd.FLOAT_BANNER_SIZE;
import com.qhad.ads.sdk.core.QhFloatbannerAd.LOCATION;
import com.qhad.ads.sdk.model.InstanceModel;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.utils.Utils;

public class BridgeMiddleware {

    /**
     * 初始化Banner
     *
     * @param adContainer 广告容器
     * @param activity    当前活动实例
     * @param adSpaceid   广告位标识
     * @param isTest      是否测试
     * @return
     */
    public static QhBannerAd initSimpleBanner(ViewGroup adContainer, Activity activity, String adSpaceid, Boolean isTest) {
        QhBannerAd bannerAdView = new QhBannerAd(activity, adSpaceid, AD_TYPE.BANNER, isTest, activity.getApplicationContext());
        ViewGroup.LayoutParams adlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (adContainer != null) {
            adContainer.addView(bannerAdView, adlp);
            InstanceModel.getInstance().regAdInstance(activity, bannerAdView);
        }
        return bannerAdView;
    }

    /**
     * 初始化NativeBanner
     *
     * @param adContainer 广告容器
     * @param activity    当前活动实例
     * @param adSpaceid   广告位标识
     * @param isTest      是否测试
     * @return
     */
    public static QhNativeBannerAd initSimpleNativeBanner(ViewGroup adContainer, Activity activity, String adSpaceid, Boolean isTest) {
        QhNativeBannerAd nativebannerAdView = new QhNativeBannerAd(activity, adSpaceid, AD_TYPE.NATIVE_BANNER, isTest, activity.getApplicationContext());
        ViewGroup.LayoutParams adlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (adContainer != null) {
            adContainer.addView(nativebannerAdView, adlp);
            InstanceModel.getInstance().regAdInstance(activity, nativebannerAdView);
        }
        return nativebannerAdView;
    }

    /**
     * 初始化插屏
     * 每个Activity 只有一个插屏实例
     *
     * @param activity  当前活动实例
     * @param adSpaceid 广告位标识
     * @param isTest    是否测试
     * @return
     */
    public static QhInterstitialAd initSimpleInterstitial(Activity activity, String adSpaceid, Boolean isTest) {
        Utils.init(activity.getApplicationContext());
        QhInterstitialAd interstitialAdView = InstanceModel.getInstance().getInterstitialInstance();
        if (interstitialAdView == null) {
            interstitialAdView = new QhInterstitialAd(activity, adSpaceid, AD_TYPE.INTERSTITIAL, isTest, activity.getApplicationContext());
            interstitialAdView.loadAds(activity);
            InstanceModel.getInstance().setInterstitialInstance(interstitialAdView);
        } else {
            interstitialAdView.loadAds(activity);
        }
        return interstitialAdView;
    }

    /**
     * 初始化浮动条幅
     * 每个Activity 只有一个条幅实例
     *
     * @param activity  活动实例
     * @param adSpaceid 广告位标识
     * @param isTest    是否为测试
     * @param size      广告大小
     * @param location  广告位置
     * @return
     */
    public static QhFloatbannerAd initSimpleFloatbanner(Activity activity, String adSpaceid, Boolean isTest, int size, int location) {
        Utils.init(activity.getApplicationContext());
        QhFloatbannerAd floatbannerAd = InstanceModel.getInstance().getFloatbannerInstance();

        FLOAT_BANNER_SIZE b_size = FLOAT_BANNER_SIZE.SIZE_DEFAULT;
        if (size == 0) {
            b_size = FLOAT_BANNER_SIZE.SIZE_DEFAULT;
        } else if (size == 1) {
            b_size = FLOAT_BANNER_SIZE.SIZE_MATCH_PARENT;
        } else if (size == 2) {
            b_size = FLOAT_BANNER_SIZE.SIZE_640X100;
        } else if (size == 3) {
            b_size = FLOAT_BANNER_SIZE.SIZE_936x120;
        } else if (size == 4) {
            b_size = FLOAT_BANNER_SIZE.SIZE_728x90;
        }

        LOCATION loc = LOCATION.TOP;
        if (location == 0) {
            loc = LOCATION.TOP;
        } else if (location == 1) {
            loc = LOCATION.BOTTOM;
        }

        if (floatbannerAd == null) {
            floatbannerAd = new QhFloatbannerAd();
            floatbannerAd.showAds(activity,
                    adSpaceid,
                    isTest,
                    b_size,
                    loc,
                    activity.getApplicationContext());
            InstanceModel.getInstance().setFloatbannerInstance(floatbannerAd);
        } else {
            floatbannerAd.showAds(activity,
                    adSpaceid,
                    isTest,
                    b_size,
                    loc,
                    activity.getApplicationContext());
        }
        return floatbannerAd;
    }

    /**
     * 初始化Spalsh
     *
     * @param adContainer 广告容器
     * @param activity    当前活动实例
     * @param adSpaceid   广告位标识
     * @param isTest      是否测试
     * @return
     */
    public static QhSplashAd initSplash(ViewGroup adContainer, Activity activity, String adSpaceid,
                                        IQhAdEventListener listener, Boolean showCountdown, Boolean isTest) {
        QhSplashAd splashAdView = new QhSplashAd(activity, adSpaceid, AD_TYPE.SPLASH, listener, showCountdown, isTest, activity.getApplicationContext());
        ViewGroup.LayoutParams adlp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        if (adContainer != null) {
            adContainer.addView(splashAdView, adlp);
            InstanceModel.getInstance().regAdInstance(activity, splashAdView);
        }
        return splashAdView;
    }

    public static QhNativeAdLoader initNativeAdLoader(Activity activity, String adspaceid, IQhNativeAdListener listener, Boolean isTest) {
        QhAdModel.getInstance().initGlobal(activity.getApplicationContext());
        QhNativeAdLoader nativeLoader = new QhNativeAdLoader(activity, adspaceid, listener, isTest);
        return nativeLoader;
    }

    /**
     * 检查Activity
     *
     * @param activity
     */
    public static void activityDestroy(Activity activity) {
        InstanceModel.getInstance().checkadContextLife(activity.hashCode());
        QhInterstitialAd interstitialAd = InstanceModel.getInstance().getInterstitialInstance();
        if (interstitialAd != null) {
            interstitialAd.handlerActivityDestroy(activity);
            InstanceModel.getInstance().setInterstitialInstance(null);
        }
        QhFloatbannerAd floatbannerAd = InstanceModel.getInstance().getFloatbannerInstance();
        if (floatbannerAd != null) {
            floatbannerAd.closeAds();
            InstanceModel.getInstance().setFloatbannerInstance(null);
        }
    }

    public static Object initVideoAdLoader(Context context, String adSpaceid,
                                           IQhVideoAdListener listener, Boolean isTest) {
        QhAdModel.getInstance().initGlobal(context);
        QhVideoAdLoader videoLoader = new QhVideoAdLoader(adSpaceid, listener, isTest);
        return videoLoader;
    }
}