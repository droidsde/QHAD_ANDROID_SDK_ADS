package com.qhad.ads.sdk.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.res.MessageConfig;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.task.NetsTask;
import com.qhad.ads.sdk.utils.RijindaelUtils;
import com.qhad.ads.sdk.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by chengsiy on 2015/7/9.
 */
class PkgInfoHandler implements Runnable {

    private final Handler handler;
    private final Context context;
    private final String COUNT_KEY = "qhad_PkgInfoHandler_upload_count";

    public PkgInfoHandler(Context context) {
        this.context = context;
        handler = new Handler();
        new Thread(this).start();
    }

    private static boolean hasFlag(int flags, int flag) {
        return (flags & flag) == flag;
    }

    /**
     * try upload pkg list
     */
    @Override
    public void run() {
        try {
            if (!needUpload()) {
                QHADLog.d("max count reached,skip upload.");
                return;
            }
            postData();
            finishUpload();
        } catch (Throwable throwable) {
            QHADLog.e(QhAdErrorCode.REPORT_PKGLIST_ERROR, "PkgInfoHandler error.", throwable);
        }
    }

    /**
     * 上传计数器+1
     *
     * @throws Exception
     */
    private void finishUpload() throws Exception {
        synchronized (this) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(getSpFileName(), 0);
            int count = sharedPreferences.getInt(COUNT_KEY, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(COUNT_KEY, ++count);
            editor.commit();
        }
    }

    private String getSpFileName() {
        return String.format("qhad_PkgInfoHandler_%s", new SimpleDateFormat("yyMMdd").format(System.currentTimeMillis()));
    }

    /**
     * 判断是否需要上传
     *
     * @return
     * @throws Throwable
     */
    private boolean needUpload() throws Throwable {
        synchronized (this) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(getSpFileName(), 0);
            int count = sharedPreferences.getInt(COUNT_KEY, 0);
            return count < 1;
        }
    }

    /**
     * 加密并上传数据
     *
     * @throws Throwable
     */
    private void postData() throws Throwable {
        long currentTime = System.currentTimeMillis();
        int index = (int) (currentTime % 64);
        String password = RijindaelUtils.KEY_STORE[index];
        password = RijindaelUtils.hexPasswordToStrPassword(password);
        JSONObject pinfoObj = getPinfoObj();
        String content = RijindaelUtils.encrypt(pinfoObj.toString(), password);
        HashMap<String, String> param = new HashMap<>();
        param.put("pinfo", content);
        param.put("t", Long.toString(currentTime));
        param.put("datatype", "2");
        NetsTask.postData(StaticConfig.AL_LOG_URL, param, handler, MessageConfig.PKG_INFO);
        QHADLog.d("PkgInfoHandler uploaded.");
    }

    /**
     * json pinfo object
     *
     * @return
     * @throws Throwable
     */
    private JSONObject getPinfoObj() throws Throwable {
        JSONObject object = new JSONObject();
        object.put("deviceinfo", getDeviceObj());
        object.put("packageinfo", getPkgInfoListObj());
        return object;
    }

    /**
     * 获取APP LIST JSON
     *
     * @return
     * @throws Throwable
     */
    private JSONArray getPkgInfoListObj() throws Throwable {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        JSONArray resultArray = new JSONArray();
        for (ApplicationInfo info : appList) {
            if (hasFlag(info.flags, ApplicationInfo.FLAG_SYSTEM) || hasFlag(info.flags, ApplicationInfo.FLAG_UPDATED_SYSTEM_APP))//系统APP跳过
                continue;
            JSONObject appInfoObj = getAppInfoObj(info, packageManager);
            resultArray.put(appInfoObj);
        }
        return resultArray;
    }

    /**
     * 获取单个AppInfo
     *
     * @param applicationInfo
     * @param packageManager
     * @return
     * @throws Throwable
     */
    private JSONObject getAppInfoObj(ApplicationInfo applicationInfo, PackageManager packageManager) throws Throwable {
        PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, 0);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pkg", applicationInfo.packageName);
        jsonObject.put("vername", packageInfo.versionName);
        jsonObject.put("vercode", packageInfo.versionCode);
        jsonObject.put("label", applicationInfo.loadLabel(packageManager));
        if (Build.VERSION.SDK_INT > 8) {
            jsonObject.put("firstInstallTime", packageInfo.firstInstallTime);
            jsonObject.put("lastUpdateTime", packageInfo.lastUpdateTime);
        }
        return jsonObject;
    }

    /**
     * deviceinfo field
     *
     * @return
     * @throws Throwable
     */
    private JSONObject getDeviceObj() throws Throwable {
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
        return deviceObj;
    }

}
