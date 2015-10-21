package com.qhad.ads.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;

import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Created by qihuajun on 2015/6/27.
 */
public class AdCounter {

    public static final int ACTION_LOAD_ADS_CALLED = 0;
    public static final int ACTION_LOAD_ADS_START = 1;
    public static final int ACTION_LOAD_ADS_SUCCEED = 2;
    public static final int ACTION_LOAD_ADS_FAILED = 3;
    public static final int ACTION_APP_CALL_IMPTRACK = 4;
    public static final int ACTION_SDK_TRACK_IMPRESSION = 5;
    public static final int ACTION_SDK_TRACK_IMPRESSION_DONE = 6;
    public static final int ACTION_SDK_TRACK_IMPRESSION_FAILED = 7;
    public static final int ACTION_SDK_ON_CLICK_CALLED = 8;
    public static final int ACTION_SDK_ON_CLICK_INNERBROWSER = 9;
    public static final int ACTION_SDK_ON_CLICK_SYSTEMBROWSER = 10;
    public static final int ACTION_SDK_ON_CLICK_DOWNLOAD = 11;
    public static final int ACTION_SDK_TRACK_CLICK = 12;
    public static final int ACTION_SDK_TRACK_CLICK_FAILED = 13;
    public static final int ACTION_SDK_TRACK_CLICK_DONE = 14;
    public static final int ACTION_CANCEL_DOWNLOAD = 15;
    public static final int ACTION_CONFIRM_DOWNLOAD = 16;
    public static final int ACTION_SERVICE_RECEIVE_DOWNLOAD = 17;
    public static final int ACTION_SERVICE_START_DOWNLOAD = 18;
    public static final int ACTION_SERVICE_OPEN_APP = 19;
    public static final int ACTION_SERVICE_OPEN_APP_REPORTED = 20;
    public static final int ACTION_SERVICE_APP_FILE_DOWNLOADED = 21;
    public static final int ACTION_SERVICE_START_INSTALL_APP = 22;
    public static final int ACTION_SERVICE_START_SYSTEM_DOWNLOAD = 23;
    public static final int ACTION_SERVICE_CREATE_NEW_SYSTEM_DOWNLOAD = 24;
    public static final int ACTION_SERVICE_CREATE_NEW_SYSTEM_DOWNLOAD_DONE = 25;
    public static final int ACTION_SERVICE_FOUND_EXISTED_SYSTEM_DOWNLOAD = 26;
    public static final int ACTION_SERVICE_START_USE_APPDOWNLOAD = 27;
    public static final int ACTION_SERVICE_START_USE_APPDOWNLOAD_DONE = 28;
    public static final int ACTION_SERVICE_START_USE_APPDOWNLOAD_FAILED = 29;
    public static final int ACTION_SERVICE_RECEIVE_SYSTEM_DOWNLOAD_NOTIFICATION = 30;
    public static final int ACTION_SERVICE_RECEIVED_SYSTEM_DOWNLOAD_SUCCEED = 31;
    public static final int ACTION_SERVICE_RECEIVED_SYSTEM_DOWNLOAD_FAILED = 32;
    public static final int ACTION_SERVICE_RECEIVED_SYSTEM_DOWNLOAD_FILE_PARSED = 33;
    public static final int ACTION_SERVICE_START_REPORT_DOWNLOAD_COMPLETE = 34;
    public static final int ACTION_SERVICE_DOWNLOAD_APP_REPORTED = 35;
    public static final int ACTION_SERVICE_REGISTER_INSTALL_RECEIVER = 36;
    public static final int ACTION_SERVICE_RECEIVED_INSTALL_MATCHED = 37;
    public static final int ACTION_SERVICE_START_REPORT_INSTALL_COMPLETE = 38;
    public static final int ACTION_SERVICE_INSTALL_APP_REPORTED = 39;
    public static final int ACTION_SERVICE_REGISTER_ACTIVEAPP_MONITOR = 40;
    public static final int ACTION_SERVICE_START_REPORT_ACTIVATE_APP = 41;
    public static final int ACTION_SERVICE_ACTIVATE_APP_REPORTED = 42;
    public static final int ACTION_SDK_NATVIEADLOADER_LOAD_SKIP = 43;
    public static final int ACTION_DEEP_LINK_OPEN_START = 44;
    public static final int ACTION_DEEP_LINK_OPEN_SUCCESS = 45;
    public static final int ACTION_DEEP_LINK_OPEN_FAILED = 46;
    //所有打点总数，增加几个则加几个
    private static final int ACTIONS_COUNT = 47;
    private static Context context;

    /**
     * 初始化Context
     *
     * @param c
     */
    public static void initContext(Context c) {
        context = c;
    }


    /**
     * 为某个Action计数加1
     *
     * @param actionid
     */
    public static synchronized void increment(int actionid) {
        QHADLog.d("Action INC:" + actionid);
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String key = "QHAD_ACTON_COUNT" + df.format(System.currentTimeMillis());
            int count = context.getSharedPreferences(key, Context.MODE_PRIVATE).getInt(String.valueOf(actionid), 0);
            count++;
            context.getSharedPreferences(key, Context.MODE_PRIVATE).edit().putInt(String.valueOf(actionid), count).commit();
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.STAT_COLLECT_ERROR, "increment action failed, actionid:" + actionid, e);
        }

    }

    /**
     * 获取某日的统计数据
     *
     * @param date
     * @return
     */
    public static int[] getDailyCounts(String date) {
        String key = "QHAD_ACTON_COUNT" + date;
        int[] ret = new int[ACTIONS_COUNT];

        SharedPreferences sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);

        for (int i = 0; i < ACTIONS_COUNT; i++) {
            String actionid = String.valueOf(i);
            int v = sharedPreferences.getInt(actionid, 0);
            ret[i] = v;
        }

        return ret;
    }

    /**
     * 清除某天的数据
     *
     * @param date
     */
    public static void clearDailyCounts(String date) {
        String key = "QHAD_ACTON_COUNT" + date;
        context.getSharedPreferences(key, Context.MODE_PRIVATE).edit().clear().commit();
    }

    /**
     * 把统计数据上传到服务器
     */
    public static void uploadCounts() {
        QHADLog.d("Uploading stat data");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String date = df.format(System.currentTimeMillis() - 24 * 3600 * 1000);

        String key = "QHAD_ADCOUNTER_UPLOADED_" + date;
        boolean uploaded = context.getSharedPreferences(key, Context.MODE_PRIVATE).getBoolean("uploaded", false);
        if (!uploaded) {
            QHADLog.d("to upload stats");
            int[] counts = getDailyCounts(date);
            String msg = date + Arrays.toString(counts);
            QHADLog.e(QhAdErrorCode.STAT_COLLECT, msg);
            context.getSharedPreferences(key, Context.MODE_PRIVATE).edit().putBoolean("uploaded", true).commit();
            clearDailyCounts(date);
        } else {
            QHADLog.d("stats already uploaded ");
        }


    }
}
