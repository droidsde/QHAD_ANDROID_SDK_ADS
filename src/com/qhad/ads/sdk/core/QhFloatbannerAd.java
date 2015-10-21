package com.qhad.ads.sdk.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhFloatbannerAd;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.utils.Utils;

public class QhFloatbannerAd implements IQhFloatbannerAd, DynamicObject {

    private RelativeLayout adContainer = null;
    private QhBannerAd bannerAdView = null;
    private Context context = null;

    public QhFloatbannerAd() {
    }

    public void showAds(Activity activity, String adSpaceid, Boolean isTest,
                        FLOAT_BANNER_SIZE bannerSize, LOCATION bannerLoc, Context appContext) {

        WindowManager mw = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);

        if (bannerAdView != null) {
            closeAds();
            bannerAdView.onActivityFinishing();
        }

        if (adContainer != null) {
            adContainer.removeAllViews();
            if (adContainer.getParent() != null) {
                mw.removeView(adContainer);
            }
        }


        context = activity;
        int gravity = Gravity.BOTTOM;
        switch (bannerLoc) {
            case TOP:
                gravity = Gravity.TOP;
                break;

            case BOTTOM:
                gravity = Gravity.BOTTOM;
                break;
        }

        int ad_width = 0;
        int ad_height = 0;
        int size[] = Utils.getDeviceScreenSizeWithInt();
        switch (bannerSize) {
            case SIZE_DEFAULT:
                if (size[0] > size[1]) {
                    //横频
                    ad_width = size[0] / 2;
                } else {
                    //竖屏
                    ad_width = size[0];
                }
                ad_height = (int) (ad_width / 8.1);
                break;

            case SIZE_MATCH_PARENT:
                ad_width = size[0];
                ad_height = (int) (ad_width / 8.1);
                break;

            case SIZE_640X100:
                ad_width = 640;
                ad_height = 100;
                break;

            case SIZE_936x120:
                ad_width = 936;
                ad_height = 120;
                break;

            case SIZE_728x90:
                ad_width = 728;
                ad_height = 90;
                break;

            default:
                if (size[0] > size[1]) {
                    ad_width = size[0] / 2;
                } else {
                    ad_width = size[0];
                }
                ad_height = (int) (ad_width / 8.1);
                break;
        }

        RelativeLayout.LayoutParams adlp = new RelativeLayout.LayoutParams(ad_width, ad_height);
        adContainer = new RelativeLayout(activity);
        bannerAdView = new QhBannerAd(activity, adSpaceid, AD_TYPE.FLOAT_BANNER, isTest, activity.getApplicationContext());
        bannerAdView.loadAds(activity, ad_width, ad_height);
        adContainer.addView(bannerAdView, adlp);

        WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
        wlp.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCH_MODAL;
        int v = activity.getWindow().getAttributes().flags;
        if ((v & LayoutParams.FLAG_FULLSCREEN) == LayoutParams.FLAG_FULLSCREEN) {
            wlp.flags |= LayoutParams.FLAG_FULLSCREEN;
        }
        wlp.format = PixelFormat.TRANSPARENT;
        wlp.width = ad_width;
        wlp.height = ad_height;
        wlp.gravity = gravity;
        wlp.type = LayoutParams.TYPE_APPLICATION;
        mw.addView(adContainer, wlp);
    }

    @Override
    public void setAdEventListener(Object adEventListener) {
        if (adEventListener != null) {
            bannerAdView.setAdEventListener(adEventListener);
        }
    }

    @Override
    public void closeAds() {
        try {
            WindowManager mw = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (adContainer != null) {
//				if (bannerAdView.getParent() != null) {
                mw.removeViewImmediate(adContainer);
                if (bannerAdView != null) {
                    bannerAdView.onActivityFinishing();
                }
//				}
                adContainer = null;
            }
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.AD_CLOSE_ERROR, "FloatBanner Close Error", e);
        }
    }

    @Override
    public Object invoke(int funcId, Object arg) {
        switch (funcId) {
            case _D.QHFLOATBANNERAD_closeAds:
                QHADLog.d("ADS", "QHFLOATBANNERAD_closeAds");
                closeAds();
                break;
            case _D.QHFLOATBANNERAD_setAdEventListener:
                QHADLog.d("ADS", "QHFLOATBANNERAD_setAdEventListener");
                setAdEventListener(new QhAdEventListenerProxy((DynamicObject) arg));
                break;
        }
        return null;
    }

    public enum FLOAT_BANNER_SIZE {
        SIZE_DEFAULT,
        SIZE_MATCH_PARENT,
        SIZE_640X100,
        SIZE_936x120,
        SIZE_728x90
    }

    public enum LOCATION {
        TOP,
        BOTTOM
    }
}