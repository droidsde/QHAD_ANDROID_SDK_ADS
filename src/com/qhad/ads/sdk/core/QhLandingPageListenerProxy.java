package com.qhad.ads.sdk.core;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageListener;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;

/**
 * Created by chengsiy on 2015/6/30.
 */
class QhLandingPageListenerProxy implements DynamicObject {

    private final IQhLandingPageListener listener;

    public QhLandingPageListenerProxy(IQhLandingPageListener listener) {
        if (listener == null)
            listener = new IQhLandingPageListener() {
                @Override
                public void onPageClose() {

                }

                @Override
                public void onPageLoadFinished() {

                }

                @Override
                public void onPageLoadFailed() {

                }

                @Override
                public boolean onAppDownload(String url) {
                    return false;
                }
            };
        this.listener = listener;
    }

    @Override
    public Object invoke(int funcId, Object arg) {
        switch (funcId) {
            case _D.QHLANDINGPAGELISTENER_onPageClose:
                QHADLog.d("ADS", "QHLANDINGPAGELISTENER_onPageClose");
                listener.onPageClose();
                break;
            case _D.QHLANDINGPAGELISTENER_onPageLoadFinished:
                QHADLog.d("ADS", "QHLANDINGPAGELISTENER_onPageLoadFinished");
                listener.onPageLoadFinished();
                break;
            case _D.QHLANDINGPAGELISTENER_onPageLoadFailed:
                QHADLog.d("ADS", "QHLANDINGPAGELISTENER_onPageLoadFailed");
                listener.onPageLoadFailed();
                break;
            case _D.QHLANDINGPAGELISTENER_onAppDownload:
                QHADLog.d("ADS", "QHLANDINGPAGELISTENER_onAppDownload");
                listener.onAppDownload((String) arg);
                break;
        }
        return null;
    }
}
