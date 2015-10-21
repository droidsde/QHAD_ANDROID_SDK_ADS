package com.qhad.ads.sdk.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.webkit.URLUtil;

import com.qhad.ads.sdk.logs.QHADLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Duan
 */
public class Utils {
    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    /**
     * @param ctx 应用上下文
     */
    public static void init(Context ctx) {
        mContext = ctx;
    }

    /**
     * 获取资源id
     *
     * @param context   上下文
     * @param className
     * @param name
     * @return
     */
    public static int getIdByName(Context context, String className, String name) {
        String defPackage = context.getPackageName();
        return context.getResources()
                .getIdentifier(name, className, defPackage);
    }

    /**
     * 获取Android Id
     *
     * @return
     */
    public static String getAndroidid() {
        String androidid = "";
        try {
            androidid = Secure.getString(getContext().getContentResolver(),
                    Secure.ANDROID_ID);
        } catch (Exception e) {
            QHADLog.e("获取AndroidId失败");
        }
        return androidid;
    }

    /**
     * 获取设备序列号
     *
     * @return
     */
    public static String getDeviceSerial() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }

    public static String getm2id() {
        String m2id = "";
        m2id = Utils.getIMEI() + Utils.getAndroidid() + Utils.getDeviceSerial();
        m2id = MD5Utils360.encode(m2id);
        return m2id;
    }

    /**
     * 获取Android Id MD5
     *
     * @return
     */
    public static String getAndroididWithMD5() {
        String androidid = "";
        try {
            androidid = Secure.getString(getContext().getContentResolver(),
                    Secure.ANDROID_ID);
            if (androidid != null) {
                androidid = MD5(androidid);
            }
        } catch (Exception e) {
            QHADLog.e("获取AndroidId失败");
        }
        return androidid;
    }

    /**
     * 获取路由器的MAC地址
     *
     * @return MAC地址
     */
    public static String getRouteMac() {
        try {
            WifiManager wm = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            if (info.getBSSID() == null) {
                return "";
            } else {
                return info.getBSSID() + "";
            }
        } catch (Exception e) {
            QHADLog.e("工具-BSSID Error=" + e.getMessage());
            return "";
        }
    }

    /**
     * 获取路由器SSID
     *
     * @return SSID
     */
    public static String getRouteSSID() {
        try {
            WifiManager wm = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wm.getConnectionInfo();
            if (info.getSSID().contains("<")) {
                return "";
            } else {
                return info.getSSID().replace("\"", "") + "";
            }
        } catch (Exception e) {
            QHADLog.e("工具-SSID Error=" + e.getMessage());
            return "";
        }
    }

    /**
     * 获取应用包名
     *
     * @return 包名
     */
    public static String getAppPackageName() {
        String pn = "";
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    mContext.getPackageName(), 0);
            pn = info.packageName;
        } catch (Exception e) {
            QHADLog.e("工具-应用包名 Error=" + e.getMessage());
            return pn;
        }
        return pn;
    }

    /**
     * 获取屏幕方向
     *
     * @return
     */
    public static String getScreenOrientation() {
        String screenOrientation = "";

        int o = mContext.getResources().getConfiguration().orientation;
        screenOrientation = o + "";

        return screenOrientation;
    }

    /**
     * 获取应用名称
     *
     * @return 应用名称
     */
    public static String getAppname() {
        String appname = "";
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    mContext.getPackageName(), 0);
            appname = info.applicationInfo.loadLabel(manager).toString();
        } catch (Exception e) {
            QHADLog.e("工具-AppName Error=" + e.getMessage());
            return appname;
        }
        return appname;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getAppVersion() {
        String ver = "";
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    mContext.getPackageName(), 0);
            ver = info.versionName;
        } catch (Exception e) {
            QHADLog.e("工具-AppVer Error=" + e.getMessage());
            return ver;
        }
        return ver;
    }

    /**
     * 获取MAC地址
     *
     * @return mac地址
     */
    public static String getMac() {
        String mac = "";
        try {
            WifiManager wifi = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String macAddress = info.getMacAddress();
            if (macAddress != null) {
                mac = macAddress;
            }
        } catch (Exception e) {
            QHADLog.e("工具-Mac Error=" + e.getMessage());
        }
        return mac;
    }

    /**
     * 获取MAC地址
     *
     * @return mac地址MD5
     */
    public static String getMacWhitMD5() {
        String mac = "";
        try {
            WifiManager wifi = (WifiManager) mContext
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String macAddress = info.getMacAddress();
            if (macAddress != null) {
                if (isNotEmpty(macAddress)) {
                    mac = MD5(macAddress);
                }
            }

        } catch (Exception e) {
            QHADLog.e("工具-Mac Error=" + e.getMessage());
        }
        return mac;
    }

    /**
     * 获取系统版本
     *
     * @return 系统版本号
     */
    public static String getSysteminfo() {
        String systemInfo = "";
        try {
            systemInfo = "Android%20" + android.os.Build.VERSION.RELEASE;
        } catch (Exception e) {
            QHADLog.e("工具-SysVer Error=" + e.getMessage());
            return systemInfo;
        }
        return systemInfo;
    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param str 字符串
     * @return 字符串是否为空
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str);
    }

    public static long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    /**
     * @return 网络是否已经连接
     */
    public static boolean isNetEnable() {
        try {
            ConnectivityManager manger = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manger.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        } catch (Exception e) {
            QHADLog.e("工具-NetIsOn Error=" + e.getMessage());
            return false;
        }
    }

    /**
     * @return 判断是否正在使用WIFI网络
     */
    public static boolean isWifiConnected() {
        ConnectivityManager mConnectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        State state_wifi = null;
        state_wifi = mConnectivity
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return null != state_wifi && State.CONNECTED == state_wifi;
    }

    /**
     * @return 获取手机IMEI号
     */
    public static String getIMEI() {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imei = telephonyManager.getDeviceId();
                if (TextUtils.isEmpty(imei)) {
                    imei = Secure.getString(mContext.getContentResolver(),
                            Secure.ANDROID_ID);
                }
            }
        } catch (Exception e) {
            QHADLog.e("工具-IMEI Error=" + e.getMessage());
        }
        return imei;
    }

    /**
     * @return 获取手机IMEI号MD5
     */
    public static String getIMEIWhitMD5() {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imei = telephonyManager.getDeviceId();
                if (TextUtils.isEmpty(imei)) {
                    imei = Secure.getString(mContext.getContentResolver(),
                            Secure.ANDROID_ID);
                    if (imei != null) {
                        imei = MD5(imei);
                    }
                } else {
                    imei = MD5(imei);
                }
            }
        } catch (Exception e) {
            QHADLog.e("工具-IMEI Error=" + e.getMessage());
        }
        return imei;
    }

    /**
     * @return 获取手机IMSI号
     */
    public static String getIMSI() {
        String imsi = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imsi = telephonyManager.getSubscriberId();
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = "UNKNOWN";
            }
        } catch (Exception e) {
            QHADLog.e("工具-IMSI Error=" + e.getMessage());
        }
        return imsi;
    }

    /**
     * @return 获取手机IMSI号MD5
     */
    public static String getIMSIWhitMD5() {
        String imsi = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imsi = telephonyManager.getSubscriberId();
                if (imsi != null) {
                    imsi = MD5(imsi);
                }
            }
            if (TextUtils.isEmpty(imsi)) {
                imsi = "UNKNOWN";
            }
        } catch (Exception e) {
            QHADLog.e("工具-IMSI Error=" + e.getMessage());
        }
        return imsi;
    }

    /**
     * @param fileName 文件名
     * @return 返回md5后的文件名称
     */
    public static String MD5(String fileName) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = fileName.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            QHADLog.e("工具-fileName-MD5 Error=" + e.getMessage());
            return null;
        }
    }

    public static String getBrand() {
        String brand = Build.BRAND;
        if (brand == null) {
            brand = "";
        }
        return brand;
    }

    /**
     * @return 返回手机型号
     */
    public static String getProductModel() {
        String pn = "";
        try {
            pn = android.os.Build.MODEL;
        } catch (Exception e) {
            QHADLog.e("工具-PhoneModel Error=" + e.getMessage());
            return pn;
        }
        return pn;
    }

    /**
     * @return 返回当前手机的基站信息
     */
    @TargetApi(8)
    public static int getLocalAreaCode() {
        try {
            TelephonyManager telephony = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);

            switch (telephony.getPhoneType()) {
                case TelephonyManager.PHONE_TYPE_GSM:
                    GsmCellLocation gsmCellLocation = (GsmCellLocation) telephony
                            .getCellLocation();
                    if (gsmCellLocation != null) {
                        return gsmCellLocation.getLac();
                    }
                    break;
                case TelephonyManager.PHONE_TYPE_CDMA:
                    CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) telephony
                            .getCellLocation();
                    if (cdmaCellLocation != null) {
                        return cdmaCellLocation.getBaseStationId();
                    }
                    break;
            }
        } catch (Exception e) {
            QHADLog.e("工具-Areacode Error=" + e.getMessage());
        }
        return -1;
    }

    /**
     * @return 当前手机外部存储卡是否可用
     */
    public static boolean isSDCardEnable() {
        String state = android.os.Environment.getExternalStorageState();
        if (android.os.Environment.MEDIA_MOUNTED.equals(state)) {
            if (android.os.Environment.getExternalStorageDirectory().canWrite()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取运营商名称
     *
     * @return
     */
    public static String getNetworkOperator() {
        String operator = "";
        try {
            TelephonyManager manager = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (manager.getNetworkOperator() != null) {
                operator = manager.getNetworkOperator();
                return operator;
            }
        } catch (Exception e) {
            QHADLog.e("工具-getCarrierName=" + e.getMessage());
        }
        return "";
    }

    /**
     * @return 返回当前手机精确的GPS位置信息
     */
    public static Location getLocation() {
        try {
            LocationManager locationManager;
            String contextString = Context.LOCATION_SERVICE;
            locationManager = (LocationManager) mContext
                    .getSystemService(contextString);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(false);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, true);
            if (provider == null) {
                return null;
            }
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                return null;
            }
            return location;
        } catch (Exception e) {
            QHADLog.e("工具-LocalGPS Error=" + e.getMessage());
        }
        return null;
    }

    /**
     * @return 返回手机屏幕宽高尺寸（字符串）
     */
    public static String getDeviceScreenSizeWithString(Boolean isWidth) {
        try {
            DisplayMetrics displayMetrics = mContext.getResources()
                    .getDisplayMetrics();
            int widthPixels = displayMetrics.widthPixels;
            int heightPixels = displayMetrics.heightPixels;
            if (isWidth) {
                return widthPixels + "";
            } else {
                return heightPixels + "";
            }
        } catch (Exception e) {
            QHADLog.e("工具-ScrrenSize Error=" + e.getMessage());
        }
        return "";
    }

    /**
     * @return 返回手机屏幕宽高尺寸（整数值）
     */
    public static int[] getDeviceScreenSizeWithInt() {
        DisplayMetrics displayMetrics = mContext.getResources()
                .getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        int[] num = {widthPixels, heightPixels};
        return num;
    }

    /**
     * @return 获取当前手机的密度信息
     */
    public static double getDeviceDensity() {
        try {
            DisplayMetrics displayMetrics = mContext.getResources()
                    .getDisplayMetrics();
            double density = displayMetrics.density;
            return density;
        } catch (Exception e) {
            QHADLog.e("工具-Density Error=" + e.getMessage());
        }
        return -1;
    }

    /**
     * @param i float类型数据
     * @return 返回int类型数据
     */
    public static int IntegerRounded(float i) {
        int roundedIntefer;
        try {
            roundedIntefer = Integer.valueOf(new BigDecimal(i).setScale(0,
                    BigDecimal.ROUND_HALF_UP).toString());
            return roundedIntefer;
        } catch (NumberFormatException e) {
            QHADLog.e("工具-Float-int Error=" + e.getMessage());
        }
        return 0;
    }

    /**
     * @param str 图片网络url
     * @return 是否是图片类型链接
     */
    public static boolean isPicture(String str) {
        if (isNotEmpty(str)) {
            String suffix = str.substring(str.lastIndexOf(".") + 1,
                    str.length());
            if (suffix != null) {
                if (suffix.equalsIgnoreCase("png")
                        || suffix.equalsIgnoreCase("jpeg")
                        || suffix.equalsIgnoreCase("jpg")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前网络类型
     *
     * @return String
     */
    public static String getCurrentNetWorkInfo() {
        String netInfo = "";
        try {
            ConnectivityManager connectionManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                netInfo = "0";
            } else {
                netInfo = networkInfo.getExtraInfo();
            }
            return netInfo;
        } catch (Exception e) {
            QHADLog.e("工具-CurrentNetInfo Error=" + e.getMessage());
        }
        return netInfo;
    }

    /**
     * @param url
     * @return 返回MD5后的新名称
     */
    public static String GenerateJPGName(String url) {
        try {
            if (isNotEmpty(url)) {
                String suffixName = url.substring(url.lastIndexOf("."));
                String md5Vid = Utils.MD5(url);
                return md5Vid + suffixName;
            }
        } catch (Exception e) {
            QHADLog.e("工具-Url-MD5 Error=" + e.getMessage());
        }
        return "";
    }

    /**
     * 图片数据目录
     *
     * @return 图片文件夹
     */
    public static File imageDataDir() {
        File dir = new File(mContext.getFilesDir().getPath() + "/qhad/image/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * @param url
     * @return 图片资源直接返回后缀文件名
     */
    public static boolean isApk(String url) {
        if (isNotEmpty(url) && URLUtil.isHttpUrl(url)) {
            String localName = url.substring(url.lastIndexOf("/") + 1);
            if (localName != null && "apk".equalsIgnoreCase(localName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param html
     * @return 过滤图片url
     */
    public static String filterImgSrc(String html) {
        Pattern patternImgStr = Pattern.compile(
                "<\\s*img\\s*(?:[^>]*)src\\s*=\\s*([^>]+)",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = patternImgStr.matcher(html);
        String returnString = null;
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group == null) {
                continue;
            }
            if (group.startsWith("'")) {
                returnString = group.substring(1, group.indexOf("'", 1));
            } else if (group.startsWith("\"")) {
                returnString = group.substring(1, group.indexOf("\"", 1));
            } else {
                returnString = group.split("\\s")[0];
            }
        }
        return returnString;
    }

    /**
     * 当前activity是否活动
     *
     * @param context
     * @return
     */
    public static boolean isAction(final Context context) {
        try {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            @SuppressWarnings("deprecation") List<RunningTaskInfo> infos = am.getRunningTasks(1);
            if (context.getClass().getCanonicalName()
                    .equals(infos.get(0).topActivity.getClassName())) {
                return true;
            }
        } catch (Exception e) {
            QHADLog.e(e.getMessage());
        }
        return false;
    }

    /**
     * 屏幕状态 Boolean 是否关屏
     */
    @TargetApi(8)
    public static Boolean getScreenState(Context context) {
        try {
            PowerManager manager = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            android.app.KeyguardManager mKeyguardManager = (KeyguardManager) context
                    .getSystemService(Context.KEYGUARD_SERVICE);
            //noinspection deprecation
            if (manager.isScreenOn()) {
                return !mKeyguardManager.inKeyguardRestrictedInputMode();
            }
        } catch (Exception e) {
            QHADLog.e(e.getMessage());
        }
        return false;
    }

    public static String getMD5(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();
        return getString(m);
    }

    private static String getString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(b[i]);
        }
        return sb.toString();
    }

    public static String getTextFromAssetsFile(Context context, String filename) {
        if (context == null) {
            context = mContext;
        }

        InputStream is = null;

        Writer writer = new StringWriter();
        char[] buffer = new char[8 * 1024];
        try {
            is = context.getResources().getAssets().open(filename);
            Reader reader = new BufferedReader(new InputStreamReader(is,
                    "UTF-8"));
            int n = 0;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    QHADLog.e(e.getMessage());
                }
            }
        }

        return writer.toString();
    }

    /**
     * @param httpurl
     * @return 是否是一个合法的http url
     */
    public static boolean isHttpUrl(String httpurl) {
        return httpurl != null && !"".equals(httpurl)
                && httpurl.startsWith("http://");
    }

    /**
     * 判断是否是平板
     *
     * @return
     */
    public static int getDeviceType() {
        return (mContext.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE ? 2 : 1;
    }

    /**
     * 获取缓存目录
     *
     * @return
     */
    public static String getCacheDir() {
        String cachePath;
        boolean isMediaMounted = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        boolean isnotRemovable = false;
        if (Build.VERSION.SDK_INT > 8 && !isMediaMounted)
            isnotRemovable = !Environment.isExternalStorageRemovable();
        if (isMediaMounted || isnotRemovable) {
            try {
                cachePath = mContext.getExternalCacheDir().getPath();
            } catch (Exception e) {
                QHADLog.e(e.getMessage());
                cachePath = mContext.getCacheDir().getPath();
            }
        } else {
            cachePath = mContext.getCacheDir().getPath();
        }
        return cachePath;
    }

    /**
     * 获取异常的堆栈信息
     *
     * @param e
     * @return
     */
    public static String stackTraceToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }

    /**
     * 对字符串进行base64编码
     *
     * @param str
     * @return
     */
    public static String base64Encode(String str) {
        if (str == null)
            return "";
        byte[] encode = Base64.encode(str.getBytes(), Base64.NO_WRAP);
        return new String(encode);
    }

    public static boolean isEmpty(String str) {
        return str.length() == 0;
    }

    private static boolean isNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String removeParameterInUrlString(String url, String parameterName) {
        String parsedUrl = url;
        Uri uri = Uri.parse(url);
        String queryString = uri.getQuery();
        String parameter = uri.getQueryParameter(parameterName);
        String hostUrl = "";

        if (url.contains("?")) {
            hostUrl = url.substring(0, url.indexOf('?'));
        } else {
            return parsedUrl;
        }

        if (!Utils.isNullOrEmpty(queryString) && !Utils.isNullOrEmpty(parameter)) {
            String replaceStr = "";
            if (url.contains("?" + parameterName)) {
                replaceStr = parameterName + "=" + parameter;
                if (!queryString.equals(replaceStr)) {
                    replaceStr = replaceStr + "&";
                }
            }
            if (url.contains("&" + parameterName)) {
                replaceStr = "&" + parameterName + "=" + parameter;
            }
            queryString = queryString.replace(replaceStr, "");
            if (queryString.length() > 0) {
                parsedUrl = hostUrl + "?" + queryString;
            } else {
                parsedUrl = hostUrl;
            }
        }

        return parsedUrl;
    }
}
