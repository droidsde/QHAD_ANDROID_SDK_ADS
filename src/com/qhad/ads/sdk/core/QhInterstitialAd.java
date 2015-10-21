package com.qhad.ads.sdk.core;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhInterstitialAd;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.view.AdImageView;
import com.qhad.ads.sdk.view.AdWebView;

@SuppressWarnings("unused")
public class QhInterstitialAd extends QhAdView implements IQhInterstitialAd, DynamicObject {

    private final int size = 20;
    private final float standard = 100;

    @Deprecated
    public QhInterstitialAd(Context context) {
        super(context);
    }

    @Deprecated
    public QhInterstitialAd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QhInterstitialAd(Context context, String adSpaceid, AD_TYPE type,
                            Boolean isTest, Context appContext) {
        super(context, adSpaceid, type, isTest, appContext);
    }

    @Override
    protected void renderingImage() {
        super.renderingImage();

        final int[] wh = Utils.getDeviceScreenSizeWithInt();
        int width = (int) (wh[0] * StaticConfig.INTER_AD_SCALE);
        int height = (int) (wh[1] * StaticConfig.INTER_HEIGHT_AD_SCALE);

        int trueW = vo.bmp.getWidth();
        int trueH = vo.bmp.getHeight();

        if (vo.bmp.getHeight() > height || vo.bmp.getHeight() < height) {
            trueH = height;
            final double s = ((double) vo.bmp.getWidth() / (double) vo.bmp.getHeight());
            trueW = (int) (s * (double) height);
        }

        if (trueW > width) {
            trueW = width;
            final double s = ((double) vo.bmp.getHeight() / (double) vo.bmp.getWidth());
            trueH = (int) (s * (double) width);
        }

        imageView = new AdImageView(context, adViewEventListener, this);
        imageView.showAD(vo);
        initClosebtn(trueH);

        RelativeLayout.LayoutParams viewlp = new RelativeLayout.LayoutParams(trueW, trueH);
        viewlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        this.addView(imageView, viewlp);

        showInterstitialAd(trueW, trueH);
    }

