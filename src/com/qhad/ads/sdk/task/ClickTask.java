package com.qhad.ads.sdk.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.qhad.ads.sdk.adcore.QhAdActivity;
import com.qhad.ads.sdk.adsinterfaces.IQhAdEventListener;
import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageListener;
import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageView;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdOnClickListener;
import com.qhad.ads.sdk.core.AD_TYPE;
import com.qhad.ads.sdk.core.DownloadConfirmLayout;
import com.qhad.ads.sdk.core.LandingPageActivityBridge;
import com.qhad.ads.sdk.core.QhAdView;
import com.qhad.ads.sdk.core.QhSplashAd;
import com.qhad.ads.sdk.httpcache.HttpRequester;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.SwitchConfig;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.utils.LocalFileManager;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.io.File;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class ClickTask {
    private final IQhLandingPageView landingPageView;
    private CommonAdVO vo = null;
    private Context context = null;
    private IQhAdEventListener adEventListener = null;
    private QhAdView adView = null;
    private Handler handler;
    private IQhVideoAdOnClickListener videoAdOnClickListener;
    private AlertDialog currentAlertDialog;
    private boolean isDialoging = false;

    public ClickTask(CommonAdVO vo, IQhAdEventListener adEventListener, QhAdView adView, Context context) {
        this.vo = vo;
        this.adEventListener = adEventListener;
        this.context = context;
        this.adView = adView;
        landingPageView = getLandingPageView();
    }

    public ClickTask(CommonAdVO vo, IQhVideoAdOnClickListener videoAdOnClickListener, Context context) {
        this.vo = vo;
        this.videoAdOnClickListener = videoAdOnClickListener;
        this.context = context;
        landingPageView = getLandingPageView();
    }

    public static void postHotsopt(String ld_str, String[] xy, String impid, String[] size, Context context) {
        QHADLog.d("Report hotspot");
        try {
            String cus = "";
            String pub = "";
            String wh = size[0] + "x" + size[1];
            String[] params = ld_str.split("^?", 2)[1].split("&");
            for (String p : params) {
                String[] tmp = p.split("=");
                if (tmp.length == 2) {
                    String name = tmp[0];
                    String value = tmp[1];
                    if (name.endsWith("cus")) {
                        cus = value;
                    }

                    if (name.endsWith("pub")) {
                        pub = value;
                    }

                    if (!cus.endsWith("") && !pub.endsWith("")) {
                        break;
                    }
                }
            }

            String url = "http://view.mediav.com/v?type=12&db=mediav&dimpid=" + impid + "&impid=" + impid +
                    "&pub=" + pub + "&cus=" + cus + "&wh=" + wh + "&x=" + xy[0] + "&y=" + xy[1] +
                    "&imei=" + URLEncoder.encode(Utils.getIMEI(), "utf-8") + "&ct=" + System.currentTimeMillis();

            HttpRequester.getAsynData(context, url, false, new HttpRequester.Listener() {
                @Override
                public void onGetDataSucceed(byte[] data) {
                    QHADLog.d("Report hotspot done");
                }

                @Override
                public void onGetDataFailed(String error) {
                    QHADLog.e(QhAdErrorCode.REPORT_HOTSPOT_ERROR, "Report Hotspot Error:" + error);
                }
            });
        } catch (Exception e) {
            String co = "";
            if (xy != null) {
                co = Arrays.toString(xy);
            }
            QHADLog.e(QhAdErrorCode.REPORT_HOTSPOT_ERROR, "Report Hotspot Error:" + ld_str + " xy:" + co, e);
        }
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || Utils.isEmpty(str);
    }

    private IQhLandingPageView getLandingPageView() {
        if (SwitchConfig.AllowUserCustomLandingPageView && QhAdModel.getInstance().getUserLandingPage() != null)
            return QhAdModel.getInstance().getUserLandingPage();
        else
            return new DefaultQhLandingPageView();
    }

    public void onClick(String[] xy, String[] size) {
        QHADLog.d("Ad clicked");
        if (adEventListener != null) {
            adEventListener.onAdviewClicked();
        }

        if (xy != null && size != null) {
            ClickTask.postHotsopt(vo.ld, xy, vo.impid, size, context);
        }


        if (vo != null) {

            vo.tld = vo.ld;

            if (tryOpenDeepLink())
                return;

            switch (vo.ld_type) {
                case DOWNLAND:
                    if (!isDialoging) {
                        isDialoging = true;
                        startDownload();
                    }
                    break;

                case PAGE:
                    innerBrowser();
                    break;

                case SYS_BROWSER:
                    systemBrowser();
                    break;

                case UNKOWN:
                    break;
            }
        }
    }

    private boolean tryOpenDeepLink() {
        QHADLog.d("try open deep link");
        if (vo.deepLink == null || vo.deepLink.equals("")) {
            return false;
        }
        try {
            AdCounter.increment(AdCounter.ACTION_DEEP_LINK_OPEN_START);
            Intent intent = Intent.parseUri(vo.deepLink, Intent.URI_INTENT_SCHEME);
            boolean canProcess = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.GET_ACTIVITIES).size() > 0;
            if (!canProcess)
                return false;
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setComponent(null);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID,
                    context.getPackageName());
            context.startActivity(intent);

            QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.CLICK_AD);
            AdCounter.increment(AdCounter.ACTION_DEEP_LINK_OPEN_SUCCESS);
            QHADLog.d("deep link open");
            return true;
        } catch (Throwable e) {
            AdCounter.increment(AdCounter.ACTION_DEEP_LINK_OPEN_FAILED);
            QHADLog.e(QhAdErrorCode.CLICK_DEEP_LINK_ERROR, "deep link open error.", e, vo);
            return false;
        }
    }

    @SuppressLint("HandlerLeak")
    public void startDownload() {
        QHADLog.d("start download");
        AdCounter.increment(AdCounter.ACTION_SDK_ON_CLICK_DOWNLOAD);

        if (Build.VERSION.SDK_INT < 9) {
            Toast.makeText(context, "当前手机系统版本低，无法下载。建议升级到最新版本，谢谢！", Toast.LENGTH_SHORT).show();
            isDialoging = false;
            return;
        }
        String type = Utils.getCurrentNetWorkInfo();
        String message;
        String yes;
        String no;
        final int networkTypes;
        if (null == type || "".equals(type)) {
            Toast.makeText(context, "网络连接错误，请检查网络设置！", Toast.LENGTH_SHORT).show();
            isDialoging = false;
            return;
        }
        if ("0".equals(type)) {
            message = "您处于WiFi环境下，可以放心下载哦！";
            yes = "放心下载";
            no = "容我想想";
            networkTypes = DownloadManager.Request.NETWORK_WIFI;
        } else {
            message = "您处于2G/3G/4G环境，可能会产生流量费用，是否确定下载？";
            yes = "确定下载";
            no = "容我想想";
            networkTypes = DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE;
        }

        try {
            if (isInstalled() || isDownloaded()) {
                QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.CLICK_AD);
                if (handler == null) {
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                download(networkTypes);

                                if (adView instanceof QhSplashAd) {
                                    if (adEventListener != null)
                                        adEventListener.onAdviewClosed();
                                    ((QhSplashAd) adView).overAds();
                                }

                                isDialoging = false;
                            }
                        }
                    };
                }
                new Thread(new ClickTracker()).start();

                return;
            }
        } catch (Throwable e) {
            QHADLog.e(QhAdErrorCode.CLICK_DOWNLOAD_APP_ERROR, "判断应用是否下载:Error.", e, vo);
        }
        final View.OnClickListener okListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdCounter.increment(AdCounter.ACTION_CONFIRM_DOWNLOAD);

                if (videoAdOnClickListener != null) {
                    videoAdOnClickListener.onDownloadConfirmed();
                }

                QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.CLICK_AD);
                if (handler == null) {
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                download(networkTypes);
                                if (adView instanceof QhSplashAd) {
                                    if (adEventListener != null)
                                        adEventListener.onAdviewClosed();
                                    ((QhSplashAd) adView).overAds();
                                }
                            }
                        }
                    };
                }
                new Thread(new ClickTracker()).start();
                if (currentAlertDialog != null)
                    currentAlertDialog.dismiss();
                isDialoging = false;
            }
        };
        final View.OnClickListener cancelListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoAdOnClickListener != null) {
                    videoAdOnClickListener.onDownloadCancelled();
                }

                AdCounter.increment(AdCounter.ACTION_CANCEL_DOWNLOAD);

                if (currentAlertDialog != null)
                    currentAlertDialog.dismiss();

                if (adView instanceof QhSplashAd) {
                    if (adEventListener != null)
                        adEventListener.onAdviewClosed();
                    ((QhSplashAd) adView).overAds();
                }
                isDialoging = false;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (videoAdOnClickListener != null) {
                            videoAdOnClickListener.onDownloadCancelled();
                        }
                        AdCounter.increment(AdCounter.ACTION_CANCEL_DOWNLOAD);

                        if (adView instanceof QhSplashAd) {
                            ((QhSplashAd) adView).isPause = false;
                        }
                        isDialoging = false;
                    }
                });
        DownloadConfirmLayout confirmLayout = null;
        if (vo.adType == AD_TYPE.NATIVE && (vo.appName != null && Utils.isNotEmpty(vo.appName))) {
            try {
                confirmLayout = new DownloadConfirmLayout(context, vo.adm, message, vo.appName, yes, okListener, no, cancelListener);
            } catch (Throwable e) {
                QHADLog.e(QhAdErrorCode.COMMON_ERROR, "init DownloadConfirmLayout error,use default layout.", e, vo);
            }
        }
        if (confirmLayout != null)
            builder.setView(confirmLayout);
        else {
            builder.setTitle("下载提醒");
            builder.setMessage(message);
            builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    okListener.onClick(null);
                }
            });
            builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancelListener.onClick(null);
                }
            });


        }
        currentAlertDialog = builder.show();

        if (adView instanceof QhSplashAd) {
            ((QhSplashAd) adView).isPause = true;
        }
    }

    public boolean isDownloaded() throws Exception {
        QHADLog.d("下载应用:验证文件是否存在");
        boolean isDownload = false;
        Uri uri = Uri.parse(vo.ld);
        String appurl = uri.getQueryParameter("url");
        URL url = new URL(appurl);
        String path = url.getPath();
        String[] tmp = path.split("/");
        String fileName = tmp[tmp.length - 1];
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

        return isDownload;
    }

    public boolean isInstalled() {
        QHADLog.d("下载应用:验证应用是否已安装");
        if (vo.pkg != null) {
            Intent existIntent = new Intent(Intent.ACTION_MAIN, null);
            existIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> list = context.getPackageManager()
                    .queryIntentActivities(existIntent,
                            PackageManager.GET_ACTIVITIES);
            for (int i = 0; i < list.size(); i++) {
                String pname = list.get(i).activityInfo.applicationInfo.packageName;
                if (pname.equals(vo.pkg))
                    return true;
            }
        }
        return false;
    }

    public void download(int networkTypes) {
        QHADLog.d("Start service to download app");
        Intent intent = new Intent(context, QhAdModel.getInstance().getQhServiceCls());
        intent.putExtra("url", vo.tld);
        intent.putExtra("advertiserid", vo.advertiserid);
        intent.putExtra("campaignid", vo.campaignid);
        intent.putExtra("solutionid", vo.solutionid);
        intent.putExtra("bannerid", vo.bannerid);
        intent.putExtra("pkg", vo.pkg);
        intent.putExtra("impid", vo.impid);
        intent.putExtra("clickid", vo.clickEventId);
        intent.putExtra("action", "download");
        intent.putExtra("nt", networkTypes);
        intent.putExtra("adspaceid", vo.adspaceid);
        intent.putExtra("softid", vo.softid);
        try {
            context.startService(intent);
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.CLICK_DOWNLOAD_APP_ERROR, "startService error.", e, vo);
        }
    }

    public void innerBrowser() {
        QHADLog.d("Open landingpage in inner browser");
        AdCounter.increment(AdCounter.ACTION_SDK_ON_CLICK_INNERBROWSER);
        if (vo.deepLink == null || TextUtils.isEmpty(vo.deepLink)) {
            QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.CLICK_AD);
        }
        if (QhAdModel.getInstance().getUserLandingPage() != null && !SwitchConfig.AllowUserCustomLandingPageView)
            QhAdModel.getInstance().getUserLandingPage().open(context, null, null);//通知开发者自定义落地页被云控禁用
        landingPageView.open(context, vo.ld, new QhLandingPageListenerImp(this));

        if (adView instanceof QhSplashAd) {
            ((QhSplashAd) adView).overAds();
        }
    }

    public void systemBrowser() {
        QHADLog.d("Open landingpage in system browser");
        AdCounter.increment(AdCounter.ACTION_SDK_ON_CLICK_SYSTEMBROWSER);
        if (vo.deepLink == null || TextUtils.isEmpty(vo.deepLink)) {
            QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.CLICK_AD);
        }

        String tld = "";
        if (vo.tld != null) {
            tld = vo.tld;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(tld);
        intent.setData(content_url);

        try {

            context.startActivity(intent);

            if (adEventListener != null) {
                adEventListener.onAdviewIntoLandpage();
            }
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.CLICK_SYSTEMBROSWER_ERROR, "Open System Browser Failed: " + tld, e, vo);
        }


    }

    private static class QhLandingPageListenerImp implements IQhLandingPageListener {

        private String advertiserid = "";
        private String campaignid = "";
        private String solutionid = "";
        private String bannerid = "";
        private String impid = "";
        private WeakReference<ClickTask> clickTaskWeakReference;
        private boolean isOnPageLoadFinishedCalled;
        private boolean isOnPageLoadFailedCalled;

        public QhLandingPageListenerImp(ClickTask clickTask) {
            clickTaskWeakReference = new WeakReference<>(clickTask);
        }

        @Override
        public boolean onAppDownload(String url) {
            QHADLog.d("Innerbrowser request download.");
            ClickTask clickTask = clickTaskWeakReference.get();
            if (clickTask == null) {
                QHADLog.e(QhAdErrorCode.COMMON_ERROR, "LandingPageListenerImp,click task disposed.");
                return false;
            }
            advertiserid = clickTask.vo.advertiserid;
            campaignid = clickTask.vo.campaignid;
            solutionid = clickTask.vo.solutionid;
            bannerid = clickTask.vo.bannerid;
            impid = clickTask.vo.impid;
            String ntType = Utils.getCurrentNetWorkInfo();
            if (ntType == null || !Utils.isNotEmpty(ntType)) {
                Toast.makeText(clickTask.context, "下载失败，当前无网络连接。", Toast.LENGTH_SHORT);
                return false;
            }

            Uri uri = Uri.parse(url);
            String clickEventId = uri.getQueryParameter("_mvosr");
            String pkg = uri.getQueryParameter("pkg");
            String downloadApkUrl = "";

            if (url.contains("?")) {
                downloadApkUrl = url.substring(0, url.indexOf('?'));
            }
            if (url.endsWith(".apk")) {
                downloadApkUrl = url;
            }

            if (isNullOrEmpty(downloadApkUrl)) {
                return false;
            }

            if (downloadApkUrl.endsWith(".apk")) {
                String parsedUrl = url;
                try {
                    parsedUrl = Utils.removeParameterInUrlString(parsedUrl, "_mvosr");
                    parsedUrl = Utils.removeParameterInUrlString(parsedUrl, "pkg");
                } catch (Exception e) {
                    QHADLog.e("startService error." + e.getMessage());
                    parsedUrl = url;
                }


                if (isNullOrEmpty(clickEventId)) {
                    clickEventId = "";
                }
                if (isNullOrEmpty(pkg)) {
                    pkg = "";
                }

                Intent intent = new Intent(QhAdModel.getInstance().getContext(), QhAdModel.getInstance().getQhServiceCls());
                intent.putExtra("url", url);
                intent.putExtra("advertiserid", advertiserid);
                intent.putExtra("campaignid", campaignid);
                intent.putExtra("solutionid", solutionid);
                intent.putExtra("bannerid", bannerid);
                intent.putExtra("pkg", pkg);
                intent.putExtra("impid", impid);
                intent.putExtra("clickid", clickEventId);
                intent.putExtra("action", "download");
                intent.putExtra("nt", DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
                try {
                    QhAdModel.getInstance().getContext().startService(intent);
                    return true;
                } catch (Exception e) {
                    Toast.makeText(clickTask.context, "下载失败，未知错误。", Toast.LENGTH_SHORT);
                    QHADLog.e(QhAdErrorCode.CLICK_DOWNLOAD_APP_ERROR, "startService error.", e, null);
                    return false;
                }
            }
//            if (!isNullOrEmpty(clickEventId) && !isNullOrEmpty(pkg)) {
//            }
            return false;
        }

        @Override
        public void onPageClose() {
            QHADLog.d("Innerbrowser page closed");
            ClickTask clickTask = clickTaskWeakReference.get();
            if (clickTask == null) {
                QHADLog.e(QhAdErrorCode.COMMON_ERROR, "LandingPageListenerImp,click task disposed.");
                return;
            }

            if (clickTask.adEventListener != null) {
                if (clickTask.adView != null) {
                    clickTask.adEventListener.onAdviewDismissedLandpage();
                    if (clickTask.adView instanceof QhSplashAd)
                        clickTask.adEventListener.onAdviewClosed();
                }
            }

            if (clickTask.videoAdOnClickListener != null) {
                clickTask.videoAdOnClickListener.onLandingpageClosed();
            }
        }

        @Override
        public void onPageLoadFinished() {
            ClickTask clickTask = clickTaskWeakReference.get();
            if (clickTask == null) {
                QHADLog.e(QhAdErrorCode.COMMON_ERROR, "LandingPageListenerImp,click task disposed.");
                return;
            }
            if (isOnPageLoadFinishedCalled) {
                QHADLog.e("onPageLoadFinished has called");
                return;
            }
            if (clickTask.adEventListener != null) {
                clickTask.adEventListener.onAdviewIntoLandpage();
            }

            if (clickTask.videoAdOnClickListener != null) {
                clickTask.videoAdOnClickListener.onLandingpageOpened();
            }
            QHADLog.d(String.format("landing page:%s load finished,class:%s", clickTask.vo.ld, clickTask.landingPageView.getClass().getSimpleName()));
            isOnPageLoadFinishedCalled = true;
        }

        @Override
        public void onPageLoadFailed() {
            ClickTask clickTask = clickTaskWeakReference.get();
            if (clickTask == null) {
                QHADLog.e(QhAdErrorCode.COMMON_ERROR, "LandingPageListenerImp,click task disposed.");
                return;
            }
            if (isOnPageLoadFailedCalled) {
                QHADLog.e("onPageLoadFailed has called.");
                return;
            }
            QHADLog.e(QhAdErrorCode.CLICK_INNERBROSWER_ERROR, String.format("landing page:%s load failed,class:%s", clickTask.vo.ld, clickTask.landingPageView.getClass().getSimpleName()));
            isOnPageLoadFailedCalled = true;
        }
    }

    private class DefaultQhLandingPageView implements IQhLandingPageView {

        @Override
        public void open(Context context, String url, IQhLandingPageListener listener) {
            QHADLog.d("Open landingpage in SDK innerbrowser,url: " + url);
            LandingPageActivityBridge bridge = new LandingPageActivityBridge();
            bridge.url = url;
            bridge.bannerid = vo.bannerid;
            bridge.advertiserid = vo.advertiserid;
            bridge.campaignid = vo.campaignid;
            bridge.solutionid = vo.solutionid;
            bridge.impid = vo.impid;
            bridge.landingPageListener = listener;
            QhAdActivity.activityBridge = bridge;
            Intent intent = new Intent(context, QhAdActivity.class);
            int flags = ((Activity) context).getWindow().getAttributes().flags;
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(LandingPageActivityBridge.WINDOW_FLAGS, flags);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                QHADLog.e(QhAdErrorCode.CLICK_INNERBROSWER_ERROR, "innerBrowser error.", e, vo);
                listener.onPageLoadFailed();
            }
        }
    }

    private class ClickTracker implements Runnable {
        @Override
        public void run() {
            String tld = vo.tld;
            URL url;
            try {
                url = new URL(tld);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(1000);
                conn.setRequestMethod("GET");
                conn.setInstanceFollowRedirects(false);
                if (conn.getResponseCode() == 302 || conn.getResponseCode() == 301) {
                    String newurl = conn.getHeaderField("Location");
                    String downloadApkUrl = newurl;
                    if (newurl.contains("?")) {
                        downloadApkUrl = newurl.substring(0, newurl.indexOf('?'));
                    }
                    if (newurl.endsWith(".apk")) {
                        downloadApkUrl = newurl;
                    }

                    if (downloadApkUrl.endsWith(".apk")) {
                        vo.tld = newurl;
                        url = new URL(newurl);
                        String query = url.getQuery();
                        if (query != null) {
                            String ps[] = url.getQuery().split("&");
                            for (String string : ps) {
                                if (string.contains("_mvosr")) {
                                    String p[] = string.split("=");
                                    if (p.length > 0) {
                                        vo.clickEventId = p[1];
                                        vo.tld = Utils.removeParameterInUrlString(newurl, "_mvosr");
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
//                String host = url.getHost();
//                if (host.contains("mediav")) {
//                }
            } catch (Exception e) {
                QHADLog.e(QhAdErrorCode.CLICK_TRACK_REDIRECT_ERROR, "Click Track Error:" + tld, e, vo);
            }

            Message msg = Message.obtain();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }
}