package com.qhad.ads.sdk.task;

import android.net.Uri;
import android.util.Base64;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;
import com.qhad.ads.sdk.res.LandingType;
import com.qhad.ads.sdk.res.ResourceType;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Duan
 */
public class ParsedataTask {

    /**
     * 解析广告数据
     *
     * @param data 广告数据
     * @return 返回广告实例
     */
    public static ArrayList<CommonAdVO> parseCommonDataSet(String data) {
        ArrayList<CommonAdVO> dataset = new ArrayList<CommonAdVO>();

        try {
            JSONTokener jsonParser = new JSONTokener(data);
            JSONArray adjsons = (JSONArray) jsonParser.nextValue();
            int l = adjsons.length();

            for (int i = 0; i < l; i++) {
                JSONObject adjson = adjsons.getJSONObject(i);
                CommonAdVO vo = parseData(adjson);
                if (vo != null) {
                    dataset.add(vo);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            QHADLog.e(QhAdErrorCode.AD_JSON_PARSE_ERROR, "AD JSON Parse Error:", e);
        }


        return dataset;

    }

    private static CommonAdVO parseData(JSONObject adjson) {
        try {
            QHADLog.d("-------------------解析数据-------------------");
            QHADLog.d("解析数据:开始");

            CommonAdVO adVo = new CommonAdVO();
            adVo.mid = Utils.getCurrentTime();
            adVo.adspaceid = adjson.getString("adspaceid");
            if (adjson.has("softid")) {
                adVo.softid = adjson.getString("softid");
            }
            adVo.bannerid = adjson.getString("bannerid");
            adVo.impid = adjson.getString("impid");
            adVo.pkg = adjson.getString("pkg");
            if (adjson.has("app_name"))
                adVo.appName = adjson.getString("app_name");
            if (adjson.has("app_size"))
                adVo.appSize = adjson.getLong("app_size");

            if (adjson.getInt("ld_type") == 0) {
                adVo.ld_type = LandingType.PAGE;
            } else if (adjson.getInt("ld_type") == 1) {
                adVo.ld_type = LandingType.DOWNLAND;
            } else if (adjson.getInt("ld_type") == 2) {
                adVo.ld_type = LandingType.SYS_BROWSER;
            } else {
                adVo.ld_type = LandingType.UNKOWN;
            }

            String ld = adjson.getString("ld");
            ld = Uri.decode(ld);
            adVo.ld = ld;

            if (ld != null && !"".equals(ld)) {
                URL url = new URL(ld);
                String query = url.getQuery();
                if (query != null) {
                    String ps[] = query.split("&");
                    for (String string : ps) {
                        if (string.startsWith("cus=")) {
                            String p[] = string.split("=");
                            if (p.length > 1) {
                                String cus = p[1];
                                String ap[] = cus.split("_");
                                if (ap.length > 3) {
                                    adVo.advertiserid = ap[0];
                                    adVo.campaignid = ap[1];
                                    adVo.solutionid = ap[2];
                                }
                                break;
                            }
                        }
                    }
                }
            }
            if (adjson.has("deep_link")) {
                adVo.deepLink = adjson.getString("deep_link");
            }
            if (adjson.getInt("adm_type") == 0) {
                adVo.adm_type = ResourceType.IMAGE;
            } else if (adjson.getInt("adm_type") == 1) {
                adVo.adm_type = ResourceType.MRAID;
            } else if (adjson.getInt("adm_type") == 2) {
                adVo.adm_type = ResourceType.DSP_HTML5;
            } else if (adjson.getInt("adm_type") == 3) {
                adVo.adm_type = ResourceType.NATIVE;
            } else if (adjson.getInt("adm_type") == 4) {
                adVo.adm_type = ResourceType.VIDEO;
            } else {
                adVo.adm_type = ResourceType.UNKOWN;
            }
            adVo.adm = adjson.getString("adm");

            JSONArray impArr = adjson.getJSONArray("imp_tk");
            int i = 0;
            int len = impArr.length();
            for (i = 0; i < len; i++) {
                adVo.imp_tk.add(impArr.getString(i));
            }

            impArr = adjson.getJSONArray("click_tk");
            i = 0;
            len = impArr.length();
            for (i = 0; i < len; i++) {
                adVo.click_tk.add(impArr.getString(i));
            }

            JSONObject otherJson = adjson.getJSONObject("other_tk");
            @SuppressWarnings("unchecked")
            Iterator<String> it = otherJson.keys();
            while (it.hasNext()) {
                String key = it.next();
                String value = otherJson.getString(key);
                adVo.other_tk.put(key, value);
            }
            QHADLog.d("解析数据:完成");
            return adVo;
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.AD_JSON_PARSE_ERROR, "AD JSON Parse Error:", e);
        }
        return null;
    }


    /**
     * 解析广告数据
     *
     * @param data 广告数据
     * @return 返回广告实例
     */
    public static CommonAdVO parseCommonData(String data) {
        try {
            JSONTokener jsonParser = new JSONTokener(data);
            JSONObject adjson = (JSONObject) jsonParser.nextValue();
            CommonAdVO adVo = parseData(adjson);
            return adVo;
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.AD_JSON_PARSE_ERROR, "AD JSON Parse Error:", e);
        }
        return null;
    }

    /**
     * 检查HTML代码
     *
     * @param base64 编码
     * @return html代码
     */
    public static String checkHtmlData(String base64) {
        try {
            byte b[] = android.util.Base64.decode(base64, Base64.DEFAULT);
            String htmlString = new String(b);
            return htmlString;
        } catch (Exception e) {
            QHADLog.e(QhAdErrorCode.ADM_HTML_PARSE_ERROR, "AD HTML Parse Error:", e);
        }
        return null;
    }

}
