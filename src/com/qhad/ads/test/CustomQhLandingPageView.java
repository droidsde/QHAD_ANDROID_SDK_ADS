package com.qhad.ads.test;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageListener;
import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageView;

/**
 * Created by chengsiy on 2015/6/24.
 */
public class CustomQhLandingPageView implements IQhLandingPageView {

    private static final String LOG_TAG = CustomQhLandingPageView.class.getSimpleName();

    @Override
    public void open(Context context, String url, IQhLandingPageListener listener) {
        if (url == null) {
            Log.e(LOG_TAG, "custom landing page disabled by sdk.");
            return;
        }
        CustomLandingPageActivity.ldUrl = url;
        CustomLandingPageActivity.listener = listener;
        Intent intent = new Intent(context, CustomLandingPageActivity.class);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(LOG_TAG, "unable start activity.", e);
            listener.onPageLoadFailed();
        }
    }
}
