/**
 *
 */
package com.qhad.ads.sdk.adsinterfaces;

import android.app.Activity;

import org.json.JSONObject;

/**
 * @author qihuajun
 */
public interface IQhVideoAd {
    JSONObject getContent();

    void onAdPlayStarted();

    void onAdPlayExit(int n);

    void onAdPlayFinshed(int n);

    void onAdClicked(Activity activity, int n, IQhVideoAdOnClickListener listener);
}
