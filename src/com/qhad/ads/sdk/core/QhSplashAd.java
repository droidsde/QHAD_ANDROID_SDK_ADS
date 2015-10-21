package com.qhad.ads.sdk.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qhad.ads.sdk.adsinterfaces.IQhAdEventListener;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.view.AdImageView;
import com.qhad.ads.sdk.view.AdWebView;

import java.util.Timer;
import java.util.TimerTask;

public class QhSplashAd extends QhAdView implements DynamicObject {

    public Timer mCountDownTimer;
    public boolean isPause = false;
    public int countDownTime = 3;
    public double currentCountDownTime = 0.0;
    public int displayCountDownTime = 0;
    public TimerTask mTimerTask;
    public Handler handler;
    private boolean showCountdown = true;
    private TextView textView;


    @Deprecated
    public QhSplashAd(Context context) {
        super(context);
    }

    @Deprecated
    public QhSplashAd(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QhSplashAd(Context context, String adSpaceid, AD_TYPE type, IQhAdEventListener listener, boolean showCountdown,
                      Boolean isTest, Context appContext) {
        super(context, adSpaceid, type, isTest, appContext);

        currentCountDownTime = countDownTime;
        displayCountDownTime = countDownTime;

        this.adViewEventListener = listener;
        this.showCountdown = showCountdown;
    }

    @Override
    protected void renderingImage() {
        super.renderingImage();
        double density = context.getResources().getDisplayMetrics().density;
        int wp = adWidth;
        int hp = adHeight;

        int trueW = vo.bmp.getWidth();
        int trueH = vo.bmp.getHeight();

        final double p = (double) hp / (double) wp;
        final double s = (double) trueH / (double) trueW;

        if (p > 1) {
            if (s > 1) {
                if (s > p) {
                    trueH = hp;
                    trueW = (int) ((double) hp / s);
                }
                trueW = wp;
                trueH = (int) (s * (double) wp);
            }
            trueW = wp;
            trueH = (int) (s * (double) wp);
        }

        if (s < 1) {
            if (s < p) {
                trueW = wp;
                trueH = (int) (s * (double) wp);
            }
            trueH = hp;
            trueW = (int) ((double) hp / s);
        }
        trueH = hp;
        trueW = (int) ((double) hp / s);

        float scaleW = ((float) trueW) / vo.bmp.getWidth();
        float scaleH = ((float) trueH) / vo.bmp.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH);
        vo.bmp = Bitmap.createBitmap(vo.bmp, 0, 0, vo.bmp.getWidth(), vo.bmp.getHeight(), matrix, true);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(wp, hp);
        if (imageView != null) {
            imageView.dismissBitmap();
        }
        imageView = new AdImageView(context, adViewEventListener, this);
        imageView.showAD(vo);
        imageView.setScaleType(ScaleType.FIT_XY);
        imageView.setId(imageView.hashCode());
        this.addView(imageView, lp);

        textView = new TextView(context);
        textView.setText(String.valueOf(countDownTime));

        RelativeLayout.LayoutParams textlp = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textlp.addRule(ALIGN_PARENT_RIGHT);
        textlp.addRule(ALIGN_PARENT_TOP);

        textlp.topMargin = getPx(46, density);
        textlp.rightMargin = getPx(56, density);
        textView.setPadding(30, 0, 30, 0);
        textView.setTextColor(Color.parseColor("#FFFFFF"));
        textView.setBackgroundColor(Color.parseColor("#999999"));
        textView.getBackground().setAlpha(188);
        textView.setTextSize(23);
        if (!showCountdown)
            textView.setVisibility(GONE);
        this.addView(textView, textlp);


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        currentCountDownTime -= 0.1;

                        textView.setText(String.valueOf((int) currentCountDownTime + 1));

                        if (currentCountDownTime <= 0) {
                            if (adViewEventListener != null)
                                adViewEventListener.onAdviewClosed();

                            overAds();
                        }
                        break;


                    default:
                        break;
                }


                super.handleMessage(msg);
            }
        };

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (isPause)
                    return;

//                QHADLog.e("mTimerTask");
                handler.sendEmptyMessage(1);
            }
        };
    }

    public void startCountDown() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();

        mCountDownTimer = new Timer();
        mCountDownTimer.schedule(mTimerTask, 100, 100);
    }

    @Override
    protected void renderingMraid() {
        super.renderingMraid();
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(adWidth, adHeight);
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

    public void overAds() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
    }

    @Override
    public Object invoke(int funcId, Object arg) {
        return null;
    }

    private int getPx(int dPx, double density) {
        return (int) Math.floor(dPx * density / 3);
    }
}