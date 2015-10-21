package com.qhad.ads.unittest;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.qhad.ads.sdk.logs.LogFileManager;
import com.qhad.ads.sdk.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by qihuajun on 2015/5/20.
 */
public class LogFileManagerTest extends InstrumentationTestCase {


    public void testSaveLog() {
        Context context = getInstrumentation().getTargetContext().getApplicationContext();
        Utils.init(context);

        LogFileManager.uploadAllLogs(context);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("a", "b");

        for (int i = 0; i < 10; i++) {
            LogFileManager.saveLog(map);
        }

        ArrayList<JSONObject> logs = LogFileManager.getAllLogs();
        assertEquals(10, logs.size());
    }
}
