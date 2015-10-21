package com.qhad.ads.sdk.core;

import android.app.Activity;
import android.content.Context;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAd;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.task.ClickTask;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class QhNativeAd implements IQhNativeAd, DynamicObject {

    private CommonAdVO vo;
    private Context context;
    private boolean isShowed = false;
    private ClickTask clickTask;

    public QhNativeAd(CommonAdVO vo, Context context) {
        this.vo = vo;
        this.context = context;
    }

    public String getBannerid() {
        if (vo != null) {
            return vo.bannerid;
        }

        return null;
    }

    public JSONObject getContent() {
        QHADLog.i("QHAD", "NativeAd getContent " + vo.adspaceid + " " + vo.bannerid);
        try {
            JSONTokener jsonParser = new JSONTokener(vo.adm);
            JSONObject adjson = (JSONObject) jsonParser.nextValue();
            return adjson;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            QHADLog.e(QhAdErrorCode.ADM_JSON_PARSE_ERROR, "QhNativeAd get content failed, adm.", e, vo);
        } catch (RuntimeException e) {
            QHADLog.e(QhAdErrorCode.ADM_JSON_PARSE_ERROR, "QhNativeAd get content failed, adm.", e, vo);
        }
        return null;
    }

    public void onAdShowed() {
        QHADLog.i("QHAD", "NativeAd onAdShowed " + vo.adspaceid + " " + vo.bannerid);
        AdCounter.increment(AdCounter.ACTION_APP_CALL_IMPTRACK);
        if (!isShowed) {
            long currentTime = Utils.getCurrentTime();

            if (currentTime - vo.mid < StaticConfig.MAX_REQUEST_SHOW_INTERVAL * 1000) {
                QhAdModel.getInstance().getTrackManager().RegisterTrack(vo, TrackType.IMP_AD);
                isShowed = true;
                QHADLog.i("QHAD", "NativeAd AdShowSucceed " + vo.adspaceid + " " + vo.bannerid);
            } else {
                QHADLog.i("QHAD", "NativeAd AdShowTimeout " + vo.adspaceid + " " + vo.bannerid);
            }

        } else {
            QHADLog.i("QHAD", "NativeAd AdShowRepeated " + vo.adspaceid + " " + vo.bannerid);
        }
    }

    public void onAdClicked() {
        QHADLog.i("QHAD", "NativeAd onAdClicked " + vo.adspaceid + " " + vo.bannerid);
        AdCounter.increment(AdCounter.ACTION_SDK_ON_CLICK_CALLED);
        if (clickTask == null) {
            clickTask = new ClickTask(vo, null, null, context);
        }
        clickTask.onClick(null, null);
    }

    public void onAdClicked(Activity activity) {
        QHADLog.i("QHAD", "NativeAd onAdClicked " + vo.adspaceid + " " + vo.bannerid);
        AdCounter.increment(AdCounter.ACTION_SDK_ON_CLICK_CALLED);
        if (clickTask == null) {
            clickTask = new ClickTask(vo, null, null, activity);
        }
        clickTask.onClick(null, null);
    }

    @Override
    public Object invoke(int funcId, Object arg) {
        switch (funcId) {
            case _D.QHNATIVEAD_getContent:
                QHADLog.d("ADS", "QHNATIVEAD_getContent");
                return getContent();
            case _D.QHNATIVEAD_onAdShowed:
                QHADLog.d("ADS", "QHNATIVEAD_onAdShowed");
                onAdShowed();
                break;
            case _D.QHNATIVEAD_onAdClicked:
                QHADLog.d("ADS", "QHNATIVEAD_onAdClicked");
                if (arg != null) {
                    onAdClicked((Activity) arg);
                    break;
                }
                onAdClicked();
                break;
        }
        return null;
    }
}
