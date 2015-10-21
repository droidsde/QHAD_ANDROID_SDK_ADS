package com.qhad.ads.sdk.core;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhAdAttributes;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAd;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdListener;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdLoader;
import com.qhad.ads.sdk.adsinterfaces.IQhProductAdAttributes;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.task.AsynDataLoader;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.utils.RijindaelUtils;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

public class QhNativeAdLoader implements IQhNativeAdLoader, DynamicObject {

    protected boolean isLoading = false;
    private String adspaceid;
    private boolean isTest;
    private AD_TYPE adType = AD_TYPE.NATIVE;
    private String lastBannerid = "";
    private IQhNativeAdListener adListener = null;
    private HashSet<String> keywords = null;
    private IQhAdAttributes adAttributes = null;
    private int maxbanners = 0;
    private Context context;
    private String uid = "";

    public QhNativeAdLoader(Activity activity, String adspaceid, IQhNativeAdListener listener, Boolean isTest) {
        this.adspaceid = adspaceid;
        this.isTest = isTest;
        this.adListener = listener;
        this.context = activity;

        uid = Utils.MD5(Utils.getIMEI() + Utils.getAppPackageName() + activity.hashCode());
    }


    public void setKeywords(HashSet<String> keywordList) {
        QHADLog.i("QHAD", "QhVideoAdLoader setKeywords " + this.adspaceid);
        this.keywords = keywordList;
    }

    public void loadAds(int n) {
        QHADLog.i("QHAD", "NativeAdLoader loadAds " + this.adspaceid);
        if (n > 0 && n <= 5) {
            executeLoadAds(n);
        } else {
            executeLoadAds(0);
        }
    }

    public void loadAds() {
        QHADLog.i("QHAD", "NativeAdLoader loadAds " + this.adspaceid);
        executeLoadAds(0);
    }

    private void executeLoadAds(int n) {
        AdCounter.increment(AdCounter.ACTION_LOAD_ADS_CALLED);

        if (isLoading) {
            AdCounter.increment(AdCounter.ACTION_SDK_NATVIEADLOADER_LOAD_SKIP);
            QHADLog.i("QHAD", "NativeAdLoader loadAdsRepeated " + adspaceid);
            return;
        }

        maxbanners = n;

        isLoading = true;

        String url = assemblyUrl();

        new AsynDataLoader(url, new AsynDataLoader.Listener() {

            @Override
            public void onGetDataSucceed(ArrayList<CommonAdVO> vos) {
                QHADLog.i("QHAD", "NativeAdLoader onGetDataSucceed " + adspaceid);
                AdCounter.increment(AdCounter.ACTION_LOAD_ADS_SUCCEED);

                ArrayList<IQhNativeAd> nativeAds = new ArrayList<IQhNativeAd>();
                if (vos != null && !vos.isEmpty()) {
                    int size = vos.size();

                    for (int i = 0; i < size; i++) {
                        CommonAdVO vo = vos.get(i);
                        vo.adType = AD_TYPE.NATIVE;
                        lastBannerid = vo.bannerid;
                        QhNativeAd ad = new QhNativeAd(vo, context);
                        nativeAds.add(ad);
                    }
                }

                isLoading = false;
                if (adListener != null) {
                    adListener.onNativeAdLoadSucceeded(nativeAds);
                }

            }

            @Override
            public void onGetDataFailed(String error) {
                QHADLog.i("QHAD", "NativeAdLoader onGetDataFailed " + adspaceid);
                AdCounter.increment(AdCounter.ACTION_LOAD_ADS_FAILED);

                isLoading = false;


                if (adListener != null) {
                    adListener.onNativeAdLoadFailed();
                }
            }

            @Override
            public void onGetDataSucceed(CommonAdVO vo) {
                QHADLog.i("QHAD", "NativeAdLoader onGetDataSucceed " + adspaceid);
                AdCounter.increment(AdCounter.ACTION_LOAD_ADS_SUCCEED);

                vo.adType = AD_TYPE.NATIVE;
                ArrayList<IQhNativeAd> nativeAds = new ArrayList<>();
                lastBannerid = vo.bannerid;
                QhNativeAd ad = new QhNativeAd(vo, context);
                nativeAds.add(ad);

                isLoading = false;

                if (adListener != null) {
                    adListener.onNativeAdLoadSucceeded(nativeAds);
                }
            }
        });

        AdCounter.increment(AdCounter.ACTION_LOAD_ADS_START);
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
            QHADLog.e(e.getMessage());
        }

