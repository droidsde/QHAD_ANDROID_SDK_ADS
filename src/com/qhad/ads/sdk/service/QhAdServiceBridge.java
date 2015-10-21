package com.qhad.ads.sdk.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.ActiveappMonitor;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.model.SystemReceiver;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.task.DownloadeTask;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chengsiy on 2015/5/19.
 */
public class QhAdServiceBridge extends AbstractServiceBridge {
    private static WeakReference<QhAdServiceBridge> instanceRef;
    private final String DOWNLOAD = "download";
    private final DownloadeTask downloadeTask;
    private final DownloadReceiver downloadReceiver;
    private final SystemReceiver systemReceiver;
    private final Handler uiHandler;
    private final ActiveappMonitor activeAppHandler;

    public QhAdServiceBridge(Service service) {
        super(service);
        uiHandler = new Handler();
        downloadReceiver = new DownloadReceiver();
        systemReceiver = new SystemReceiver();
        instanceRef = new WeakReference<>(this);
        downloadeTask = new DownloadeTask(uiHandler);
        activeAppHandler = new ActiveappMonitor(service.getApplicationContext());

    }

    public static QhAdServiceBridge getServiceBridge() {
        if (instanceRef == null)
            return null;
        return instanceRef.get();
    }

    public ActiveappMonitor getActiveAppHandler() {
        return activeAppHandler;
    }

    public SystemReceiver getSystemReceiver() {
        return systemReceiver;
    }

    @Override
    public void onCreate() {
        QHADLog.d("Service onCreate!");
        super.onCreate();
        try {
            AppDownloader.initHandler();
            QhAdModel.getInstance().initGlobal(getService().getApplicationContext());
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            getService().registerReceiver(downloadReceiver, intentFilter);
            intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addDataScheme("package");
            getService().registerReceiver(systemReceiver, intentFilter);
            cleanupDownloads(getService());
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.COMMON_ERROR, "Service onCreate error.", e);
        }
    }

    @Override
    public void onDestroy() {
        QHADLog.d("Service onDestroy!");
        try {
            if (AppDownloader.handler != null)
                AppDownloader.handler.removeCallbacksAndMessages(null);
            uiHandler.removeCallbacksAndMessages(null);
            getService().unregisterReceiver(downloadReceiver);
            getService().unregisterReceiver(systemReceiver);
            activeAppHandler.cleanup();
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.COMMON_ERROR, "Service onDestroy error.", e);
        }
    }

    @Override
    public void onLowMemory() {
        QHADLog.d("Service onLowMemory");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        QHADLog.d("Service onStartCommand!");
        try {
            if (intent == null) {
                QHADLog.d("null service intent.");
                return Service.START_STICKY;
            }

            String action = intent.getStringExtra("action");
            if (action != null && action.endsWith(DOWNLOAD)) {
                AdCounter.increment(AdCounter.ACTION_SERVICE_RECEIVE_DOWNLOAD);
                onStartDownload(intent);
            }
            return Service.START_STICKY;
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.COMMON_ERROR, "Service onStartCommand error.", e);
        }
        return Service.START_STICKY;
    }

    private void onStartDownload(Intent intent) {
        QHADLog.d("Service start downloading");
        final CommonAdVO vo = new CommonAdVO();
        vo.ld = intent.getStringExtra("url");
        vo.impid = intent.getStringExtra("impid");
        vo.pkg = intent.getStringExtra("pkg");
        vo.clickEventId = intent.getStringExtra("clickid");
        vo.networkTypes = intent.getIntExtra("nt", DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        vo.advertiserid = intent.getStringExtra("advertiserid");
        vo.campaignid = intent.getStringExtra("campaignid");
        vo.solutionid = intent.getStringExtra("solutionid");
        vo.bannerid = intent.getStringExtra("bannerid");
        vo.adspaceid = intent.getStringExtra("adspaceid");
        vo.softid = intent.getStringExtra("softid");
        synchronized (downloadeTask) {
            if (!downloadeTask.isDownloading()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadeTask.downloadApp(vo, getService());
                    }
                }).start();
            }
        }
    }

    private void cleanupDownloads(Context context) {
        final long EXP_SPAN = StaticConfig.DOWNLOAD_CLEANUP_DAYS * 24 * 60 * 60 * 1000;
        DownloadDBManager manager = new DownloadDBManager(context);
        Map<Long, Long> downloadIds = manager.getDownloadIds();
        if (downloadIds.size() == 0) {
            QHADLog.d("no downloads skip cleanupDownloads.");
            return;
        }
        long curDt = System.currentTimeMillis();
        List<Long> expIds = new ArrayList<>();
        for (Long id : downloadIds.keySet()) {
            Long date = downloadIds.get(id);
            if (curDt - date >= EXP_SPAN) {
                expIds.add(id);
            }
        }
        if (expIds.size() == 0)
            return;
        long[] idArr = new long[expIds.size()];
        for (int i = 0; i < expIds.size(); i++) {
            idArr[i] = expIds.get(i);
            manager.deleteDownload(i);
        }
        QHADLog.d(String.format("Cleanup downloads,found %s download tasks", idArr.length));
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.remove(idArr);

    }
}
