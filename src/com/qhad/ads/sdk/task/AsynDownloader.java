package com.qhad.ads.sdk.task;

import android.os.AsyncTask;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AsynDownloader extends AsyncTask<CommonAdVO, Integer, Boolean> {

    private int fileSize = 0;
    private int downloaded = 0;


    public AsynDownloader() {
    }

    /**
     * 执行前
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * 执行中
     */
    @Override
    protected Boolean doInBackground(CommonAdVO... params) {
        try {
            CommonAdVO vo = params[0];
            String tld = vo.tld;
            String path = vo.apkFilePath;
            File file = new File(path);
            URL url = new URL(tld);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setInstanceFollowRedirects(true);
            conn.setDoInput(true);
            QHADLog.d("下载应用:开始, PATH:" + path + "," + "URL:" + tld);
            fileSize = conn.getContentLength();
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
                    Integer[] val = {pro};
                    onProgressUpdate(val);
                }
                is.close();
                fos.close();
                conn.disconnect();
                return true;
            } else {
                QHADLog.e("下载应用:non200错误，ErrorCode=" + conn.getResponseCode());
                return false;
            }
        } catch (Exception e) {
            QHADLog.e("下载应用中:ERROR:" + e.getMessage());
        }
        return false;
    }

    /**
     * 执行后
     */
    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }

    /**
     * 执行进度
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     * 取消任务
     */
    @Override
    protected void onCancelled() {
        QHADLog.d("获取数据:Cancelled");
        super.onCancelled();
    }
}
