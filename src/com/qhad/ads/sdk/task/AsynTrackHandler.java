package com.qhad.ads.sdk.task;

import android.os.AsyncTask;

import com.qhad.ads.sdk.logs.QHADLog;

/**
 * @author Duan
 */
public abstract class AsynTrackHandler extends AsyncTask<String, Integer, Boolean> {

    public AsynTrackHandler() {
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
    protected Boolean doInBackground(String... params) {
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

