package com.qhad.ads.sdk.task;

import android.content.Context;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.SwitchConfig;
import com.qhad.ads.sdk.utils.LocalFileManager;
import com.qhad.ads.sdk.utils.Utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;


public class UpdateSwitchTask {

    private Context context;

    public UpdateSwitchTask(Context _context) {
        context = _context;
        try {
            String data = LocalFileManager.readFile(StaticConfig.SWITCH_FILE_NAME, context);
            update(data);
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.SWITCH_CONFIG_LOCAL_LOAD_ERROR, "UpdateSwitchTask Load Local File Error", e);
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                String param = "";
                try {
                    param = "?os=" + Utils.getSysteminfo() +
                            "&imei=" + URLEncoder.encode(Utils.getIMEI(), "utf-8") +
                            "&imsi=" + URLEncoder.encode(Utils.getIMSI(), "utf-8") +
                            "&mac=" + URLEncoder.encode(Utils.getMac(), "utf-8") +
                            "&model=" + URLEncoder.encode(Utils.getProductModel(), "utf-8").replace("+", "%20") +
                            "&sdkv=" + URLEncoder.encode(StaticConfig.SDK_VERSION, "utf-8") +
                            "&appv=" + URLEncoder.encode(Utils.getAppVersion(), "utf-8") +
                            "&density=" + Utils.getDeviceDensity() +
                            "&appname=" + URLEncoder.encode(Utils.getAppname(), "utf-8") +
                            "&apppkg=" + URLEncoder.encode(Utils.getAppPackageName(), "utf-8") +
                            "&net=" + URLEncoder.encode(Utils.getCurrentNetWorkInfo(), "utf-8") +
                            "&channelid=" + URLEncoder.encode(StaticConfig.CHANNEL_ID, "utf-8") +
                            "&longitude=" + URLEncoder.encode(StaticConfig.longitude, "utf-8") +
                            "&latitude=" + URLEncoder.encode(StaticConfig.latitude, "utf-8");
                } catch (Exception e) {
                    QHADLog.d("更新配置:URL编码失败");
                }
                String host = StaticConfig.SWITCH_URL + param;
                String data = NetsTask.getAdData(host);
                if (data == null) {
                    QHADLog.e(QhAdErrorCode.SWITCH_CONFIG_REQUEST_ERROR, "UpdateSwitchTask Load GetData Error,URL: " + host);
                    return;
                }

                if (update(data)) {
                    try {
                        LocalFileManager.writeFile(StaticConfig.SWITCH_FILE_NAME, data, context);
                    } catch (Exception e) {
                        QHADLog.e(QhAdErrorCode.SWITCH_CONFIG_SAVE_ERROR, "UpdateSwitchTask Save Config Error", e);
                    }
                } else {
                    try {
                        data = LocalFileManager.readFile(StaticConfig.SWITCH_FILE_NAME, context);
                        if (data.equals("")) {
                            QHADLog.d("更新配置:读错误 本地无数据");
                        } else {
                            update(data);
                        }
                    } catch (Exception e) {
                        QHADLog.e(QhAdErrorCode.SWITCH_CONFIG_LOCAL_LOAD_ERROR, "UpdateSwitchTask Load Local File Error", e);
                    }
                }
            }
        }).start();
    }

    public Boolean update(String data) {
        if (data == null || Utils.isEmpty(data)) {
            QHADLog.d("更新配置:失败 数据不正常");
            return false;
        }


        try {
            JSONTokener jsonParser = new JSONTokener(data);
            JSONObject swhjson = (JSONObject) jsonParser.nextValue();
            SwitchConfig.SWH = intToBoolean(swhjson.getInt("SWH"));
            SwitchConfig.CRASH = intToBoolean(swhjson.getInt("CRASH"));
            SwitchConfig.ERROR = intToBoolean(swhjson.getInt("ERROR"));
            SwitchConfig.DEV = intToBoolean(swhjson.getInt("DEV"));
            SwitchConfig.BANNER = intToBoolean(swhjson.getInt("BANNER"));
            SwitchConfig.FS = intToBoolean(swhjson.getInt("FS"));
            SwitchConfig.INT = intToBoolean(swhjson.getInt("INT"));
            return true;
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.SWITCH_CONFIG_PARSE_ERROR, "SwitchConfig Data Parse Error :" + data, e);
        }

        return false;
    }

    private Boolean intToBoolean(int num) {
        return num != 0;
    }
}
