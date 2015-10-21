package com.qhad.ads.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.qhad.ads.sdk.core.BridgeMiddleware;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.service.QhAdService;

/**
 * Created by chengsiy on 2015/5/28.
 */
public class DownloadTestActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QhAdModel.getInstance().initGlobal(getApplicationContext());
        Intent intent = new Intent(this, QhAdService.class);
        intent.putExtra("action", "download");
        intent.putExtra("url", "http://product.fenxi.com/static/mat.apk");
        intent.putExtra("impid", "fake");
        intent.putExtra("pkg", "com.qhad.apptracker");
        intent.putExtra("clickid", "fake");
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        BridgeMiddleware.activityDestroy(this);
        super.onDestroy();
    }
}
