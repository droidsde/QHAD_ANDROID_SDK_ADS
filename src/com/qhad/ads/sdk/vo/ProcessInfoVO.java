package com.qhad.ads.sdk.vo;

import com.qhad.ads.sdk.logs.QHADLog;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProcessInfoVO {
    public int uid;
    public int pid;
    public String pn;
    public String pkg[];

    public JSONObject getJsonObject() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("uid", uid);
            jsonObject.put("pid", pid);
            jsonObject.put("pn", pn);
            JSONArray obj = new JSONArray();
            for (int i = 0; i < pkg.length; i++) {
                obj.put(pkg[i]);
            }
            jsonObject.put("pkg", obj);
            return jsonObject;
        } catch (Exception e) {
            QHADLog.e("组装JSON对象失败 Error=" + e.getMessage());
        }
        return null;
    }
}
