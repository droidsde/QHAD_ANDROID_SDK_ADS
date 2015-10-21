package com.qhad.ads.sdk.core;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdOnClickListener;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;

/**
 * Created by chengsiy on 2015/6/30.
 */
class QhVideoAdOnClickListenerProxy implements IQhVideoAdOnClickListener {

    private final DynamicObject dynamicObject;

    public QhVideoAdOnClickListenerProxy(DynamicObject dynamicObject) {
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
    public void onDownloadConfirmed() {
        QHADLog.d("ADS", "QHVIDEOADONCLICKLISTENER_onDownloadConfirmed");
        dynamicObject.invoke(_D.QHVIDEOADONCLICKLISTENER_onDownloadConfirmed, null);
    }

    @Override
    public void onDownloadCancelled() {
        QHADLog.d("ADS", "QHVIDEOADONCLICKLISTENER_onDownloadCancelled");
        dynamicObject.invoke(_D.QHVIDEOADONCLICKLISTENER_onDownloadCancelled, null);
    }

    @Override
    public void onLandingpageOpened() {
        QHADLog.d("ADS", "QHVIDEOADONCLICKLISTENER_onLandingpageOpened");
        dynamicObject.invoke(_D.QHVIDEOADONCLICKLISTENER_onLandingpageOpened, null);
    }

    @Override
    public void onLandingpageClosed() {
        QHADLog.d("ADS", "QHVIDEOADONCLICKLISTENER_onLandingpageClosed");
        dynamicObject.invoke(_D.QHVIDEOADONCLICKLISTENER_onLandingpageClosed, null);
    }
}
