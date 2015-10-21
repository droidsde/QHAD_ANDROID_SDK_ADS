package com.qhad.ads.sdk.logs;

import android.content.Context;
import android.util.Log;

import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.SwitchConfig;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.util.HashMap;

public class QHADLog {

    private final static String TAG = "QHAD";

    private static Context context;

    private QHADLog() {
    }

    public static void init(Context c) {
        context = c;

        new Thread(new Runnable() {
            @Override
            public void run() {
                LogFileManager.uploadAllLogs(context);
            }
        }).start();

    }

    public static void i(String msg) {
        if (msg != null) {
            if (SwitchConfig.LOG) {
                Log.i(TAG, msg);
            }
        }
    }

    public static void d(String msg) {
        if (msg != null) {
            if (SwitchConfig.LOG) {
                Log.d(TAG, msg);
            }
        }
    }

    public static void w(Exception ex) {
        if (ex != null) {
            if (SwitchConfig.LOG) {
                ex.printStackTrace();
            }
        }
    }

    public static void e(String msg) {
        if (msg != null) {
            if (SwitchConfig.LOG) {
                Log.e(TAG, msg);
            }
        }
    }

    public static void e(int errorcode, String msg) {
        e(errorcode, msg, null, null);
    }

    public static void e(int errorcode, String msg, Throwable e) {
        e(errorcode, msg, e, null);
    }

    public static void e(int errorcode, String msg, Throwable e, CommonAdVO vo) {
        if (msg != null) {
            if (SwitchConfig.LOG) {
                if (e != null) {
                    msg += e.getMessage();
                }
                Log.e(TAG, msg);
            }

            if (SwitchConfig.ERROR && context != null) {
                HashMap<String, String> data = new HashMap<String, String>();
                int etype = errorcode / 100;
                data.put("etype", etype + "");
                data.put("ecode", errorcode + "");
                data.put("emsg", Utils.base64Encode(msg));
                data.put("etime", System.currentTimeMillis() + "");

                if (e != null) {
                    data.put("exception", Utils.base64Encode(e.getMessage()));
                    String trace = Utils.stackTraceToString(e);
                    data.put("trace", Utils.base64Encode(trace));
                }

                data.putAll(getBasicParameters());

                if (vo != null) {
                    data.putAll(getAdParameters(vo));
                }

                final HashMap<String, String> logdata = data;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LogUploader.postLog(logdata, context, true);
                    }
                }).start();

            }
        }
    }

    public static void i(String tag, String msg) {
        if (msg != null) {
            if (SwitchConfig.LOG) {
                Log.i(tag, msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if (msg != null) {
            if (SwitchConfig.LOG) {
                Log.d(tag, msg);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (msg != null) {
            if (SwitchConfig.LOG) {
                Log.w(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if (msg != null) {
            if (SwitchConfig.LOG) {
                Log.e(tag, msg);
            }
        }
    }


    private static HashMap<String, String> getAdParameters(CommonAdVO vo) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("bannerid", vo.bannerid);
        map.put("adspaceid", vo.adspaceid);
        map.put("impid", vo.impid);
        map.put("clickid", vo.clickEventId);
        if (vo.adm_type != null)
            map.put("admtype", vo.adm_type.toString());
        else
            map.put("admtype", "");
        if (vo.ld_type != null)
            map.put("ldtype", vo.ld_type.toString());
        else
            map.put("ldtype", "");
        if (vo.adType != null)
            map.put("adtype", vo.adType.ordinal() + "");
        else
            map.put("adtype", "");
        map.put("adwidth", vo.adWidth + "");
        map.put("adheight", vo.adHeight + "");

        return map;
    }

    /**
     * 获取基本参数
     *
     * @return
     */
    private static HashMap<String, String> getBasicParameters() {
        HashMap<String, String> map = new HashMap<String, String>();

        map.put("apppackagename", Utils.getAppPackageName());
        map.put("appname", Utils.getAppname());
        map.put("appv", Utils.getAppVersion());
        map.put("sdkv", StaticConfig.SDK_VERSION);
        map.put("channel", StaticConfig.CHANNEL_ID);
        map.put("os", Utils.getSysteminfo());
        map.put("imei", Utils.getIMEI());
        map.put("imei_md5", Utils.getIMEIWhitMD5());
        map.put("imsi", Utils.getIMSI());
        map.put("imsi_md5", Utils.getIMSIWhitMD5());
        map.put("mac", Utils.getMac());
        map.put("mac_md5", Utils.getMacWhitMD5());
        map.put("model", Utils.getProductModel());
        map.put("screenwidth", Utils.getDeviceScreenSizeWithString(true));
        map.put("screenheight", Utils.getDeviceScreenSizeWithString(false));
        map.put("so", Utils.getScreenOrientation());
        map.put("density", Utils.getDeviceDensity() + "");
        map.put("appname", Utils.getAppname());
        map.put("apppkg", Utils.getAppPackageName());
        map.put("net", Utils.getCurrentNetWorkInfo());
        map.put("androidid", Utils.getAndroidid());
        map.put("androidid_md5", Utils.getAndroididWithMD5());
        map.put("longitude", StaticConfig.longitude);
        map.put("latitude", StaticConfig.latitude);
        map.put("brand", Utils.getBrand());
        map.put("carrier", Utils.getNetworkOperator());
        map.put("m2id", Utils.getm2id());
        map.put("serialid", Utils.getDeviceSerial());
        map.put("devicetype", Utils.getDeviceType() + "");
        map.put("rmac", Utils.getRouteMac());
        map.put("rssid", Utils.getRouteSSID());

        return map;
    }


}
