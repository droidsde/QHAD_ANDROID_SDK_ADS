package com.qhad.ads.sdk.task;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.utils.AdCounter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class TrackRunable implements Runnable {

    private List<String> urls;
    private TrackType trackType;

    public TrackRunable(List<String> url, TrackType type) {
        urls = url;
        trackType = type;
    }

    @Override
    public void run() {
        trackUrls();
    }

    private void trackUrls() {
        for (String url : urls) {
            try {
                trackUrl(url);
                switch (trackType) {
                    case IMP_AD:
                        AdCounter.increment(AdCounter.ACTION_SDK_TRACK_IMPRESSION_DONE);
                        QHADLog.d("track imp done:" + url);
                        break;

                    case CLICK_AD:
                        AdCounter.increment(AdCounter.ACTION_SDK_TRACK_CLICK_DONE);
                        QHADLog.d("track click done:" + url);
                        break;

                    case OPEN_APP:
                        AdCounter.increment(AdCounter.ACTION_SERVICE_OPEN_APP_REPORTED);
                        QHADLog.d("track open app done:" + url);
                        break;

                    case DOWNLOAD_APP:
                        AdCounter.increment(AdCounter.ACTION_SERVICE_DOWNLOAD_APP_REPORTED);
                        QHADLog.d("track down app done:" + url);
                        break;

                    case INSTALL_APP:
                        AdCounter.increment(AdCounter.ACTION_SERVICE_INSTALL_APP_REPORTED);
                        QHADLog.d("track install app done:" + url);
                        break;

                    case ACTIVE_APP:
                        AdCounter.increment(AdCounter.ACTION_SERVICE_ACTIVATE_APP_REPORTED);
                        QHADLog.d("track activate app done:" + url);
                        break;

                    case FINISH_VIDEO_PLAY:
                        QHADLog.d("track video play done:" + url);
                        break;
                }
            } catch (Throwable e) {
                switch (trackType) {
                    case IMP_AD:
                        AdCounter.increment(AdCounter.ACTION_SDK_TRACK_IMPRESSION_FAILED);
                        QHADLog.e(QhAdErrorCode.REPORT_IMPRESSION_ERROR, "track imp error:" + url, e);
                        break;

                    case CLICK_AD:
                        AdCounter.increment(AdCounter.ACTION_SDK_TRACK_CLICK_FAILED);
                        QHADLog.e(QhAdErrorCode.REPORT_CLICK_ERROR, "track click error:" + url, e);
                        break;

                    case OPEN_APP:
                        QHADLog.e(QhAdErrorCode.REPORT_OPEN_ERROR, "track open app error:" + url, e);
                        break;

                    case DOWNLOAD_APP:
                        QHADLog.e(QhAdErrorCode.REPORT_DOWNLOAD_ERROR, "track download app error:" + url, e);
                        break;

                    case INSTALL_APP:
                        QHADLog.e(QhAdErrorCode.REPORT_INSTALL_ERROR, "track install app error:" + url, e);
                        break;

                    case ACTIVE_APP:
                        QHADLog.e(QhAdErrorCode.REPORT_ACTIVATATION_ERROR, "track activate app error:" + url, e);
                        break;

                    case FINISH_VIDEO_PLAY:
                        QHADLog.e(QhAdErrorCode.REPORT_VIDEO_ERROR, "track video play error:" + url, e);
                        break;
                }
            }
        }
    }

    /**
     * 发送监测请求
     *
     * @param urlString
     * @throws Throwable
     */
    private void trackUrl(String urlString) throws Exception {
        int count = 0;
        int statusCode = 0;
        Exception ex = null;

        while (count < 2) {
            count++;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(StaticConfig.NET_TIMEOUT * 2);
                conn.setUseCaches(false);
                conn.setRequestMethod("GET");
                statusCode = conn.getResponseCode();
                if (statusCode < 300 && statusCode >= 200) {
                    QHADLog.d("监测上报:成功:" + url);
                    return;
                } else {
                    QHADLog.d("监测上报:失败:" + urlString + " Response Code:" + statusCode);
                }

                Thread.sleep(1000);
            } catch (Exception e) {
                ex = e;
                QHADLog.d("监测上报:失败:" + urlString + " Exception:" + e.getMessage());
            }

        }

        throw new Exception("TrackURL Failed: ResponseCode:" + statusCode + "Exception:" + ex.getMessage());


    }
}
