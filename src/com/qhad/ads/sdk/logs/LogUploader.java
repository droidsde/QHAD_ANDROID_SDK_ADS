package com.qhad.ads.sdk.logs;

import android.content.Context;
import android.content.SharedPreferences;

import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.task.NetsTask;
import com.qhad.ads.sdk.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by qihuajun on 2015/5/19.
 */
public class LogUploader {
    private final static String ERROR_LOG_KEY = "qhadsdkerrordaycheck";
    private final static int DAILY_MAX_LIMIT = 100;
    private static int logid = 0;

    /**
     * 上传Log
     *
     * @param data 发送的Log数据
     */
    public static synchronized void postLog(HashMap<String, String> data, Context context, boolean saveOnFailure) {

        if (saveOnFailure) {
            logid++;
            data.put("elogid", logid + "");
        }

        incLogCount(context);


        if (!checkLimit(context)) {
            QHADLog.d("上传LOG数已超过上限，取消上传");
            return;
        }

        QHADLog.d("上传LOG...");

        if (Utils.isNetEnable()) {
            boolean result = NetsTask.postData(StaticConfig.ERROR_LOG_URL, data);
            if (!result && saveOnFailure) {
                LogFileManager.saveLog(data);
            }
        }

    }


    /**
     * @param context
     */
    private static void incLogCount(Context context) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        String key = "count" + df.format(System.currentTimeMillis());
        int count = context.getSharedPreferences(ERROR_LOG_KEY, Context.MODE_PRIVATE).getInt(key, 0);
        count++;
        context.getSharedPreferences(ERROR_LOG_KEY, Context.MODE_PRIVATE).edit().putInt(key, count).commit();
    }

    /**
     * 判断当前是否超过上传上限
     *
     * @return
     */
    private static boolean checkLimit(Context context) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        String key = "count" + df.format(System.currentTimeMillis());
        int count = context.getSharedPreferences(ERROR_LOG_KEY, Context.MODE_PRIVATE).getInt(key, -1);
        if (count < 0) {
            SharedPreferences.Editor editor = context.getSharedPreferences(ERROR_LOG_KEY, Context.MODE_PRIVATE).edit();
            editor.clear();
            editor.putInt(key, 0);
            editor.commit();
            return true;
        } else {
            return count < DAILY_MAX_LIMIT;
        }

    }

}
