package com.qhad.ads.sdk.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.service.QhAdServiceBridge;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SystemReceiver extends BroadcastReceiver {

    private List<CommonAdVO> apps;

    public SystemReceiver() {
        apps = new ArrayList<>();
    }

    public void regApps(CommonAdVO vo) {
        synchronized (this) {
            apps.add(vo);
            AdCounter.increment(AdCounter.ACTION_SERVICE_REGISTER_INSTALL_RECEIVER);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        QHADLog.d("SystemReceiver: onReceive");
        try {
            onPackageAdded(intent);
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.COMMON_ERROR, "SystemReceiver.onReceive error.", e);
        }
    }

    private void onPackageAdded(Intent intent) throws Exception {
        String action = intent.getAction();
        if (!Intent.ACTION_PACKAGE_ADDED.equals(action))
            return;
        CommonAdVO foundCommandAdVO = null;
        synchronized (this) {
            Iterator<CommonAdVO> iterator = apps.iterator();
            while (iterator.hasNext()) {
                String packageName = intent.getDataString().split(":")[1];
                CommonAdVO commonAdVO = iterator.next();
                if (packageName.equals(commonAdVO.pkg)) {
                    AdCounter.increment(AdCounter.ACTION_SERVICE_RECEIVED_INSTALL_MATCHED);
                    foundCommandAdVO = commonAdVO;
                    iterator.remove();
                    break;
                }
            }
            if (foundCommandAdVO == null)
                return;
        }
        QHADLog.d("SystemReceiver: app install finished");
        foundCommandAdVO.installEndTime = System.currentTimeMillis();
        foundCommandAdVO.activeStartTime = System.currentTimeMillis();
        try {
            AdCounter.increment(AdCounter.ACTION_SERVICE_START_REPORT_INSTALL_COMPLETE);
            QhAdModel.getInstance().getTrackManager().RegisterTrack(foundCommandAdVO, TrackType.INSTALL_APP);
            QhAdServiceBridge.getServiceBridge().getActiveAppHandler().regApps(foundCommandAdVO);
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.CLICK_ACTIVATE_APP_ERROR, "onPackageAdded error.", e, foundCommandAdVO);
        }
    }
}
