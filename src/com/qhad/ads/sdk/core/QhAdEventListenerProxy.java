package com.qhad.ads.sdk.core;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhAdEventListener;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;

/**
 * Created by chengsiy on 2015/6/29.
 */
class QhAdEventListenerProxy implements IQhAdEventListener {

    private final DynamicObject dynamicObject;

    public QhAdEventListenerProxy(DynamicObject dynamicObject) {
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
    public void onAdviewGotAdSucceed() {
        QHADLog.d("ADS", "QHADEVENTLISTENER_onAdviewGotAdSucceed");
        dynamicObject.invoke(_D.QHADEVENTLISTENER_onAdviewGotAdSucceed, null);
    }

    @Override
    public void onAdviewGotAdFail() {
        QHADLog.d("ADS", "QHADEVENTLISTENER_onAdviewGotAdFail");
        dynamicObject.invoke(_D.QHADEVENTLISTENER_onAdviewGotAdFail, null);
    }

    @Override
    public void onAdviewRendered() {
        QHADLog.d("ADS", "QHADEVENTLISTENER_onAdviewRendered");
        dynamicObject.invoke(_D.QHADEVENTLISTENER_onAdviewRendered, null);
    }

    @Override
    public void onAdviewIntoLandpage() {
        QHADLog.d("ADS", "QHADEVENTLISTENER_onAdviewIntoLandpage");
        dynamicObject.invoke(_D.QHADEVENTLISTENER_onAdviewIntoLandpage, null);
    }

    @Override
    public void onAdviewDismissedLandpage() {
        QHADLog.d("ADS", "QHADEVENTLISTENER_onAdviewDismissedLandpage");
        dynamicObject.invoke(_D.QHADEVENTLISTENER_onAdviewDismissedLandpage, null);
    }

    @Override
    public void onAdviewClicked() {
        QHADLog.d("ADS", "QHADEVENTLISTENER_onAdviewClicked");
        dynamicObject.invoke(_D.QHADEVENTLISTENER_onAdviewClicked, null);
    }

    @Override
    public void onAdviewClosed() {
        QHADLog.d("ADS", "QHADEVENTLISTENER_onAdviewClosed");
        dynamicObject.invoke(_D.QHADEVENTLISTENER_onAdviewClosed, null);
    }

    @Override
    public void onAdviewDestroyed() {
        QHADLog.d("ADS", "QHADEVENTLISTENER_onAdviewDestroyed");
        dynamicObject.invoke(_D.QHADEVENTLISTENER_onAdviewDestroyed, null);
    }
}