    @Override
    public void closeAds() {
        if (this.getParent() == null) {
            QHADLog.d("插屏未显示");
            return;
        }
        try {
            if (vo.bmp != null) {
                if (!vo.bmp.isRecycled()) {
                    vo.bmp.recycle();
                }
            }

            Activity activity = (Activity) context;
            if (activity != null) {
                if (activity.isFinishing()) {
                    QHADLog.d("关闭插屏广告失败：Context is Dead");
                    return;
                }
            }
            WindowManager mw = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mw.removeView(this);
            if (adViewEventListener != null) {
                adViewEventListener.onAdviewClosed();
            }
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.AD_CLOSE_ERROR, "close ad failed", e, vo);
        }
        super.closeAds();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    closeAds();
                }
            case KeyEvent.KEYCODE_MENU:
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void showAds(Activity activity) {
        super.showAds(activity);
        loadAds(activity);
    }

    @TargetApi(17)
    private void showInterstitialAd(int w, int h) {
        if (this.getParent() == null) {
            try {
                Activity activity = (Activity) context;
                if (activity != null) {
                    if (activity.isFinishing()) {
                        QHADLog.e(QhAdErrorCode.AD_RENDER_ERROR, "InterstitialAd Render Failed:Context is Dead");
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= 17) {
                        if (activity.isDestroyed()) {
                            QHADLog.e(QhAdErrorCode.AD_RENDER_ERROR, "InterstitialAd Render Failed:Context is Dead");
                            return;
                        }
                    }
                }
                WindowManager mw = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams wlp = new WindowManager.LayoutParams();
                wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                wlp.dimAmount = 0f;
                wlp.width = w;
                wlp.height = h;
                wlp.format = -3;
                this.setBackgroundColor(Color.TRANSPARENT);
                mw.addView(this, wlp);
            } catch (Exception e) {
                QHADLog.e(QhAdErrorCode.AD_RENDER_ERROR, "InterstitialAd Render Failed", e);
            }
        }
    }

    private void closeInterstitialAd() {
        WindowManager mw = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (this.getParent() != null) {
            if (adViewEventListener != null) {
                adViewEventListener.onAdviewClosed();
            }
            mw.removeView(this);
        }
    }

    @Override
    protected void renderingMraid() {
        super.renderingMraid();
        final int[] wh = Utils.getDeviceScreenSizeWithInt();
        adWidth = (int) (wh[0] * StaticConfig.INTER_HEIGHT_AD_SCALE);
        adHeight = (int) (wh[1] * StaticConfig.INTER_HEIGHT_AD_SCALE);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(adWidth, adHeight);
        webview = new AdWebView(context, String.valueOf(adWidth), String.valueOf(adHeight), adType, uiHandler);
        webview.showAD(vo, adViewEventListener, this, hashNum, new QhAdView.Listener() {

            @Override
            public void onRenderedSucceed() {
                QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.IMP_AD);
            }
        });
        this.addView(webview, lp);
        initClosebtn(adHeight);
        showInterstitialAd(adWidth, adHeight);

        /** 暂不使用
         final int[] wh = Utils.getDeviceScreenSizeWithInt();
         if (vo.adm_type != ResourceType.MRAID) {
         adWidht = (int)(wh[0] * StaticConfig.INTER_HEIGHT_AD_SCALE);
         adHeight = (int)(wh[1] * StaticConfig.INTER_HEIGHT_AD_SCALE);
         }else{
         adWidht = wh[0];
         adHeight = wh[1];
         }

         RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(adWidht, adHeight);
         webview = new AdWebView(context, String.valueOf(adWidht), String.valueOf(adHeight), adType);
         webview.showAD(vo, adViewEventListener, this, hashNum);
         this.addView(webview, lp);
         if (vo.adm_type != ResourceType.MRAID) {
         initClosebtn(adHeight);
         }

         showInterstitialAd(adWidht, adHeight);
         */
    }

    @SuppressWarnings("deprecation")
    private void initClosebtn(int h) {
        if (imgBtn == null) {
            imgBtn = new ImageButton(context);
            try {
                imgBtn.setBackgroundDrawable(QhAdModel.getInstance().getClosebtnBitmap());
            } catch (Exception e) {
                QHADLog.e(QhAdErrorCode.AD_RENDER_ERROR, "InterstitialAd init close button error", e, vo);
            }
            imgBtn.setScaleType(ScaleType.FIT_XY);

            int btnwh = 30;
            float s = h / standard;
            float d = s - 1;

            btnwh = (int) (size * (1 + (d / 8)));

            int btnw = (int) (btnwh * Utils.getDeviceDensity());
            int btnh = (int) (btnwh * Utils.getDeviceDensity());
            RelativeLayout.LayoutParams btnlp = new RelativeLayout.LayoutParams(btnw, btnh);
            btnlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            this.addView(imgBtn, btnlp);

            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    closeInterstitialAd();
                }
            });
        }
    }

    public void handlerActivityDestroy(Activity activity) {
        try {
            WindowManager mw = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            if (this.getParent() != null) {
                mw.removeViewImmediate(this);
            }
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.AD_CLEAN_ERROR, "InterstitialAd handlerActivityDestroy Error", e);
        }
    }

    public void loadAds(Activity activity) {
        context = activity;
        getData();
    }

    @Override
    public Object invoke(int funcId, Object arg) {
        switch (funcId) {
            case _D.QHINTERSTITIALAD_closeAds:
                QHADLog.d("ADS", "QHINTERSTITIALAD_closeAds");
                closeAds();
                break;
            case _D.QHINTERSTITIALAD_showAds:
                QHADLog.d("ADS", "QHINTERSTITIALAD_showAds");
                showAds((Activity) arg);
                break;
            case _D.QHINTERSTITIALAD_setAdEventListener:
                QHADLog.d("ADS", "QHINTERSTITIALAD_setAdEventListener");
                setAdEventListener(new QhAdEventListenerProxy((DynamicObject) arg));
                break;
        }
        return null;
    }
}