package com.qhad.ads.sdk.core;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.qhad.ads.sdk.adsinterfaces.IQhAdEventListener;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.SwitchConfig;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.task.AsynDataLoader;
import com.qhad.ads.sdk.utils.RijindaelUtils;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.view.AdImageView;
import com.qhad.ads.sdk.view.AdWebView;
import com.qhad.ads.sdk.view.ConfirmView;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Duan
 */

public class QhAdView extends RelativeLayout implements OnGlobalLayoutListener {

    protected final Handler uiHandler;
    protected IQhAdEventListener adViewEventListener = new IQhAdEventListener() {
        @Override
        public void onAdviewIntoLandpage() {
        }

        @Override
        public void onAdviewGotAdSucceed() {
        }

        @Override
        public void onAdviewGotAdFail() {
        }

        @Override
        public void onAdviewRendered() {

        }

        @Override
        public void onAdviewDismissedLandpage() {
        }

        @Override
        public void onAdviewDestroyed() {
        }

        @Override
        public void onAdviewClicked() {
        }

        @Override
        public void onAdviewClosed() {
        }

    };
    protected QhAdView adView = null;
    protected Context context = null;
    protected Context appContext = null;
    protected CommonAdVO vo = null;
    protected Boolean isGetData = false;
    protected String adSpaceid = null;
    protected Boolean isTest = false;
    protected Timer timer = null;
    protected AD_TYPE adType = AD_TYPE.BANNER;
    protected ImageButton imgBtn = null;
    protected String hashNum = "";
    protected Boolean isShowbg = false;
    protected ConfirmView confirmView = null;
    protected Boolean isShowad = true;
    protected AdWebView webview = null;
    protected AdImageView imageView = null;
    protected int adWidth;
    protected int adHeight;
    protected String uid = "";
    protected AdPlayTimerTask timerTask = new AdPlayTimerTask(this);

    @Deprecated
    public QhAdView(Context context) {
        super(context);
        uiHandler = new Handler();
    }

    @Deprecated
    public QhAdView(Context context, AttributeSet attrs) {
        super(context, attrs);
        uiHandler = new Handler();
    }

    /**
     * @param context   应用上下文
     * @param adSpaceid 广告位ID
     * @param type      广告类型
     * @param isTest    是否为测试
     */
    public QhAdView(Context context, String adSpaceid, AD_TYPE type, Boolean isTest, Context appContext) {
        super(context);
        uiHandler = new Handler();
        this.appContext = appContext;
        this.getViewTreeObserver().addOnGlobalLayoutListener(this);

        QhAdModel.getInstance().initGlobal(appContext);
        this.isTest = isTest;
        this.context = context;
        this.adSpaceid = adSpaceid;
        hashNum = adSpaceid + String.valueOf(System.currentTimeMillis());
        adView = this;
        adType = type;

        uid = Utils.MD5(Utils.getIMEI() + Utils.getAppPackageName() + context.hashCode());

        if (SwitchConfig.SWH && SwitchConfig.BANNER) {
            if (adType == AD_TYPE.BANNER || adType == AD_TYPE.FLOAT_BANNER) {
                timer = new Timer();
                timer.schedule(timerTask, StaticConfig.REPLAY_TIME, StaticConfig.REPLAY_TIME);
            }
        }
    }

    @Override
    public void onGlobalLayout() {
        if (!isGetData) {
            if (SwitchConfig.SWH && (adType == AD_TYPE.BANNER || adType == AD_TYPE.NATIVE_BANNER || adType == AD_TYPE.SPLASH)) {
                adView.getData();
            }
            isGetData = true;
        }
    }

    /**
     * 设置广告状态回调侦听
     *
     * @param adEventListener 侦听实例
     */
    public void setAdEventListener(Object adEventListener) {
        IQhAdEventListener listener = (IQhAdEventListener) adEventListener;
        if (adEventListener != null) {
            adViewEventListener = listener;
        }
    }

    /**
     * 关闭广告
     */
    public void closeAds() {
    }

    /**
     * 显示广告
     */
    public void showAds(Activity activity) {
    }

