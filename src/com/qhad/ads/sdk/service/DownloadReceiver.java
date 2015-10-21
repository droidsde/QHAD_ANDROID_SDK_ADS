package com.qhad.ads.sdk.service;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.io.File;

public class DownloadReceiver extends BroadcastReceiver {

    @TargetApi(9)
    @Override
    public void onReceive(Context context, Intent intent) {
        QHADLog.d("DownloadReceiver: onReceive");
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            try {
                onDownloadComplete(context, intent);
            } catch (Exception e) {
                QHADLog.e(QhAdErrorCode.COMMON_ERROR, "DownloadReceiver.onReceive error.", e);
            }
        }
    }

    private void onDownloadComplete(Context context, Intent intent) throws Exception {
        QHADLog.d("DownloadReceiver: onDownloadComplete");
        DownloadManager dm = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        String uriString = "";
        long downId = intent.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        DownloadDBManager m = new DownloadDBManager(context);
        CommonAdVO vo = m.getDownloadInfo(downId);

        if (vo != null) {
            vo.downloadEndTime = System.currentTimeMillis();
            Query query = new Query();
            query.setFilterById(downId);
            Cursor cur = dm.query(query);
            if (cur.moveToFirst()) {
                AdCounter.increment(AdCounter.ACTION_SERVICE_RECEIVE_SYSTEM_DOWNLOAD_NOTIFICATION);
                int columnIndex = cur
                        .getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_SUCCESSFUL == cur.getInt(columnIndex)) {
                    AdCounter.increment(AdCounter.ACTION_SERVICE_RECEIVED_SYSTEM_DOWNLOAD_SUCCEED);
                    uriString = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (uriString != null && uriString.startsWith("file://")) {
                        uriString = uriString.substring(7);
                    }
                } else if (DownloadManager.STATUS_FAILED == cur.getInt(columnIndex)) {
                    AdCounter.increment(AdCounter.ACTION_SERVICE_RECEIVED_SYSTEM_DOWNLOAD_FAILED);
                    String reason = cur.getInt(cur.getColumnIndex(DownloadManager.COLUMN_REASON)) + "";
                    QHADLog.e(QhAdErrorCode.CLICK_DOWNLOAD_APP_ERROR, "DM report download error:" + reason, null, vo);
                    dm.remove(downId);
                }
            }
            cur.close();

            m.deleteDownload(downId);

            if (null != uriString && !"".equals(uriString)) {
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(uriString,
                        PackageManager.GET_ACTIVITIES);
                if (info != null) {
                    QHADLog.d("DownloadManager: app download succeed, start install app");
                    AdCounter.increment(AdCounter.ACTION_SERVICE_RECEIVED_SYSTEM_DOWNLOAD_FILE_PARSED);
                    String pkg = info.packageName;
                    vo.pkg = pkg;
                    vo.installStartTime = System.currentTimeMillis();
                    AdCounter.increment(AdCounter.ACTION_SERVICE_START_REPORT_DOWNLOAD_COMPLETE);
                    QhAdModel.getInstance().getTrackManager()
                            .RegisterTrack(vo, TrackType.DOWNLOAD_APP);
                    QhAdServiceBridge serviceBridge = QhAdServiceBridge.getServiceBridge();
                    if (serviceBridge != null) {
                        AdCounter.increment(AdCounter.ACTION_SERVICE_START_INSTALL_APP);
                        serviceBridge.getSystemReceiver().regApps(vo);
                    } else {
                        QHADLog.e(QhAdErrorCode.CLICK_INSTALL_APP_ERROR, "DownloadReceiver regApps error", null, vo);
                    }
                    File file = new File(uriString);
                    Intent inst = new Intent(Intent.ACTION_VIEW);
                    inst.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");
                    inst.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        context.startActivity(inst);
                    } catch (Exception e) {
                        QHADLog.e(QhAdErrorCode.CLICK_INSTALL_APP_ERROR, "DownloadReceiver startActivity error.", e, vo);
                    }
                }
            }
        }
        m.closeDB();
    }
}
