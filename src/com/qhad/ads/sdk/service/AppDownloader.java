package com.qhad.ads.sdk.service;

import android.os.Handler;
import android.os.Message;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.service.AppDownloader.Listener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class AppDownloader {

    public static Handler handler = null;

    public AppDownloader() {
    }

    public static void initHandler() {
        if (handler == null) {
            handler = new Handler() {
                @SuppressWarnings("unchecked")
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
                    Listener listener = (Listener) map.get("l");
                    if (msg.what == 0) {
                        listener.onDownloadAppFailed();
                    } else if (msg.what == 1) {
                        listener.onDownloadAppSucceed();
                    } else if (msg.what == 2) {
                        Integer p = (Integer) map.get("p");
                        listener.onDownloadAppProcess(p);
                    }
                }
            };
        }
    }

    public static void downloadApp(String url, String filePath, Listener listener) {
        HttpRunable runable = new HttpRunable(url, filePath, handler, listener);
        Thread thread = new Thread(runable);
        thread.start();
    }

    public interface Listener {
        void onDownloadAppSucceed();

        void onDownloadAppProcess(int p);

        void onDownloadAppFailed();
    }
}

class HttpRunable implements Runnable {

    private String url = null;
    private Handler handler = null;
    private Listener listener = null;
    private String filePath = null;
    private int fileSize = 0;
    private int downloaded = 0;

    public HttpRunable(String url, String filePath, Handler handler, Listener listener) {
        this.url = url;
        this.handler = handler;
        this.listener = listener;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        HashMap<String, Object> obj = new HashMap<String, Object>();
        obj.put("l", listener);
        try {
            String tld = url;
            String path = filePath;
            File file = new File(path);
            URL url = new URL(tld);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000 * 30);
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            conn.setDoInput(true);
            fileSize = conn.getContentLength();
            QHADLog.d("下载应用:开始, PATH:" + path + "," + "URL:" + tld);
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    downloaded += len;
                    float tt = ((float) downloaded / (float) fileSize) * 100;
                    Integer pro = Math.round(tt);
                    Message msg = Message.obtain();
                    msg.what = 2;
                    obj.put("p", pro);
                    msg.obj = obj;
                    handler.dispatchMessage(msg);
                }
                is.close();
                fos.close();
                conn.disconnect();
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = obj;
                handler.dispatchMessage(msg);
            } else {
                Message msg = Message.obtain();
                msg.what = 0;
                msg.obj = obj;
                handler.dispatchMessage(msg);
                QHADLog.e("下载应用中:错误，ErrorCode=" + conn.getResponseCode());
            }
        } catch (Exception e) {
            Message msg = Message.obtain();
            msg.what = 0;
            msg.obj = obj;
            handler.dispatchMessage(msg);
            QHADLog.e("下载应用中:ERROR:" + e.getMessage());
        }
    }
}
