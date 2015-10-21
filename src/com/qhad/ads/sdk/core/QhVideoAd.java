/**
 *
 */
package com.qhad.ads.sdk.core;

import android.app.Activity;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAd;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdOnClickListener;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.task.ClickTask;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author qihuajun
 */
public class QhVideoAd implements IQhVideoAd, DynamicObject {
    private CommonAdVO vo;
    private boolean isShowed = false;
    protected TimerTask impressionTask = new TimerTask() {
        @Override
        public void run() {
            onAdShowed();
        }

    };
    private boolean isPlaying = false;


    /**
     *
     */
    public QhVideoAd(CommonAdVO vo) {
        this.vo = vo;
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAd#getContent()
     */
    @Override
    public JSONObject getContent() {
        QHADLog.i("QHAD", "Get content of video ad");
        try {
            JSONTokener jsonParser = new JSONTokener(vo.adm);
            JSONObject adjson = (JSONObject) jsonParser.nextValue();
            return adjson;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            QHADLog.e(QhAdErrorCode.ADM_JSON_PARSE_ERROR, "QhVideoAd get content failed, adm.", e, vo);
        }
        return null;
    }

    public String getBannerid() {
        if (vo != null) {
            return vo.bannerid;
        }

        return null;
    }

    private void onAdShowed() {
        QHADLog.d("Video ad onAdShowed ");
        if (!isShowed) {
            long currentTime = Utils.getCurrentTime();

            if (currentTime - vo.mid < StaticConfig.MAX_REQUEST_SHOW_INTERVAL * 1000) {
                QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.IMP_AD);
                isShowed = true;
            }

        }
    }


    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAd#onAdPlayStarted()
     */
    @Override
    public void onAdPlayStarted() {
        QHADLog.i("QHAD", "Video ad onAdPlayStarted");
        isPlaying = true;
        vo.videoStartTime = System.currentTimeMillis();

        if (!isShowed) {
            Timer timer = new Timer();
            timer.schedule(impressionTask, StaticConfig.VIDEO_AD_SHOW_TIME * 1000);
        }

    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAd#onAdPlayExit(int)
     */
    @Override
    public void onAdPlayExit(int n) {
        QHADLog.i("QHAD", "Video ad onAdPlayExit:" + n);
        isPlaying = false;

        vo.videoEndTime = System.currentTimeMillis();
        vo.videoPlayedTime = n;
        QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.FINISH_VIDEO_PLAY);
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAd#onAdPlayFinshed(int)
     */
    @Override
    public void onAdPlayFinshed(int n) {
        QHADLog.i("QHAD", "Video ad onAdPlayFinshed:" + n);
        isPlaying = false;

        vo.videoEndTime = System.currentTimeMillis();
        vo.videoPlayedTime = n;
        QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.FINISH_VIDEO_PLAY);
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAd#onAdClicked(int)
     */
    @Override
    public void onAdClicked(Activity activity, int n, IQhVideoAdOnClickListener listener) {

        ClickTask clickTask = new ClickTask(vo, listener, activity);
        clickTask.onClick(null, null);
    }

    @Override
    public Object invoke(int funcId, Object arg) {
        switch (funcId) {
            case _D.QHVIDEOAD_getContent:
                QHADLog.d("ADS", "QHVIDEOAD_getContent");
                return getContent();
            case _D.QHVIDEOAD_onAdPlayStarted:
                QHADLog.d("ADS", "QHVIDEOAD_onAdPlayStarted");
                onAdPlayStarted();
                break;
            case _D.QHVIDEOAD_onAdPlayExit:
                QHADLog.d("ADS", "QHVIDEOAD_onAdPlayExit");
                onAdPlayExit((int) arg);
                break;
            case _D.QHVIDEOAD_onAdPlayFinshed:
                QHADLog.d("ADS", "QHVIDEOAD_onAdPlayFinshed");
                onAdPlayFinshed((int) arg);
                break;
            case _D.QHVIDEOAD_onAdClicked:
                QHADLog.d("ADS", "QHVIDEOAD_onAdClicked");
                Object[] args = (Object[]) arg;
                onAdClicked((Activity) args[0], (int) args[1], new QhVideoAdOnClickListenerProxy((DynamicObject) args[2]));
                break;
        }
        return null;
    }
}
