package com.qhad.ads.sdk.core;

import android.webkit.JavascriptInterface;

import com.qhad.ads.sdk.utils.Utils;

import java.util.HashMap;

public class QhMraidInterface {

    private String adw = null;
    private String adh = null;
    private AD_TYPE adtype = null;

    public QhMraidInterface(String _adw, String _adh, AD_TYPE _adtype) {
        adw = _adw;
        adh = _adh;
        adtype = _adtype;
    }

    /**
     * *********************************** MRAID V1 Not Support **********************************
     */

    @JavascriptInterface
    public Boolean close() {
        return false;
    }

    @JavascriptInterface
    public Boolean open(String url) {
        return false;
    }

    @JavascriptInterface
    public Boolean deeplink(String urljson) {
        return false;
    }

    @JavascriptInterface
    public Boolean expand(String url) {
        return false;
    }

    /**
     * *********************************** PRIVATE **********************************
     */

    @JavascriptInterface
    public Boolean appDownload(String url, String pn) {
        return false;
    }

    @JavascriptInterface
    public Boolean callTelephone(String callnum) {
        return false;
    }

    @JavascriptInterface
    public Boolean sendSMS(String callnum, String content) {
        return false;
    }

    @JavascriptInterface
    public Boolean sendMail(String emailadd, String title, String content) {
        return false;
    }

    @JavascriptInterface
    public String getCurrentAdsStatus() {
        HashMap<String, String> map = new HashMap<String, String>();
        int[] swh = Utils.getDeviceScreenSizeWithInt();
        String density = String.valueOf(Utils.getDeviceDensity());
        String at = "";
        switch (adtype) {
            case BANNER:
                at = "0";
                break;

            case INTERSTITIAL:
                at = "1";
                break;

            default:
                break;
        }

        map.put("sw", String.valueOf(swh[0]));  //屏宽
        map.put("sh", String.valueOf(swh[1]));  //屏高
        map.put("adw", adw);                    //广告位宽
        map.put("adh", adh);                    //广告位高
        map.put("den", density);                //密度
        map.put("adtype", at);                    //广告类型

        String content = "sw" + ":" + String.valueOf(swh[0]) + "," +
                "sh" + ":" + String.valueOf(swh[1]) + "," +
                "adw" + ":" + adw + "," +
                "adh" + ":" + adh + "," +
                "den" + ":" + density + "," +
                "adtype" + ":" + at;
        return content;
    }

    /**
     * *********************************** MRAID V2 Not Support **********************************
     */

    @JavascriptInterface
    public Boolean setExpandProperties(double width, double height, Boolean useCustomClose, Boolean isModal) {
        return false;
    }

    @JavascriptInterface
    public void mraidMiddlewareDone() {

    }

    @JavascriptInterface
    public Boolean useCustomClose() {
        return false;
    }

    @JavascriptInterface
    public Boolean setResizeProperties(double width,
                                       double height,
                                       String customClosePosition,
                                       double offsetX,
                                       double offsetY,
                                       Boolean allowOffscreen) {
        return false;
    }

    @JavascriptInterface
    public Boolean resize() {
        return false;
    }

    @JavascriptInterface
    public Boolean storePicture(String url) {
        return false;
    }

    @JavascriptInterface
    public Boolean playVideo(String videoUrl) {
        return false;
    }

    @JavascriptInterface
    public Boolean createCalendarEvent(String description, String location,
                                       String start, String end) {
        return false;
    }

    @JavascriptInterface
    public Boolean setOrientationProperties(Boolean allowOrientationChange, String forceOrientation) {
        return false;
    }

}
