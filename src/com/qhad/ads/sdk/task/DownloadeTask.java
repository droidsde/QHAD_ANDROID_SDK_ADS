package com.qhad.ads.sdk.task;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.service.AppDownloader;
import com.qhad.ads.sdk.service.DownloadDBManager;
import com.qhad.ads.sdk.service.QhAdServiceBridge;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.utils.LocalFileManager;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.io.File;
import java.net.URL;
import java.util.List;

public class DownloadeTask {

    private final Handler uiHandler;
    private Boolean isDownloading = false;
    private DownloadeTask sampleDownloader = null;
    private NotificationManager notificationManager = null;
    private NotificationCompat.Builder builder = null;
    private Integer counter = 0;
    private Context context = null;
    private CommonAdVO vo = null;
    private File mfile = null;
    private String fileName = "";
    private int notiId = 0;

    public DownloadeTask(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }


    public Boolean isDownloading() {
        return isDownloading;
    }

    private boolean openInstalledApp() {
        QHADLog.d("Check app installed");
        Intent jumpiIntent = new Intent(Intent.ACTION_MAIN, null);
        jumpiIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = context.getPackageManager()
                .queryIntentActivities(jumpiIntent,
                        PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            String pa = list.get(i).activityInfo.applicationInfo.packageName;
            if (vo.pkg != null) {
                if (pa.equals(vo.pkg)) {
                    QHADLog.d("App installed, open it directly");
                    AdCounter.increment(AdCounter.ACTION_SERVICE_OPEN_APP);
                    Intent jump = new Intent(Intent.ACTION_MAIN);
                    jump.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    jump.addCategory(Intent.CATEGORY_LAUNCHER);
                    jump.setPackage(vo.pkg);
                    jump.setComponent(new ComponentName(
                            list.get(i).activityInfo.packageName, list
                            .get(i).activityInfo.name));
                    context.startActivity(jump);
                    QhAdModel.getInstance().getTrackManager()
                            .RegisterTrack(vo, TrackType.OPEN_APP);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDownloaded() throws Exception {
        QHADLog.d("下载应用:验证文件是否存在");
        boolean isDownload = false;
        URL url = new URL(vo.ld);
        String path = url.getPath();
        String[] tmp = path.split("/");
        fileName = tmp[tmp.length - 1];
        File file = new File(LocalFileManager.getSDPath() + "/Download/" + fileName);
        file.mkdirs();
        if (file.exists()) {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(file.getPath(),
                    PackageManager.GET_ACTIVITIES);
            if (info != null) {
                String packageName = info.applicationInfo.packageName;
                if (vo.pkg != null) {
                    if (packageName.equals(vo.pkg)) {
                        isDownload = true;
                    }
                }
            } else {
                LocalFileManager.deleteFile(file.getPath());
            }
        }

        mfile = file;
        return isDownload;
    }

    private void startInstallApp() {
        QHADLog.d("Start installing app");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(mfile),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(mfile.getPath(),
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            vo.pkg = info.packageName;
            vo.installStartTime = System.currentTimeMillis();
            QhAdServiceBridge.getServiceBridge().getSystemReceiver().regApps(vo);
            AdCounter.increment(AdCounter.ACTION_SERVICE_START_INSTALL_APP);
        }
    }

    public synchronized Boolean downloadApp(CommonAdVO _vo, Context _context) {
        QHADLog.d("DownloadTask start downloading app");
        AdCounter.increment(AdCounter.ACTION_SERVICE_START_DOWNLOAD);
        try {
            vo = _vo;
            context = _context;
            buildNotification(context);
            sampleDownloader = this;
            try {
                if (openInstalledApp())
                    return true;
            } catch (Exception e) {
                QHADLog.e(QhAdErrorCode.CLICK_OPEN_APP_ERROR, "openInstalledApp error.", e, vo);
                return false;
            }
            boolean isDownload = isDownloaded();
            if (isDownload) {
                AdCounter.increment(AdCounter.ACTION_SERVICE_APP_FILE_DOWNLOADED);
                try {
                    startInstallApp();
                } catch (Exception e) {
                    QHADLog.e(QhAdErrorCode.CLICK_INSTALL_APP_ERROR, "startInstallApp error.", e, vo);
                    return false;
                }
            } else {
                vo.apkFilePath = mfile.getPath();
                QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.DOWNLOAD_APP_START);
                if (Build.VERSION.SDK_INT > 8) {
                    vo.apkFileName = fileName;
                    useDownloadManager();
                } else {
                    useAppDownloader();
                }
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showStartDownloadToast();
                    }
                });
            }
            return true;
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.CLICK_DOWNLOAD_APP_ERROR, "下载应用:Error.", e, vo);
        }
        return false;
    }

    private void showStartDownloadToast() {
        Toast toast = Toast.makeText(context, "应用开始下载",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void useAppDownloader() {
        QHADLog.d("Download app with AppDownloader");
        AdCounter.increment(AdCounter.ACTION_SERVICE_START_USE_APPDOWNLOAD);
        sampleDownloader.isDownloading = true;
        AppDownloader.downloadApp(vo.ld, vo.apkFilePath, new AppDownloader.Listener() {
            @Override
            public void onDownloadAppSucceed() {
                QHADLog.d("AppDownloader : app download succeed");
                AdCounter.increment(AdCounter.ACTION_SERVICE_START_USE_APPDOWNLOAD_DONE);
                updateProgressbar(100);
                downloadComplete();
                notificationManager.cancel(notiId);
                sampleDownloader.isDownloading = false;
            }

            @Override
            public void onDownloadAppProcess(int p) {
                counter++;
                if (counter % 400 == 0) {
                    updateProgressbar(p);
                }
            }

            @Override
            public void onDownloadAppFailed() {
                notificationManager.cancel(notiId);
                sampleDownloader.isDownloading = false;
                if (mfile.exists()) {
                    LocalFileManager.deleteFile(mfile.getPath());
                }
                AdCounter.increment(AdCounter.ACTION_SERVICE_START_USE_APPDOWNLOAD_FAILED);
                QHADLog.e(QhAdErrorCode.CLICK_DOWNLOAD_APP_ERROR, "AppDownloader : onDownloadAppFailed.", null, vo);
            }
        });
        notiId = (int) System.currentTimeMillis();
        builder.setContentTitle("等待下载");
        notificationManager.notify(notiId, builder.build());
    }

    private void buildNotification(Context context) throws Exception {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            builder = new NotificationCompat.Builder(context);
            builder.setTicker("应用下载");
            builder.setWhen(System.currentTimeMillis());
            builder.setContentIntent(getDefalutIntent(
                    PendingIntent.FLAG_UPDATE_CURRENT, context,
                    (int) System.currentTimeMillis() + 10));
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
            builder.setContentTitle("下载进度");
            builder.setProgress(100, 0, false);
        }
    }

    private void downloadComplete() {
        QHADLog.d("AppDownloader: downloadComplete, start installing");
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(mfile.getPath(),
                PackageManager.GET_ACTIVITIES);
        if (info != null) {
            vo.pkg = info.packageName;
            vo.installStartTime = System.currentTimeMillis();
            AdCounter.increment(AdCounter.ACTION_SERVICE_START_REPORT_DOWNLOAD_COMPLETE);
            QhAdModel.getInstance().getTrackManager()
                    .RegisterTrack(vo, TrackType.DOWNLOAD_APP);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(mfile),
                    "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            try {
                AdCounter.increment(AdCounter.ACTION_SERVICE_START_INSTALL_APP);
                QhAdServiceBridge.getServiceBridge().getSystemReceiver().regApps(vo);
            } catch (Exception e) {
                QHADLog.e(QhAdErrorCode.CLICK_INSTALL_APP_ERROR, "regApps error.", e, vo);
            }
        }
    }

    @TargetApi(9)
    private void useDownloadManager() {
        QHADLog.d("Download app with DownloadManager");
        AdCounter.increment(AdCounter.ACTION_SERVICE_START_SYSTEM_DOWNLOAD);
        DownloadDBManager m = new DownloadDBManager(context);
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);

        long id = m.getDownloadId(vo.pkg);
        vo.downloadStartTime = System.currentTimeMillis();

        if (id == 0) {
            AdCounter.increment(AdCounter.ACTION_SERVICE_CREATE_NEW_SYSTEM_DOWNLOAD);
            try {
                id = startSystemDownload();
                m.createDownload(vo, id);
                AdCounter.increment(AdCounter.ACTION_SERVICE_CREATE_NEW_SYSTEM_DOWNLOAD_DONE);
            } catch (Exception e) {
                QHADLog.e(QhAdErrorCode.CLICK_DOWNLOAD_APP_COMPACT, "DM error.use app downloader.", e, vo);
                useAppDownloader();
            }
        } else {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            Cursor c = downloadManager.query(query);
            boolean recreat = false;
            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                        QHADLog.d("STATUS_PAUSED");
                        break;
                    case DownloadManager.STATUS_PENDING:
                        QHADLog.d("STATUS_PENDING");
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        QHADLog.d("STATUS_RUNNING");
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        QHADLog.d("下载完成");
                        recreat = true;
                        downloadManager.remove(id);
                        break;
                    case DownloadManager.STATUS_FAILED:
                        QHADLog.d("下载失败");
                        recreat = true;
                        downloadManager.remove(id);
                        break;
                }
            } else {
                recreat = true;
            }

            c.close();

            if (recreat) {
                try {
                    m.deleteDownload(id);
                    AdCounter.increment(AdCounter.ACTION_SERVICE_CREATE_NEW_SYSTEM_DOWNLOAD);
                    id = startSystemDownload();
                    m.createDownload(vo, id);
                    AdCounter.increment(AdCounter.ACTION_SERVICE_CREATE_NEW_SYSTEM_DOWNLOAD_DONE);
                } catch (Exception e) {
                    QHADLog.e(QhAdErrorCode.CLICK_DOWNLOAD_APP_COMPACT, "DM error.use app downloader.", e, vo);
                    useAppDownloader();
                }
            } else {
                AdCounter.increment(AdCounter.ACTION_SERVICE_FOUND_EXISTED_SYSTEM_DOWNLOAD);
            }
        }

        m.closeDB();
    }

    private long startSystemDownload() {
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);

        Uri resource = Uri.parse(vo.ld);
        Request request = new Request(
                resource);
        request.setAllowedNetworkTypes(vo.networkTypes);
        request.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap
                .getMimeTypeFromExtension(MimeTypeMap
                        .getFileExtensionFromUrl(vo.ld));
        request.setMimeType(mimeString);
        request.setVisibleInDownloadsUi(true);
        if (Build.VERSION.SDK_INT >= 11) {
            request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir("/Download/",
                vo.apkFileName);
        request.setTitle("应用下载中");
        long id = downloadManager.enqueue(request);
        return id;
    }


    private void updateProgressbar(int pro) {
        builder.setProgress(100, pro, false);
        builder.setContentTitle("下载进度:");
        notificationManager.notify(notiId, builder.build());
    }

    private PendingIntent getDefalutIntent(int flags, Context context, int id) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id,
                new Intent(), flags);
        return pendingIntent;
    }
}
