/**
 *
 */
package com.qhad.ads.sdk.core;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhAdAttributes;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAd;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdListener;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdLoader;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.task.AsynDataLoader;
import com.qhad.ads.sdk.utils.RijindaelUtils;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * @author qihuajun
 */
public class QhVideoAdLoader implements IQhVideoAdLoader, DynamicObject {
    protected boolean isLoading = false;
    private String adspaceid;
    private boolean isTest;
    private String lastBannerid = "";
    private AD_TYPE adType = AD_TYPE.VIDEO;
    private IQhVideoAdListener adListener;
    private IQhAdAttributes adAttributes = null;

    /**
     * @param adspaceid
     * @param listener
     * @param isTest
     */
    public QhVideoAdLoader(String adspaceid, IQhVideoAdListener listener, Boolean isTest) {
        this.adspaceid = adspaceid;
        this.isTest = isTest;
        this.adListener = listener;
        // TODO Auto-generated constructor stub
    }

    public void loadAds() {
        QHADLog.i("QHAD", "QhVideoAdLoader Starting loadAds");

        if (isLoading) {
            QHADLog.i("QHAD", "QhVideoAdLoader skip loadAds, former loading in process");
            return;
        }

        isLoading = true;

        String url = assemblyUrl();

        new AsynDataLoader(url, new AsynDataLoader.Listener() {

            @Override
            public void onGetDataSucceed(ArrayList<CommonAdVO> vos) {
                QHADLog.i("QHAD", "Video ads loading succeed");

                ArrayList<IQhVideoAd> ads = new ArrayList<IQhVideoAd>();
                if (vos != null && !vos.isEmpty()) {
                    int size = vos.size();

                    for (int i = 0; i < size; i++) {
                        CommonAdVO vo = vos.get(i);
                        vo.adType = AD_TYPE.VIDEO;
                        lastBannerid = vo.bannerid;
                        QhVideoAd ad = new QhVideoAd(vo);
                        ads.add(ad);
                    }
                }

                isLoading = false;
                if (adListener != null) {
                    adListener.onVideoAdLoadSucceeded(ads);
                }

                isLoading = false;
            }

            @Override
            public void onGetDataFailed(String error) {
                QHADLog.i("QHAD", "Video ads loading fail");

                isLoading = false;


                if (adListener != null) {
                    adListener.onVideoAdLoadFailed();
                }
            }

            @Override
            public void onGetDataSucceed(CommonAdVO vo) {
                QHADLog.i("QHAD", "Video ads loading succeed");
                vo.adType = AD_TYPE.VIDEO;
                lastBannerid = vo.bannerid;
                QhVideoAd ad = new QhVideoAd(vo);

                isLoading = false;

                ArrayList<IQhVideoAd> ads = new ArrayList<IQhVideoAd>();

                if (adListener != null) {
                    ads.add(ad);
                    adListener.onVideoAdLoadSucceeded(ads);
                }
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
                    "&adsizewidth=640" +
                    "&adsizeheight=480" +
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
                    "&carrier=" + URLEncoder.encode(Utils.getNetworkOperator(), "utf-8")
            ;

            if (adAttributes != null && adAttributes instanceof IQhVideoAdAttributes) {
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

        } catch (Exception e) {
            QHADLog.d("URL编码失败");
        }

        return StaticConfig.HTTPS_AD_URL + param;
    }

    public void setAdAttributes(IQhAdAttributes attributes) {
        QHADLog.i("QHAD", "QhVideoAdLoader set ad attributes");
        adAttributes = attributes;
    }

    public void clearAdAttributes() {
        QHADLog.i("QHAD", "QhVideoAdLoader clear ad attributes");
        adAttributes = null;
    }

    @Override
    public Object invoke(int funcId, Object arg) {
        switch (funcId) {
            case _D.QHVIDEOADLOADER_loadAds:
                QHADLog.d("ADS", "QHVIDEOADLOADER_loadAds");
                loadAds();
                break;
            case _D.QHVIDEOADLOADER_setAdAttributes:
                QHADLog.d("ADS", "QHVIDEOADLOADER_setAdAttributes");
                setAdAttributes(QhAdAttributesProxy.create((DynamicObject) arg));
                break;
            case _D.QHVIDEOADLOADER_clearAdAttributes:
                QHADLog.d("ADS", "QHVIDEOADLOADER_clearAdAttributes");
                clearAdAttributes();
                break;
        }
        return null;
    }
}
