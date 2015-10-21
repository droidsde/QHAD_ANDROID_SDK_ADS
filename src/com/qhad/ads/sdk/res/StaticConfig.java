package com.qhad.ads.sdk.res;

/**
 * @author Duan
 */
public class StaticConfig {

    /**
     * 广告请求超时时间
     */
    public static final int NET_TIMEOUT = 2000;
    /**
     * 缓存超时时间
     */
    public static final int CACHE_TIMEOUT = 500;
    /**
     * 广告数据传输超时
     */
    public static final int SO_TIMEOUT = 10000;
    /**
     * 原生广告请求和展示的最大间隔
     */
    public static final int MAX_REQUEST_SHOW_INTERVAL = 3600;
    /**
     * 视频广告记录曝光的播放时间
     */
    public static final int VIDEO_AD_SHOW_TIME = 1;
    /**
     * 插屏大小比例
     */
    public static final double INTER_AD_SCALE = 0.7;
    public static final double INTER_HEIGHT_AD_SCALE = 0.6;

    /**
     * 云更新包版本
     */
    public static final int PACKAGE_VERSION = 1113;
    /**
     * 广告系统HOST
     */
    public static final String AD_URL = "http://show.m.mediav.com/s";
    /**
     * 错误上报HOST
     */
    public static final String CRASH_LOG_URL = "http://mvp.mediav.com/t?type=13";
//	public static final String AD_URL = "http://is106dg.prod.mediav.com:9000/s";
//	public static final String AD_URL = "http://10.19.2.53:8080/tt/Post"; 
//	public static final String AD_URL = "http://test.m.mdvdns.com/s";
    /**
     * 上报应用列表信息HOST
     */
    public static final String AL_LOG_URL = "http://mvp.mediav.com/t?type=14";
    public static final String ERROR_LOG_URL = "http://tran.mediav.com/t?type=15";
    public static final String CRASH_LOG_FILE_NAME = "qh_crash.log";
    public static final String ERROR_LOG_FILE_NAME = "qhad_sdk_error.log";
    /**
     * 网络开关HOST
     */
    public static final String SWITCH_URL = "http://show.m.mediav.com/s?switch=1";
    public static final String SWITCH_FILE_NAME = "qh_swich.cfg";
    /**
     * 日志上报HOST
     */
    public static final String LOG_URL = "http://mvp.mediav.com/t";

    /**
     * QH手助上报HOST
     */
    public static final String QHSZLOG_URL = "http://s.360.cn/zhushou/soft.html";

    /**
     * 是否缓存
     */
    public static final Boolean IS_CACHE = true;
    public static final int DOWNLOAD_CLEANUP_DAYS = 15;
    /**
     * SDK版本
     */
    public static String SDK_VERSION;
    /**
     * 轮播时间间隔
     */
    public static long REPLAY_TIME = 30000;
    /**
     * 渠道ID
     */
    public static String CHANNEL_ID = "4";
    public static String TRACK_CHANNEL_ID = "0_04";
    /**
     * 基站信息
     */
    public static String longitude = "";
    public static String latitude = "";
    /**
     * 是否处于活动
     */
    public static Boolean isActive = true;
    /**
     * 是否锁屏
     */
    public static Boolean isOpenscreen = true;
}