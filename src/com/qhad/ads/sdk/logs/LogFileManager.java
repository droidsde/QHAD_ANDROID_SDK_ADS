package com.qhad.ads.sdk.logs;

import android.content.Context;

import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.utils.Utils;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by qihuajun on 2015/5/20.
 */
public class LogFileManager {

    /**
     * 获取log文件地址
     *
     * @return
     */
    private static String getLogPath() {
        String cacheDir = Utils.getCacheDir();

        File file = new File(cacheDir, StaticConfig.ERROR_LOG_FILE_NAME);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                QHADLog.e("create log file error:" + e.getMessage());
                return null;
            }
        }

        return Utils.getCacheDir() + "/" + StaticConfig.ERROR_LOG_FILE_NAME;
    }

    /**
     * 获取保存到本地的所有log
     *
     * @return
     */
    public static ArrayList<JSONObject> getAllLogs() {
        ArrayList<JSONObject> logs = new ArrayList<JSONObject>();

        try {
            String path = getLogPath();
            if (path != null) {

                FileInputStream fis = new FileInputStream(path);
                int length = fis.available();
                if (length > 0) {
                    byte[] buffer = new byte[length];
                    fis.read(buffer);
                    String content = EncodingUtils.getString(buffer, "UTF-8");
                    String[] lines = content.split("\n");
                    for (String line : lines) {
                        JSONObject o = new JSONObject(line);
                        logs.add(o);
                    }
                }
                fis.close();

            }

        } catch (Exception e) {
            String msg = e.getMessage();
        }

        return logs;
    }

    /**
     * 保存一条log到本地
     *
     * @param data 要保存的log数据
     */
    public static synchronized void saveLog(HashMap<String, String> data) {
        String path = getLogPath();

        if (path == null) {
            return;
        }


        ArrayList<JSONObject> logs = getAllLogs();

        int size = logs.size();

        if (size >= 50) {
            logs = new ArrayList<JSONObject>(logs.subList(size - 20, size));
        }

        logs.add(new JSONObject(data));

        size = logs.size();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            sb.append(logs.get(i).toString());
            if (i != size - 1) {
                sb.append("\n");
            }
        }

        try {
            FileOutputStream fos = new FileOutputStream(getLogPath());
            fos.write(sb.toString().getBytes());
            fos.close();
        } catch (Exception e) {
            String msg = e.getMessage();
            QHADLog.e(msg);
        }

    }

    /**
     * 清除本地保存的log文件
     */
    private static void clearLogs() {
        String path = getLogPath();

        if (path == null) {
            return;
        }

        File file = new File(path);
        file.delete();
    }

    /**
     * 上传本地的所有logs
     */
    public static void uploadAllLogs(Context context) {
        ArrayList<JSONObject> logs = getAllLogs();

        clearLogs();

        int size = logs.size();
        for (int i = 0; i < size; i++) {
            HashMap<String, String> data = new HashMap<>();
            JSONObject log = logs.get(i);
            Iterator<String> keys = log.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                try {
                    String value = log.getString(key);
                    data.put(key, value);
                } catch (JSONException e) {
                    QHADLog.e("Read Logs Error:" + e.getMessage());
                }
            }
            LogUploader.postLog(data, context, false);
        }
    }


}
