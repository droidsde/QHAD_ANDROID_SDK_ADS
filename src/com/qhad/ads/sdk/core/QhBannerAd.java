package com.qhad.ads.sdk.core;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhBannerAd;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.view.AdImageView;
import com.qhad.ads.sdk.view.AdWebView;

public class QhBannerAd extends QhAdView implements IQhBannerAd, DynamicObject {

    @Deprecated
    public QhBannerAd(Context context) {
        super(context);
    }

    @Deprecated
    public QhBannerAd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QhBannerAd(Context context, String adSpaceid, AD_TYPE type,
                      Boolean isTest, Context appContext) {
        super(context, adSpaceid, type, isTest, appContext);
    }

    @Override
    protected void renderingImage() {
        super.renderingImage();
        int wp = adWidth;
        int hp = adHeight;

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(wp, hp);
        if (adType == AD_TYPE.FLOAT_BANNER) {
            lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        if (imageView != null) {
            imageView.dismissBitmap();
        }
        imageView = new AdImageView(context, adViewEventListener, this);
        imageView.showAD(vo);
        if (adType == AD_TYPE.FLOAT_BANNER) {
            imageView.setScaleType(ScaleType.FIT_XY);
        }
        this.addView(imageView, lp);
    }

    @Override
    public void closeAds() {
        super.closeAds();
        try {
            if (adViewEventListener != null) {
                adViewEventListener.onAdviewClosed();
            }
            this.setVisibility(View.GONE);
            isShowad = false;
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.AD_CLOSE_ERROR, "close ad failed", e, vo);
        }
    }

    @Override
    public void showAds(Activity activity) {
        super.showAds(activity);
        try {
            this.setVisibility(View.VISIBLE);
            isShowad = true;
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.AD_SHOW_ERROR, "Show Ad Error", e, vo);
        }
    }

    @Override
    protected void renderingMraid() {
        super.renderingMraid();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(adWidth, adHeight);
        if (adType == AD_TYPE.FLOAT_BANNER) {
            lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }
        webview = new AdWebView(context, String.valueOf(adWidth), String.valueOf(adHeight), adType, uiHandler);
        webview.showAD(vo, adViewEventListener, this, hashNum, new QhAdView.Listener() {

            @Override
            public void onRenderedSucceed() {
                QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.IMP_AD);
            }
        });

        this.addView(webview, lp);
    }

    public void loadAds(Activity activity, int adw, int adh) {
        this.context = activity;
        adWidth = adw;
        adHeight = adh;
        getData();
    }

    @Override
    public Object invoke(int funcId, Object arg) {
        switch (funcId) {
            case _D.QHBANNERAD_CLOSEADS:
                QHADLog.d("ADS", "QHBANNERAD_CLOSEADS");
                closeAds();
                break;
            case _D.QHBANNERAD_SHOWADS:
                QHADLog.d("ADS", "QHBANNERAD_SHOWADS");
                showAds((Activity) arg);
                break;
            case _D.QHBANNERAD_SETADEVENTLISTENER:
                QHADLog.d("ADS", "QHBANNERAD_SETADEVENTLISTENER");
                setAdEventListener(new QhAdEventListenerProxy((DynamicObject) arg));
                break;
        }
        return null;
    }
}
