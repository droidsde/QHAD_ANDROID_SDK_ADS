package com.qhad.ads.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.qhad.ads.R;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAd;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdListener;
import com.qhad.ads.sdk.adsinterfaces.IQhNativeAdLoader;
import com.qhad.ads.sdk.core.BridgeMiddleware;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chengsiy on 2015/5/7.
 */
public class AMLTActivity extends Activity {

    private List<IQhNativeAd> nativeAds;

    public AMLTActivity() {
        nativeAds = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amlt_layout);
        ViewGroup bannerAd = (ViewGroup) findViewById(R.id.bannerAd);
        BridgeMiddleware.initSimpleBanner(bannerAd, this, "5uavuInDAl", false);
        BridgeMiddleware.initSimpleFloatbanner(this, "5uavuInDAl", false, 0, 1);//DEFAULT,BOTTOM
        BridgeMiddleware.initSimpleInterstitial(this, "FFabFd8Xp7", false);
        final LinearLayout nativeAdLayout = (LinearLayout) findViewById(R.id.nativeAd);
        final IQhNativeAdLoader loader = BridgeMiddleware.initNativeAdLoader(this, "PPub5d0djn", new IQhNativeAdListener() {
            @Override
            public void onNativeAdLoadSucceeded(ArrayList<IQhNativeAd> nativeAds) {
                for (final IQhNativeAd nativeAd : nativeAds) {
                    nativeAd.onAdShowed();
                    Button view = (Button) LayoutInflater.from(AMLTActivity.this).inflate(R.layout.button_item, null);
                    try {
                        view.setText(nativeAd.getContent().getString("title"));
                    } catch (JSONException e) {
                        view.setText("none");
                    }
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            nativeAd.onAdClicked();
                        }
                    });
                    nativeAdLayout.addView(view);
                }
            }

            @Override
            public void onNativeAdLoadFailed() {

            }
        }, false);
        findViewById(R.id.loadMoreBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.loadAds();
            }
        });
        findViewById(R.id.closeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loader.loadAds();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BridgeMiddleware.activityDestroy(this);
    }
}
