package com.qhad.ads.sdk.core;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAd;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdListener;
import com.qhad.ads.sdk.interfaces.DynamicObject;

import java.util.ArrayList;

/**
 * Created by chengsiy on 2015/6/30.
 */
class QhNativeAdListenerProxy implements IQhNativeAdListener {

    private final DynamicObject dynamicObject;

    public QhNativeAdListenerProxy(DynamicObject dynamicObject) {
        if (dynamicObject == null)
            dynamicObject = new DynamicObject() {
                @Override
                public Object invoke(int funcId, Object arg) {
                    return null;
                }
            };
        this.dynamicObject = dynamicObject;
    }

    @Override
    public void onNativeAdLoadSucceeded(ArrayList<IQhNativeAd> nativeAds) {
        dynamicObject.invoke(_D.QHNATIVEADLISTENER_onNativeAdLoadSucceeded, nativeAds);
    }

    @Override
    public void onNativeAdLoadFailed() {
        dynamicObject.invoke(_D.QHNATIVEADLISTENER_onNativeAdLoadFailed, null);
    }
}
