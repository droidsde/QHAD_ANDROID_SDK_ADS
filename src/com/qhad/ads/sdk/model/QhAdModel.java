package com.qhad.ads.sdk.model;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;

import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageView;
import com.qhad.ads.sdk.interfaces.QhAdBuild;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.task.UpdateSwitchTask;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.utils.Utils;

/**
 * @author Duan
 */
public class QhAdModel {

    private static QhAdModel instance = null;
    private BitmapDrawable closeBd = null;
    private BitmapDrawable bgBd = null;
    private LocationInfoHandler locInfo = null;
    private ExceptionHandler exceptionHandler = null;
    private ProcessinfoHandler processinfoHandler = null;
    private TrackManager trackManager = null;
    private IQhLandingPageView userLandingPage;

    private UpdateSwitchTask updateSwitch = null;
    private Boolean isInited = false;
    private Context context;
    private Class qhServiceCls;

    public static QhAdModel getInstance() {
        if (instance == null) {
            instance = new QhAdModel();
        }
        return instance;
    }

    public IQhLandingPageView getUserLandingPage() {
        synchronized (this) {
            return userLandingPage;
        }
    }

    public void setUserLandingPage(IQhLandingPageView landingPage) {
        synchronized (this) {
            this.userLandingPage = landingPage;
        }
    }

    public TrackManager getTrackManager() {
        return trackManager;
    }

    public Context getContext() {
        return context;
    }

    public BitmapDrawable getClosebtnBitmap() {
        if (closeBd == null) {
            try {
                closeBd = new BitmapDrawable(context.getResources(),
                        BitmapFactory.decodeStream(context.getAssets().open("qh_ad_close.png")));
            } catch (Exception e) {
                QHADLog.d("QhAdModel getClosebtnBitmap error" + e.getMessage());
            }
        }
        return closeBd;
    }

    public BitmapDrawable getBackgroudDrawable() {
        if (bgBd == null) {
            try {
                bgBd = new BitmapDrawable(context.getResources(),
                        BitmapFactory.decodeStream(context.getAssets().open("qh_ad_bg.jpg")));
                bgBd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            } catch (Exception e) {
                QHADLog.d("QhAdModel getBackgroudDrawable error" + e.getMessage());
            }
        }
        return bgBd;
    }

    public synchronized void initGlobal(Context _context) {
        if (!isInited) {
            context = _context;
            StaticConfig.SDK_VERSION = String.format("%s.%s", QhAdBuild.SDK_VER, StaticConfig.PACKAGE_VERSION);
            Utils.init(context);
            try {
                qhServiceCls = Class.forName("com.qhad.ads.sdk.service.QhAdService");
            } catch (Exception e) {
                QHADLog.e("init failed,QhAdService class not found.");
                return;
            }
            QHADLog.init(context);
            AdCounter.initContext(context);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    AdCounter.uploadCounts();
                }
            }).start();

            if (locInfo == null) {
                locInfo = new LocationInfoHandler(context);
            }

            if (exceptionHandler == null) {
                exceptionHandler = new ExceptionHandler(context);
            }

            if (processinfoHandler == null) {
                processinfoHandler = new ProcessinfoHandler(context);
            }

            if (trackManager == null) {
                trackManager = new TrackManager();
            }

            if (updateSwitch == null) {
                updateSwitch = new UpdateSwitchTask(context);
            }
            new PkgInfoHandler(context);
            isInited = true;
        }
    }

    public Class getQhServiceCls() {
        return qhServiceCls;
    }
}
