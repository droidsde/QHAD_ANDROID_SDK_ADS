package com.qhad.ads.sdk.model;

import android.app.Activity;

import com.qhad.ads.sdk.core.QhAdView;
import com.qhad.ads.sdk.core.QhFloatbannerAd;
import com.qhad.ads.sdk.core.QhInterstitialAd;
import com.qhad.ads.sdk.core.QhNativeBannerAd;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InstanceModel {

    private static InstanceModel instance = null;

    private QhInterstitialAd interstitialInstance = null;
    private QhFloatbannerAd floatbannerInstance = null;

    private Map<Integer, List<AdContext>> adInstanceMap = new HashMap<>();

    /**
     * ********************Banner_End***********************
     */

    public static InstanceModel getInstance() {
        if (instance == null) {
            instance = new InstanceModel();
        }
        return instance;
    }

    /**
     * ********************Interstitial_Start*********************
     */
    public QhInterstitialAd getInterstitialInstance() {
        return interstitialInstance;
    }

    /**
     * ********************Interstitial_End***********************
     */

    public void setInterstitialInstance(QhInterstitialAd ad) {
        interstitialInstance = ad;
    }

    /**
     * ********************Floatbanner_Start*********************
     */
    public QhFloatbannerAd getFloatbannerInstance() {
        return floatbannerInstance;
    }

    /**
     * ********************Floatbanner_End***********************
     */

    public void setFloatbannerInstance(QhFloatbannerAd ad) {
        floatbannerInstance = ad;
    }

    /**
     * ********************Banner_Start*********************
     */

    public void regAdInstance(Activity activity, QhAdView ad) {
        if (!(ad instanceof QhNativeBannerAd))
            checkadContextLife();
        List<AdContext> ads = adInstanceMap.get(activity.hashCode());
        AdContext adContext = new AdContext(ad);
        if (ads == null) {
            ads = new ArrayList<>();
            adInstanceMap.put(activity.hashCode(), ads);
        }

        ads.add(adContext);

    }

    public void checkadContextLife(int activityHashCode) {
        if (!adInstanceMap.containsKey(activityHashCode))
            return;
        List<AdContext> adContextList = adInstanceMap.get(activityHashCode);
        for (AdContext adContext : adContextList) {
            QhAdView ad = adContext.getAd();
            if (ad != null) {
                ad.onActivityFinishing();
            }
        }
        adContextList.clear();
        adInstanceMap.remove(activityHashCode);
    }

    public void checkadContextLife() {
        Set<Integer> codes = new HashSet<>(adInstanceMap.keySet());
        for (Integer code : codes) {
            checkadContextLife(code);
        }
    }
}

class AdContext {
    private final WeakReference<QhAdView> adWeakReference;

    public AdContext(QhAdView ad) {
        adWeakReference = new WeakReference<>(ad);
    }

    public QhAdView getAd() {
        return adWeakReference.get();
    }

}