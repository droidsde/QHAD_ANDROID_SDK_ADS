package com.qhad.ads.sdk.model;

import android.content.Context;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.TrackType;
import com.qhad.ads.sdk.task.ProcessInfoTask;
import com.qhad.ads.sdk.utils.AdCounter;
import com.qhad.ads.sdk.vo.CommonAdVO;
import com.qhad.ads.sdk.vo.ProcessInfoVO;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActiveappMonitor {

    private final Timer timer;
    private List<CommonAdVO> apps;
    private Context context;
    private boolean hasRunningTask;

    public ActiveappMonitor(Context _context) {
        context = _context;
        apps = new ArrayList<>();
        timer = new Timer();
    }

    public void cleanup() {
        timer.cancel();
    }

    public void regApps(final CommonAdVO vo) {
        QHADLog.d("ActiveappMonitor: new app registered");
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (ActiveappMonitor.this) {
                    apps.add(vo);
                    AdCounter.increment(AdCounter.ACTION_SERVICE_REGISTER_ACTIVEAPP_MONITOR);
                    if (hasRunningTask)
                        return;
                    timer.schedule(new ActiveTimerTask(ActiveappMonitor.this), 2000, 1000);
                }
            }
        }).start();

    }

    private static class ActiveTimerTask extends TimerTask {

        private final WeakReference<ActiveappMonitor> monitorWeakReference;

        public ActiveTimerTask(ActiveappMonitor monitor) {
            monitorWeakReference = new WeakReference<>(monitor);
        }

        private boolean isActive(CommonAdVO commonAdVO, List<ProcessInfoVO> processInfoVOs) {
            for (ProcessInfoVO processInfoVO : processInfoVOs) {
                for (String pkg : processInfoVO.pkg) {
                    if (pkg.equals(commonAdVO.pkg)) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void run() {
            ActiveappMonitor monitor = monitorWeakReference.get();
            if (monitor == null) {
                return;
            }
            try {
                List<ProcessInfoVO> processInfoVOs = ProcessInfoTask.getProcessInfo(monitor.context);
                synchronized (monitor) {
                    Iterator<CommonAdVO> iterator = monitor.apps.iterator();
                    while (iterator.hasNext()) {
                        CommonAdVO commonAdVO = iterator.next();
                        if (isActive(commonAdVO, processInfoVOs)) {
                            commonAdVO.activeEndTime = System.currentTimeMillis();
                            AdCounter.increment(AdCounter.ACTION_SERVICE_START_REPORT_ACTIVATE_APP);
                            QhAdModel.getInstance().getTrackManager().RegisterTrack(commonAdVO, TrackType.ACTIVE_APP);
                            iterator.remove();
                        }
                    }
                    if (monitor.apps.size() == 0) {
                        cancel();
                        monitor.hasRunningTask = false;
                        return;
                    }
                }
            } catch (Exception e) {
                QHADLog.e(e.getMessage());
            }

        }
    }
}