    /**
     * 轮播时间间隔范围为 20秒 ~ 60秒
     *
     * @param second 秒数
     */
    public void setAdRepeatTime(int second) {
        int time = second;
        if (time < 20) {
            time = 20;
        }

        if (time > 60) {
            time = 60;
        }

        StaticConfig.REPLAY_TIME = time * 1000;
    }

    public void onActivityFinishing() {
        try {
            uiHandler.removeCallbacksAndMessages(null);
            timerTask.cancel();
            timerTask.cleanHandler();
            timerTask = null;
            this.removeAllViews();

            if (vo != null) {
                if (vo.bmp != null) {
                    if (!vo.bmp.isRecycled()) {
                        vo.bmp.recycle();
                    }
                }
            }
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.AD_CLEAN_ERROR, "onActivityFinishing Clean Error", e, vo);
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    /**
     * 获取数据
     */
    @TargetApi(11)
    protected void getData() {
        switch (adType) {
            case BANNER:
                if (!SwitchConfig.BANNER) {
                    return;
                }
                break;

            case SPLASH:
                if (!SwitchConfig.SPLASH) {
                    return;
                }
                break;

            case NATIVE_BANNER:
                if (!SwitchConfig.NE_BANNER) {
                    return;
                }
                break;

            case INTERSTITIAL:
                if (!SwitchConfig.INT) {
                    return;
                }
                break;

            case FLOAT_BANNER:
                break;
        }

        String url = assemblyUrl();
        if (adWidth == 0 || adHeight == 0) {
            QHADLog.e(QhAdErrorCode.INIT_PARAMS_ERROR, "adsize errro:" + adWidth + "," + adHeight, null, null);
            if (adViewEventListener != null) {
                adViewEventListener.onAdviewGotAdFail();
            }

            return;
        }

        @SuppressWarnings("unused")
        AsynDataLoader loader = new AsynDataLoader(url, new AsynDataLoader.Listener() {

            @Override
            public void onGetDataSucceed(CommonAdVO result) {
                if (result != null) {
                    vo = result;
                    vo.adType = adType;
                    vo.adWidth = adWidth;
                    vo.adHeight = adHeight;
                    layoutAds();
                    if (adViewEventListener != null) {
                        adViewEventListener.onAdviewGotAdSucceed();
                    }
                } else {
                    if (adViewEventListener != null) {
                        adViewEventListener.onAdviewGotAdFail();
                    }
                }
            }

            @Override
            public void onGetDataFailed(String error) {
                if (adViewEventListener != null) {
                    adViewEventListener.onAdviewGotAdFail();
                }
            }

            @Override
            public void onGetDataSucceed(ArrayList<CommonAdVO> vos) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 拼接URL
     *
     * @return URL
     */
    private String assemblyUrl() {
        String param = "";

        String it = ""; //获取密钥Key
        String ic = ""; //加密内容
        try {
            ic = URLEncoder.encode(Utils.getIMEI(), "utf-8");
            long currentTime = System.currentTimeMillis();
            int index = (int) (currentTime % 64);
            String password = RijindaelUtils.KEY_STORE[index];
            password = RijindaelUtils.hexPasswordToStrPassword(password);
            ic = RijindaelUtils.encrypt(ic, password);
            it = Long.toString(currentTime);
        } catch (Exception e) {
            QHADLog.e("RijindaelUtils encrypt Error:" + e.getMessage());
        }

        try {
            switch (adType) {
                case BANNER:
                    if (this.getMeasuredWidth() > 720) {
                        adWidth = this.getMeasuredWidth();
                        adHeight = (int) (this.getMeasuredWidth() / 8.1);
                    } else {
                        adWidth = this.getMeasuredWidth();
                        adHeight = (int) (this.getMeasuredWidth() / 6.4);
                    }
                    break;

                case SPLASH:
                    adWidth = this.getMeasuredWidth();
                    adHeight = this.getMeasuredHeight();
                    break;

                case NATIVE_BANNER:
                    adWidth = this.getMeasuredWidth();
                    adHeight = this.getMeasuredHeight();
                    break;

                case INTERSTITIAL:
                    final int[] wh = Utils.getDeviceScreenSizeWithInt();
                    adWidth = (int) (wh[0] * StaticConfig.INTER_AD_SCALE);
                    adHeight = (int) (wh[1] * StaticConfig.INTER_AD_SCALE);
                    break;

                case FLOAT_BANNER:
                    break;

                default:
                    break;
            }

            /** in test*/
            Random random = new Random(System.currentTimeMillis());
            @SuppressWarnings("unused")
            int num = Math.abs(random.nextInt());
            /** in test*/

            String bannerid = "";
            if (vo != null) {
                if (vo.bannerid != null) {
                    bannerid = vo.bannerid;
                }
            }

            param = "?adspaceid=" + URLEncoder.encode(adSpaceid, "utf-8") +
                    "&os=" + Utils.getSysteminfo() +
                    "&imei=" + URLEncoder.encode(Utils.getIMEI(), "utf-8") +
                    "&imei_md5=" + URLEncoder.encode(Utils.getIMEIWhitMD5(), "utf-8") +
                    "&imsi=" + URLEncoder.encode(Utils.getIMSI(), "utf-8") +
                    "&imsi_md5=" + URLEncoder.encode(Utils.getIMSIWhitMD5(), "utf-8") +
                    "&mac=" + URLEncoder.encode(Utils.getMac(), "utf-8") +
                    "&mac_md5=" + URLEncoder.encode(Utils.getMacWhitMD5(), "utf-8") +
                    "&model=" + URLEncoder.encode(Utils.getProductModel(), "utf-8").replace("+", "%20") +
                    "&channelid=" + URLEncoder.encode(StaticConfig.CHANNEL_ID, "utf-8") +
                    "&sdkv=" + URLEncoder.encode(StaticConfig.SDK_VERSION, "utf-8") +
                    "&appv=" + URLEncoder.encode(Utils.getAppVersion(), "utf-8") +
                    "&screenwidth=" + URLEncoder.encode(Utils.getDeviceScreenSizeWithString(true), "utf-8") +
                    "&screenheight=" + URLEncoder.encode(Utils.getDeviceScreenSizeWithString(false), "utf-8") +
                    "&so=" + URLEncoder.encode(Utils.getScreenOrientation(), "utf-8") +
                    "&density=" + Utils.getDeviceDensity() +
                    "&adsizewidth=" + String.valueOf(adWidth) +
                    "&adsizeheight=" + String.valueOf(adHeight) +
                    "&appname=" + URLEncoder.encode(Utils.getAppname(), "utf-8") +
                    "&apppkg=" + URLEncoder.encode(Utils.getAppPackageName(), "utf-8") +
                    "&istest=" + (isTest ? "1" : "0") +
                    "&net=" + URLEncoder.encode(Utils.getCurrentNetWorkInfo(), "utf-8") +
                    "&adtype=" + adType.ordinal() +
                    "&it=" + it +
                    "&ic=" + ic +
                    "&androidid=" + URLEncoder.encode(Utils.getAndroidid(), "utf-8") +
                    "&androidid_md5=" + URLEncoder.encode(Utils.getAndroididWithMD5(), "utf-8") +
                    "&lastbannerid=" + bannerid +
                    "&longitude=" + URLEncoder.encode(StaticConfig.longitude, "utf-8") +
                    "&latitude=" + URLEncoder.encode(StaticConfig.latitude, "utf-8") +
                    "&brand=" + URLEncoder.encode(Utils.getBrand(), "utf-8").replace("+", "%20") +
                    "&carrier=" + URLEncoder.encode(Utils.getNetworkOperator(), "utf-8") +
                    "&m2id=" + URLEncoder.encode(Utils.getm2id(), "utf-8").replace("+", "%20") +
                    "&serialid=" + URLEncoder.encode(Utils.getDeviceSerial(), "utf-8").replace("+", "%20") +
                    "&devicetype=" + Utils.getDeviceType() +
                    "&uid=" + URLEncoder.encode(uid, "utf-8");
        } catch (Exception e) {
            QHADLog.d("URL编码失败");
        }

        return StaticConfig.HTTPS_AD_URL + param;
    }

    /**
     * 暂时不使用此种二次确认
     *
     * @param vo
     */
    @SuppressWarnings("unused")
    private void initConfirmView(CommonAdVO vo) {
        if (confirmView == null) {
            confirmView = new ConfirmView(context, this.getMeasuredWidth(), this.getMeasuredHeight());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            this.addView(confirmView, lp);
        }
        confirmView.updateBtnText(vo);
    }

    /**
     * 布局广告
     */
    @SuppressWarnings("deprecation")
    @TargetApi(16)
    private void layoutAds() {
        try {
            QHADLog.d("-------------------渲染广告-------------------");
            Activity tmp = (Activity) context;
            if (tmp.isFinishing()) {
                if (vo.bmp != null) {
                    if (!vo.bmp.isRecycled()) {
                        vo.bmp.recycle();
                    }
                }
                return;
            }

            if (isShowbg) {
                if (Build.VERSION.SDK_INT >= 16) {
                    setBackground(QhAdModel.getInstance().getBackgroudDrawable());
                } else {
                    setBackgroundDrawable(QhAdModel.getInstance().getBackgroudDrawable());
                }
            } else {
                setBackgroundColor(Color.TRANSPARENT);
            }

            if (adView.getChildCount() != 0) {
                if (imageView != null) {
                    this.removeView(imageView);
                }

                if (webview != null) {
                    this.removeView(webview);
                }

                if (imgBtn != null) {
                    this.removeView(imgBtn);
                    imgBtn = null;
                }
            }
            switch (vo.adm_type) {
                case IMAGE:
                    QHADLog.d("渲染广告:静态图");
                    renderingImage();
                    QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.IMP_AD);
                    if (adView instanceof QhSplashAd) {
                        ((QhSplashAd) adView).startCountDown();
                    }
                    if (adViewEventListener != null)
                        adViewEventListener.onAdviewRendered();
                    break;

                case MRAID:
                    QHADLog.d("渲染广告:MRAID");
                    renderingMraid();
                    break;

                case DSP_HTML5:
                    QHADLog.d("渲染广告:DSP_HTML5");
                    renderingMraid();
                    break;

                case UNKOWN:
                    if (adViewEventListener != null) {
                        adViewEventListener.onAdviewGotAdFail();
                    }
                    break;

                default:
                    break;
            }
            this.setClickable(true);
        } catch (Exception e) {
            QHADLog.e("渲染广告:错误，Error Catched：" + e.getMessage());
        }

        this.bringChildToFront(imgBtn);
        this.bringChildToFront(confirmView);
    }

    protected void renderingMraid() {
    }

    /**
     * 渲染静态图片
     */
    protected void renderingImage() {
    }

    public interface Listener {
        void onRenderedSucceed();

    }

    private static class AdPlayTimerTask extends TimerTask {

        private WeakReference<QhAdView> adViewRef;
        private Handler handler;

        private AdPlayTimerTask(QhAdView qhAdView) {
            adViewRef = new WeakReference<>(qhAdView);
            handler = new Handler();
        }

        private QhAdView getAdView() {
            return adViewRef.get();
        }

        public void cleanHandler() {
            if (handler == null)
                return;
            handler.removeCallbacksAndMessages(null);
        }

        @Override
        public void run() {
            handler.post(new TaskRunnable(this));
        }

        private static class TaskRunnable implements Runnable {

            private final WeakReference<AdPlayTimerTask> timerTask;

            private TaskRunnable(AdPlayTimerTask timerTask) {
                this.timerTask = new WeakReference<>(timerTask);
            }

            private AdPlayTimerTask getTimerTask() {
                return timerTask.get();
            }

            @Override
            public void run() {
                AdPlayTimerTask timerTask = getTimerTask();
                if (timerTask == null)
                    return;
                QhAdView adView = timerTask.getAdView();
                if (adView == null) {
                    return;
                }
                StaticConfig.isActive = Utils.isAction(adView.context);
                StaticConfig.isOpenscreen = Utils.getScreenState(adView.context);
                if (adView.adType == AD_TYPE.BANNER || adView.adType == AD_TYPE.FLOAT_BANNER) {
                    if (StaticConfig.isActive) {
                        if (StaticConfig.isOpenscreen) {
                            if (adView.isShowad) {
                                Activity activity = (Activity) adView.context;
                                if (activity.isFinishing()) {
                                    timerTask.cancel();
                                    return;
                                }
                                adView.adView.getData();
                            }
                        }
                    }
                }
            }
        }
    }
}