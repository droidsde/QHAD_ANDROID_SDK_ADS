package com.qhad.ads.sdk.core;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAd;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdListener;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;

import java.util.ArrayList;

/**
 * Created by chengsiy on 2015/6/30.
 */
class QhVideoAdListenerProxy implements IQhVideoAdListener {

    private final DynamicObject dynamicObject;

    public QhVideoAdListenerProxy(DynamicObject dynamicObject) {
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
    public void onVideoAdLoadSucceeded(ArrayList<IQhVideoAd> ads) {
        QHADLog.d("ADS", "QHVIDEOADLISTENER_onVideoAdLoadSucceeded");
        dynamicObject.invoke(_D.QHVIDEOADLISTENER_onVideoAdLoadSucceeded, ads);
    }

    @Override
    public void onVideoAdLoadFailed() {
        QHADLog.d("ADS", "QHVIDEOADLISTENER_onVideoAdLoadFailed");
        dynamicObject.invoke(_D.QHVIDEOADLISTENER_onVideoAdLoadFailed, null);
    }
}
