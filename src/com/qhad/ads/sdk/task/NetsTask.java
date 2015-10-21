package com.qhad.ads.sdk.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.qhad.ads.sdk.httpcache.HttpRequester;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.res.StaticConfig;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Duan
 */
public class NetsTask {

    /**
     * 获取广告数据
     *
     * @param url 广告引擎URL
     * @return 数据String
     */
    public static String getAdData(String _url) {

        try {
            QHADLog.d("-------------------获取数据-------------------");
            QHADLog.d("获取数据:开始");
            URL url = new URL(_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(StaticConfig.NET_TIMEOUT);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] b = getBytes(is);
                String result = new String(b, "utf8");
                bis.close();
                is.close();
                QHADLog.d("获取数据:完成");
                conn.disconnect();
                return result;
            } else {
                if (conn.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                    QHADLog.e(QhAdErrorCode.COMMON_ERROR, "AdRequest Invalid HttpResponseCode:" + conn.getResponseCode() + ",url:" + url);
                }
                conn.disconnect();
            }
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.COMMON_ERROR, "AdRequest Exception Catched," + "url:" + _url, e);
        }
        return null;
    }

    /**
     * 获取缓存图片Uri
     *
     * @param path 图片url
     * @param dir  图片资源目录
     * @return Bitmap
     */
    public static Bitmap getAdresource(String path, Context context) {
        QHADLog.d("-------------------获取资源-------------------");
        Bitmap bitmap = null;

        try {
            QHADLog.d("获取资源下载:开始");
            byte[] b = HttpRequester.getSyncData(context, path, true);
            if (b != null && b.length != 0) {
                InputStream input = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inSampleSize = NetsTask.computeSampleSize(options, -1, 480 * 800);
                input = new ByteArrayInputStream(b);
                SoftReference<Bitmap> softRef = new SoftReference<Bitmap>(BitmapFactory.decodeStream(input, null, options));
                bitmap = softRef.get();
                if (bitmap == null) {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.IMAGE_LOAD_ERROR, "Image Load Error:" + path, e);
        } catch (OutOfMemoryError e) {
            QHADLog.e(QhAdErrorCode.IMAGE_LOAD_ERROR, "Image Load OutOfMemoryError:" + path, e);
        }
        return bitmap;
    }

    /**
     * POST服务
     *
     * @param url     地址
     * @param param   参数
     * @param handler 处理器
     */
    public static void postData(String url, HashMap<String, String> param, Handler handler, int type) {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (param != null) {
            Set<Entry<String, String>> set = param.entrySet();
            Iterator<Entry<String, String>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Entry<String, String> tempEntry = iterator.next();
                params.add(new BasicNameValuePair(tempEntry.getKey(), tempEntry.getValue()));
            }
        } else {
            return;
        }
        HttpResponse httpResponse = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpResponse.getEntity());
                Message msg = new Message();
                msg.what = type;
                msg.obj = result;
                handler.dispatchMessage(msg);
            } else {
                QHADLog.d("POST异常:Code=" + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (ClientProtocolException e) {
            QHADLog.e("POST异常:" + e.getMessage());
        } catch (IOException e) {
            QHADLog.e("POST异常:" + e.getMessage());
        }
    }


    /**
     * POST服务
     *
     * @param url     地址
     * @param param   参数
     * @param handler 处理器
     */
    public static boolean postData(String url, HashMap<String, String> param) {
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (param != null) {
            Set<Entry<String, String>> set = param.entrySet();
            Iterator<Entry<String, String>> iterator = set.iterator();
            while (iterator.hasNext()) {
                Entry<String, String> tempEntry = iterator.next();
                params.add(new BasicNameValuePair(tempEntry.getKey(), tempEntry.getValue()));
            }
        }

        HttpResponse httpResponse = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
            httpResponse = httpClient.execute(httpPost);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return true;
            } else {
                QHADLog.d("POST异常:Code=" + httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            QHADLog.d("POST异常:" + e.getMessage());
        }

        return false;
    }

    /**
     * 将InputStream对象转换为Byte[]
     *
     * @param is
     * @return
     * @throws IOException
     */
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

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
