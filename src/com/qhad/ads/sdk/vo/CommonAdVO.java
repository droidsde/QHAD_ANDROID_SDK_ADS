package com.qhad.ads.sdk.vo;

import android.content.ContentValues;
import android.graphics.Bitmap;

import com.qhad.ads.sdk.core.AD_TYPE;
import com.qhad.ads.sdk.res.LandingType;
import com.qhad.ads.sdk.res.ResourceType;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Duan
 */
public class CommonAdVO {

    public int adWidth = 0;
    public int adHeight = 0;
    public AD_TYPE adType = AD_TYPE.BANNER;
    public long downloadStartTime;
    public long downloadEndTime;
    public String apkFileName;
    /**
     * 库唯一ID
     */
    public long mid;

    /**
     * 广告主ID
     */
    public String advertiserid = "";

    /**
     * 推广计划ID
     */
    public String campaignid = "";

    /**
     * 推广组ID
     */
    public String solutionid = "";

    /**
     * 广告ID
     */
    public String bannerid = null;
    /**
     * 广告位ID
     */
    public String adspaceid = null;
    /**
     * 软件ID
     */
    public String softid = null;
    /**
     * 曝光ID
     */
    public String impid = null;
    /**
     * 点击跳转监测
     */
    public ArrayList<String> click_tk = new ArrayList<String>();
    /**
     * 曝光监测
     */
    public ArrayList<String> imp_tk = new ArrayList<String>();
    /**
     * 其他监测
     */
    public HashMap<String, String> other_tk = new HashMap<String, String>();
    /**
     * ClickEventID
     */
    public String clickEventId = "";
    /**
     * 应用下载文件路径
     */
    public String apkFilePath = null;
    /**
     * 安装 开始/结束 时间
     */
    public long installStartTime;
    public long installEndTime;
    /**
     * 激活 开始/结束 时间
     */
    public long activeStartTime;
    public long activeEndTime;
    /**
     * 落地类型
     */
    public LandingType ld_type;
    /**
     * 落地链接
     */
    public String ld = null;
    /**
     * 真是落地
     */
    public String tld = null;
    /**
     * 包名
     */
    public String pkg = null;
    /**
     * 资源类型
     */
    public ResourceType adm_type;
    /**
     * 资源内容
     */
    public String adm = null;
    /**
     * 图片资源
     */
    public Bitmap bmp;

    //-----------------------------------------------------------//
    /**
     * HTML资源
     */
    public String html;
    public long videoStartTime;
    public long videoEndTime;
    public int videoPlayedTime;
    public int networkTypes;

    /**
     * APP名称
     */
    public String appName;

    /**
     * APP大小
     */
    public long appSize;

    /**
     * deep link 跳转地址
     */
    public String deepLink;

    public CommonAdVO() {
    }

    /**
     * 转换原子
     *
     * @return 原子
     * @throws Exception 转换异常
     */
    public ContentValues getContentValues() throws Exception {
        ContentValues v = new ContentValues();
        v.put("mid", mid);
        v.put("adspaceid", adspaceid);
        v.put("bannerid", bannerid);
        v.put("impid", impid);
        v.put("imp", encodeString(imp_tk));
        v.put("click_tk", encodeString(click_tk));
        return v;
    }

    private String encodeString(ArrayList<String> values) throws Exception {
        StringBuilder sb = new StringBuilder();
        int len = values.size();
        for (int i = 0; i < len; i++) {
            String impStr = URLEncoder.encode(values.get(i), "UTF-8");
            if (i != len - 1) {
                sb.append(impStr + ",");
            } else {
                sb.append(impStr);
            }
        }
        return sb.toString();
    }
}
