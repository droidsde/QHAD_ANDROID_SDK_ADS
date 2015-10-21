package com.qhad.ads.sdk.model;

import android.content.Context;
import android.os.Handler;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.MessageConfig;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.task.NetsTask;
import com.qhad.ads.sdk.task.ProcessInfoTask;
import com.qhad.ads.sdk.utils.RijindaelUtils;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.ProcessInfoVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProcessinfoHandler {

    private static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MessageConfig.PROCESS_INFO) {
            }
        }

    };

    public ProcessinfoHandler(Context context) {
        ProcessInfoRunable runable = new ProcessInfoRunable(context, handler);
        Thread thread = new Thread(runable);
        thread.start();
    }

    class ProcessInfoRunable implements Runnable {

        private Context context = null;
        private Handler handler = null;

        public ProcessInfoRunable(Context _context, Handler _handler) {
            context = _context;
            handler = _handler;
        }

        @Override
        public void run() {
            try {
                ArrayList<ProcessInfoVO> vos = ProcessInfoTask.getProcessInfo(context);
                JSONObject obj = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for (ProcessInfoVO processInfoVO : vos) {
                    jsonArray.put(processInfoVO.getJsonObject());
                }
                JSONObject deviceObj = new JSONObject();
                deviceObj.put("os", Utils.getSysteminfo());
                deviceObj.put("imei", Utils.getIMEI());
                deviceObj.put("imsi", Utils.getIMSI());
                deviceObj.put("mac", Utils.getMac());
                deviceObj.put("model", Utils.getProductModel());
                deviceObj.put("appv", Utils.getAppVersion());
                deviceObj.put("appname", Utils.getAppname());
                deviceObj.put("apppkg", Utils.getAppPackageName());
                deviceObj.put("longitude", StaticConfig.longitude);
                deviceObj.put("latitude", StaticConfig.latitude);
                deviceObj.put("sdkv", StaticConfig.SDK_VERSION);
                deviceObj.put("ssid", Utils.getRouteSSID());
                deviceObj.put("bssid", Utils.getRouteMac());
                deviceObj.put("androidid", Utils.getAndroidid());
                obj.put("deviceinfo", deviceObj);
                obj.put("processinfo", jsonArray);

                long currentTime = System.currentTimeMillis();
                int index = (int) (currentTime % 64);
                String password = RijindaelUtils.KEY_STORE[index];
                password = RijindaelUtils.hexPasswordToStrPassword(password);
                String content = RijindaelUtils.encrypt(obj.toString(), password);
                if (content != null) {
                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put("pinfo", content);
                    param.put("t", Long.toString(currentTime));
                    param.put("datatype", "1");
                    NetsTask.postData(StaticConfig.AL_LOG_URL, param, handler, MessageConfig.PROCESS_INFO);
                }
            } catch (Exception e) {
                QHADLog.e(e.getMessage());
            }
        }
    }
}
