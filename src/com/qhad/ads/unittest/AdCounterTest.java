package com.qhad.ads.unittest;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.utils.AdCounter;

import java.util.Arrays;

/**
 * Created by qihuajun on 2015/6/27.
 */
public class AdCounterTest extends InstrumentationTestCase {
    public void testAdCounter() {
        Context context = getInstrumentation().getTargetContext().getApplicationContext();
        QhAdModel.getInstance().initGlobal(context);
        AdCounter.initContext(context);

        AdCounter.increment(AdCounter.ACTION_SERVICE_RECEIVE_DOWNLOAD);

        int[] cs = AdCounter.getDailyCounts("20150628");
        QHADLog.d(Arrays.toString(cs));
        assertEquals(44, cs.length);

        AdCounter.uploadCounts();
    }
}
