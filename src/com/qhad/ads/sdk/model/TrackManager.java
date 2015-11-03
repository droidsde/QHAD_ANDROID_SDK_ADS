package com.qhad.ads.sdk.model;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.task.TrackRunable;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

public class TrackManager {

    private final int openApp = 6;
    private final int downloadApp = 7;
    private final int installApp = 8;
    private final int activeApp = 9;
    private final int finishVideo = 10;
    private final int downloadAppStart = 11;

    public void RegisterTrack(CommonAdVO vo, TrackType type) {
        switch (type) {
            case IMP_AD:
                QHADLog.d("	监测节点:曝光");
                AdCounter.increment(AdCounter.ACTION_SDK_TRACK_IMPRESSION);
                new Thread(new TrackRunable(vo.imp_tk, TrackType.IMP_AD)).start();
                break;

            case CLICK_AD:
                QHADLog.d("	监测节点:点击");
                AdCounter.increment(AdCounter.ACTION_SDK_TRACK_CLICK);
                new Thread(new TrackRunable(vo.click_tk, TrackType.CLICK_AD)).start();
                break;

            case OPEN_APP:
                QHADLog.d("	监测节点:应用已安装，直接打开");

                trackUrl(vo, openApp, TrackType.OPEN_APP);
                break;

            case DOWNLOAD_APP_START:
                QHADLog.d("	监测节点:应用下载开始");
                trackUrl(vo, downloadAppStart, TrackType.DOWNLOAD_APP_START);
                break;

            case DOWNLOAD_APP:
                QHADLog.d("	监测节点:应用下载完成");
                trackUrl(vo, downloadApp, TrackType.DOWNLOAD_APP);
                break;

            case INSTALL_APP:
                QHADLog.d("	监测节点:安装成功");
                trackUrl(vo, installApp, TrackType.INSTALL_APP);
                break;

            case ACTIVE_APP:
                QHADLog.d("	监测节点:激活成功");
                trackUrl(vo, activeApp, TrackType.ACTIVE_APP);
                break;

            case FINISH_VIDEO_PLAY:
                QHADLog.d("视频播放完成");
                trackUrl(vo, finishVideo, TrackType.FINISH_VIDEO_PLAY);
        }
    }

