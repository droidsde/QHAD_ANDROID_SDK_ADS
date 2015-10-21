package com.qhad.ads.sdk.adsinterfaces;

/**
 * Created by chengsiy on 2015/6/24.
 */
public interface IQhLandingPageListener {
    void onPageClose();

    void onPageLoadFinished();

    void onPageLoadFailed();

    boolean onAppDownload(String url);
}
