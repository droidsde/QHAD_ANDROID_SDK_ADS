package com.qhad.ads.sdk.core;

import android.content.Context;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageListener;
import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageView;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;

/**
 * Created by chengsiy on 2015/6/30.
 */
class QhLandingPageViewProxy implements IQhLandingPageView {

    private final DynamicObject dynamicObject;

    public QhLandingPageViewProxy(DynamicObject dynamicObject) {
        this.dynamicObject = dynamicObject;
    }

    @Override
    public void open(Context context, String url, IQhLandingPageListener listener) {
        QHADLog.d("ADS", "QHLANDINGPAGEVIEW_open");
        dynamicObject.invoke(_D.QHLANDINGPAGEVIEW_open, new Object[]{context, url, new QhLandingPageListenerProxy(listener)});
    }
}