    private void trackUrl(CommonAdVO vo, int type, TrackType trackType) {
        ArrayList<String> urls = new ArrayList<String>();
        String param = "";
        try {
            if (type == finishVideo) {
                param = "?type=3&video=1&" +
                        "&vimpid=" + URLEncoder.encode(vo.impid, "utf-8") +
                        "&vbanner=" + URLEncoder.encode(vo.bannerid, "utf-8") +
                        "&vstop=" + URLEncoder.encode(vo.videoPlayedTime + "", "utf-8") +
                        "&mimei=" + URLEncoder.encode(Utils.getIMEI(), "utf-8") +
                        "&mimei_md5=" + URLEncoder.encode(Utils.getIMEIWhitMD5(), "utf-8") +
                        "&mimsi=" + URLEncoder.encode(Utils.getIMSI(), "utf-8") +
                        "&mimsi_md5=" + URLEncoder.encode(Utils.getIMSIWhitMD5(), "utf-8") +
                        "&mmac=" + URLEncoder.encode(Utils.getMac(), "utf-8") +
                        "&mmac_md5=" + URLEncoder.encode(Utils.getMacWhitMD5(), "utf-8");
            } else {
                param = "?jzqt=tran" +
                        "&type=3" +
                        "&db=none" +
                        "&jzqo=" + getRandomHash() +
                        "&jzqot=" +
                        "&jzqc=" +
                        "&jzqs=" +
                        "&jzqv=" +
                        "&jzqrd=" +
                        "&jzqopt=" +
                        "&cus=" + URLEncoder.encode(vo.advertiserid, "utf-8") +
                        "&mcampaignid=" + URLEncoder.encode(vo.campaignid, "utf-8") +
                        "&msolutionid=" + URLEncoder.encode(vo.solutionid, "utf-8") +
                        "&mbannerid=" + URLEncoder.encode(vo.bannerid, "utf-8") +
                        "&mpkg=" + URLEncoder.encode(vo.pkg, "utf-8") +
                        "&jzqosr=" + URLEncoder.encode(vo.clickEventId, "utf-8") +
                        "&impid=" + URLEncoder.encode(vo.impid, "utf-8") +
                        "&jzqotp=" + type +
                        "&jzqchl=" + URLEncoder.encode(StaticConfig.TRACK_CHANNEL_ID, "utf-8") +
                        "&mimei=" + URLEncoder.encode(Utils.getIMEI(), "utf-8") +
                        "&mimei_md5=" + URLEncoder.encode(Utils.getIMEIWhitMD5(), "utf-8") +
                        "&mimsi=" + URLEncoder.encode(Utils.getIMSI(), "utf-8") +
                        "&mimsi_md5=" + URLEncoder.encode(Utils.getIMSIWhitMD5(), "utf-8") +
                        "&mmac=" + URLEncoder.encode(Utils.getMac(), "utf-8") +
                        "&mmac_md5=" + URLEncoder.encode(Utils.getMacWhitMD5(), "utf-8") +
                        "&mmodel=" + URLEncoder.encode(Utils.getProductModel(), "utf-8").replace("+", "%20") +
                        "&msdkv=" + URLEncoder.encode(StaticConfig.SDK_VERSION, "utf-8") +
                        "&mappv=" + URLEncoder.encode(Utils.getAppVersion(), "utf-8") +
                        "&mappname=" + URLEncoder.encode(Utils.getAppname(), "utf-8") +
                        "&mapppkg=" + URLEncoder.encode(Utils.getAppPackageName(), "utf-8") +
                        "&mlongitude=" + URLEncoder.encode(StaticConfig.longitude, "utf-8") +
                        "&mlatitude=" + URLEncoder.encode(StaticConfig.latitude, "utf-8") +
                        "&mos=" + Utils.getSysteminfo() +
                        "&mandroidid=" + URLEncoder.encode(Utils.getAndroidid(), "utf-8") +
                        "&mandroidid_md5=" + URLEncoder.encode(Utils.getAndroididWithMD5(), "utf-8") +
                        "&mnet=" + URLEncoder.encode(Utils.getCurrentNetWorkInfo(), "utf-8") +
                        "&mbrand=" + URLEncoder.encode(Utils.getBrand(), "utf-8").replace("+", "%20") +
                        "&mcarrier=" + URLEncoder.encode(Utils.getNetworkOperator(), "utf-8") +
                        "&m2id=" + URLEncoder.encode(Utils.getm2id(), "utf-8").replace("+", "%20") +
                        "&mserialid=" + URLEncoder.encode(Utils.getDeviceSerial(), "utf-8").replace("+", "%20") +
                        "&mdevicetype=" + Utils.getDeviceType();
            }
        } catch (Exception e) {
            QHADLog.d("URL编码失败");
        }
        String url = StaticConfig.LOG_URL + param;
        String time = "";
        if (type == installApp) {
            time = "&jzqo1=" + vo.installStartTime +
                    "&jzqo2=" + vo.installEndTime;
        } else if (type == activeApp) {
            time = "&jzqo1=" + vo.activeStartTime +
                    "&jzqo2=" + vo.activeEndTime;
        } else if (type == finishVideo) {
            time = "&jzqo1=" + vo.videoStartTime +
                    "&jzqo2=" + vo.videoEndTime;
        } else if (type == downloadApp) {
            time = "&jzqo1=" + vo.downloadStartTime +
                    "&jzqo2=" + vo.downloadEndTime;
        } else {
            time = "&jzqo1=" + "&jzqo2=";
        }

        url = url + time;
        if (type == downloadAppStart) {

            try {
                url = StaticConfig.QHSZLOG_URL + "?mnq=mvad";
                if (vo.softid != null) {
                    url = url + "&sid=" + URLEncoder.encode(vo.softid + "", "utf-8");
                } else {
                    url = url + "&sid=";
                }

                if (vo.adspaceid != null) {
                    url = url + "&pos=" + URLEncoder.encode(vo.adspaceid + "", "utf-8");
                } else {
                    url = url + "&pos=";
                }
                url = url +
                        "&m=" + URLEncoder.encode(Utils.getIMEIWhitMD5(), "utf-8") +
                        "&m3=" + URLEncoder.encode(Utils.getIMSIWhitMD5(), "utf-8") +
                        "&mid=" + URLEncoder.encode(Utils.getMacWhitMD5(), "utf-8");
            } catch (Exception e) {
                QHADLog.d("SZ URL编码失败");
            }
        }
        urls.add(url);
        new Thread(new TrackRunable(urls, trackType)).start();
    }

    private String getRandomHash() {
        String code = "";
        try {
            Random random = new Random(System.currentTimeMillis());
            int i = random.nextInt();
            code = "" + System.currentTimeMillis() + i + Utils.getIMEI().hashCode();
            code = code.replace("-", "");
        } catch (Exception e) {
            QHADLog.e("Hash随机数失败");
        }
        return code;
    }
}