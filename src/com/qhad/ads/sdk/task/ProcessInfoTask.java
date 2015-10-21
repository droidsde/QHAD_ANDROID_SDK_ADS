package com.qhad.ads.sdk.task;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import com.qhad.ads.sdk.vo.ProcessInfoVO;

import java.util.ArrayList;
import java.util.List;

public class ProcessInfoTask {

    public static ArrayList<ProcessInfoVO> getProcessInfo(Context context) throws Exception {
        ArrayList<ProcessInfoVO> infoList = new ArrayList<ProcessInfoVO>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (RunningAppProcessInfo info : infos) {
            ProcessInfoVO vo = new ProcessInfoVO();
            vo.pid = info.pid;
            vo.uid = info.uid;
            vo.pn = info.processName;
            vo.pkg = info.pkgList;

            infoList.add(vo);
        }
        return infoList;
    }
}
