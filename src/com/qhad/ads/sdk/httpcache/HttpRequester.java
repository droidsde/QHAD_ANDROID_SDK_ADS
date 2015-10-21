package com.qhad.ads.sdk.httpcache;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.qhad.ads.sdk.httpcache.HttpRequester.Listener;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class HttpRequester {

    public static Boolean isOpenLog = true;
    private static Handler handler = null;

    public static void getAsynData(Context context, String url, Boolean isUsecache, Listener listener) {
        if (handler == null) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    handler.post(new ResultRunable(msg));
                }
            };
        }

        HttpRunable runable = new HttpRunable(url, handler, listener, context, isUsecache);
        Thread thread = new Thread(runable);
        thread.start();
    }

    public static byte[] getSyncData(Context context, String urlString, Boolean isUsecache) {
        byte[] data = null;
        final int NET_TIMEOUT = 1000;

        HttpCacher httpCacher = null;
        byte[] cache = null;
        try {
            httpCacher = HttpCacher.get(context);
            cache = httpCacher.getAsBinary(urlString);
        } catch (Exception e) {
            QHADLog.e("Cache error" + e.getMessage());
        }

        if (cache != null && isUsecache) {
            if (isOpenLog) QHADLog.d("同步:缓存命中");
            data = cache;
        } else {
            if (isUsecache) {
                if (isOpenLog) QHADLog.d("同步:缓存未命中");
            } else {
                if (isOpenLog) QHADLog.d("同步:不使用缓存");
            }
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(NET_TIMEOUT);
                conn.setUseCaches(false);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    byte[] b = getBytes(is);
                    bis.close();
                    is.close();
                    conn.disconnect();

                    data = b;
                    if (isUsecache && httpCacher != null) {
                        httpCacher.put(urlString, b, HttpCacher.TIME_DAY);
                    }
                } else {
                    conn.disconnect();
                }
            } catch (Exception e) {
                QHADLog.e(QhAdErrorCode.COMMON_ERROR, "HttpRequester getSyncData Error,url: " + urlString, e);
            }
        }
        return data;
    }

    private static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = is.read(b, 0, 1024)) != -1) {
            baos.write(b, 0, len);
            baos.flush();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

    public interface Listener {
        void onGetDataSucceed(byte[] data);

        void onGetDataFailed(String error);
    }
}

class HttpRunable implements Runnable {
    private final int NET_TIMEOUT = 1000;
    private String urlString = null;
    private Handler handler = null;
    private Listener listener = null;
    private Context context = null;
    private Boolean isUsecache = true;

    public HttpRunable(String url, Handler handler, Listener listener, Context context, Boolean isUsecache) {
        this.urlString = url;
        this.handler = handler;
        this.listener = listener;
        this.context = context;
        this.isUsecache = isUsecache;
    }

    @Override
    public void run() {
        Message msg = new Message();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("callback", listener);

        HttpCacher httpCacher = null;
        byte[] cache = null;
        try {
            httpCacher = HttpCacher.get(context);
            cache = httpCacher.getAsBinary(urlString);
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.COMMON_ERROR, "HttpRunable Cache Error ", e);
        }

        if (cache != null && isUsecache) {
            if (HttpRequester.isOpenLog) QHADLog.d("异步:缓存命中");
            msg.what = 0;
            data.put("data", cache);
            msg.obj = data;
        } else {
            if (isUsecache) {
                if (HttpRequester.isOpenLog) QHADLog.d("异步:缓存未命中");
            } else {
                if (HttpRequester.isOpenLog) QHADLog.d("异步:不使用缓存");
            }
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(NET_TIMEOUT);
                conn.setUseCaches(false);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    byte[] b = getBytes(is);
                    bis.close();
                    is.close();
                    conn.disconnect();

                    msg.what = 0;
                    data.put("data", b);
                    msg.obj = data;
                    if (isUsecache && httpCacher != null) {
                        httpCacher.put(urlString, b, HttpCacher.TIME_DAY);
                    }
                } else {
                    msg.what = 1;
                    data.put("error", String.valueOf(conn.getResponseCode()));
                    msg.obj = data;
                    conn.disconnect();
                }
            } catch (Exception e) {
                msg.what = 2;
                data.put("error", e.getMessage());
                msg.obj = data;

                QHADLog.e(QhAdErrorCode.COMMON_ERROR, "HttpRunable Run Error,url: " + urlString, e);
            }
        }
        handler.dispatchMessage(msg);
    }

    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = is.read(b, 0, 1024)) != -1) {
            baos.write(b, 0, len);
            baos.flush();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}

class ResultRunable implements Runnable {
    private static final String UNKOWN_ERROR = "unkown error";
    private Message msg = null;

    public ResultRunable(Message msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        try {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> data = (HashMap<String, Object>) msg.obj;
            Listener listener = (Listener) data.get("callback");
            if (msg.what == 0) {
                listener.onGetDataSucceed((byte[]) data.get("data"));
            } else if (msg.what == 1) {
                listener.onGetDataFailed((String) data.get("error"));
            } else if (msg.what == 2) {
                listener.onGetDataFailed((String) data.get("error"));
            } else {
                listener.onGetDataFailed(UNKOWN_ERROR);
            }
        } catch (Exception e) {
            @SuppressWarnings("unchecked")
            HashMap<String, Object> data = (HashMap<String, Object>) msg.obj;
            Listener listener = (Listener) data.get("callback");
            listener.onGetDataFailed("HttpRequester get data error:" + e.getMessage());
        }
    }
}
