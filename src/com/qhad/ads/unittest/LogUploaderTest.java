package com.qhad.ads.unittest;

import android.test.InstrumentationTestCase;

import com.qhad.ads.sdk.logs.LogUploader;

import java.util.HashMap;

/**
 * Created by qihuajun on 2015/5/19.
 */
public class LogUploaderTest extends InstrumentationTestCase {


    public void testPostLog() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("a", "b");
        LogUploader.postLog(map, getInstrumentation().getContext(), true);

        for (int i = 0; i < 2; i++) {
            LogUploader.postLog(map, getInstrumentation().getContext(), true);
        }

        assertTrue(true);
    }
}