        try {
            /** in test*/
            Random random = new Random(System.currentTimeMillis());
            @SuppressWarnings("unused")
            int num = Math.abs(random.nextInt());
            /** in test*/

            param = "?adspaceid=" + URLEncoder.encode(adspaceid, "utf-8") +
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
                    "&adsizewidth=320" +
                    "&adsizeheight=50" +
                    "&so=" + URLEncoder.encode(Utils.getScreenOrientation(), "utf-8") +
                    "&density=" + Utils.getDeviceDensity() +
                    "&appname=" + URLEncoder.encode(Utils.getAppname(), "utf-8") +
                    "&apppkg=" + URLEncoder.encode(Utils.getAppPackageName(), "utf-8") +
                    "&istest=" + (isTest ? "1" : "0") +
                    "&net=" + URLEncoder.encode(Utils.getCurrentNetWorkInfo(), "utf-8") +
                    "&adtype=" + adType.ordinal() +
                    "&it=" + it +
                    "&ic=" + ic +
                    "&androidid=" + URLEncoder.encode(Utils.getAndroidid(), "utf-8") +
                    "&androidid_md5=" + URLEncoder.encode(Utils.getAndroididWithMD5(), "utf-8") +
                    "&lastbannerid=" + lastBannerid +
                    "&longitude=" + URLEncoder.encode(StaticConfig.longitude, "utf-8") +
                    "&latitude=" + URLEncoder.encode(StaticConfig.latitude, "utf-8") +
                    "&brand=" + URLEncoder.encode(Utils.getBrand(), "utf-8").replace("+", "%20") +
                    "&carrier=" + URLEncoder.encode(Utils.getNetworkOperator(), "utf-8") +
                    "&m2id=" + URLEncoder.encode(Utils.getm2id(), "utf-8").replace("+", "%20") +
                    "&serialid=" + URLEncoder.encode(Utils.getDeviceSerial(), "utf-8").replace("+", "%20") +
                    "&devicetype=" + Utils.getDeviceType() +
                    "&uid=" + URLEncoder.encode(uid, "utf-8");

            if (keywords != null) {
                param += "&qhtag=" + URLEncoder.encode(TextUtils.join("_", keywords), "utf-8").replace("+", "%20");
            }

            if (adAttributes != null && adAttributes instanceof IQhProductAdAttributes) {
                HashMap<String, String> map = adAttributes.getAttributes();
                Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    String key = entry.getKey();
                    String value = entry.getValue();
                    String attrs = "&" + key + "=" + URLEncoder.encode(value, "utf-8").replace("+", "%20");
                    param += attrs;
                }
            }

            if (maxbanners > 0 && maxbanners <= 5) {
                param += "&maxbanners=" + maxbanners;
            }

        } catch (Exception e) {
            QHADLog.d("URL编码失败");
        }

        return StaticConfig.AD_URL + param;
    }


    @Override
    public void setAdAttributes(IQhAdAttributes attributes) {
        QHADLog.i("QHAD", "QhVideoAdLoader setAdAttributes " + this.adspaceid);
        adAttributes = attributes;
    }


    @Override
    public void clearKeywords() {
        QHADLog.i("QHAD", "QhVideoAdLoader clearKeywords" + this.adspaceid);
        keywords = null;
    }


    @Override
    public void clearAdAttributes() {
        QHADLog.i("QHAD", "QhVideoAdLoader clearAdAttributes" + this.adspaceid);
        adAttributes = null;
    }


    @Override
    public Object invoke(int funcId, Object arg) {
        switch (funcId) {
            case _D.QHNATIVEADLOADER_loadAds:
                loadAds();
                break;
            case _D.QHNATIVEADLOADER_loadAds_2:
                loadAds((int) arg);
                break;
            case _D.QHNATIVEADLOADER_setKeywords:
                setKeywords((HashSet<String>) arg);
                break;
            case _D.QHNATIVEADLOADER_clearKeywords:
                clearKeywords();
                break;
            case _D.QHNATIVEADLOADER_setAdAttributes:
                setAdAttributes(QhAdAttributesProxy.create((DynamicObject) arg));
                break;
            case _D.QHNATIVEADLOADER_clearAdAttributes:
                clearAdAttributes();
                break;
        }
        return null;
    }
}
