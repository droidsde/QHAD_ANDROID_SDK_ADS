package com.qhad.ads.test;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.qhad.ads.sdk.adsinterfaces.IQhVideoAd;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdListener;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdLoader;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdOnClickListener;
import com.qhad.ads.sdk.core.BridgeMiddleware;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class VideoTest extends Activity {

    private IQhVideoAd videoAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final VideoView vv = new VideoView(this);

        final Activity _activity = this;

        final IQhVideoAdLoader loader = (IQhVideoAdLoader) BridgeMiddleware.initVideoAdLoader(this.getApplicationContext(), "u5aQadl4Xp", new IQhVideoAdListener() {


            @Override
            public void onVideoAdLoadFailed() {
            }

            @Override
            public void onVideoAdLoadSucceeded(ArrayList<IQhVideoAd> ads) {

                if (ads.size() > 0) {
                    videoAd = ads.get(0);

                    JSONObject adjson = videoAd.getContent();

                    String uri;
                    try {
                        uri = adjson.getString("video");
                        vv.setVideoURI(Uri.parse(uri));
                        vv.setOnTouchListener(new OnTouchListener() {

                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                // TODO Auto-generated method stub
                                videoAd.onAdClicked(_activity, 3, new IQhVideoAdOnClickListener() {

                                    @Override
                                    public void onDownloadConfirmed() {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onDownloadCancelled() {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onLandingpageOpened() {
                                        // TODO Auto-generated method stub

                                    }

                                    @Override
                                    public void onLandingpageClosed() {
                                        // TODO Auto-generated method stub

                                    }

                                });
                                vv.pause();
                                return false;
                            }
                        });

                        vv.start();
                        videoAd.onAdPlayStarted();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
        }, false);

        QhVideoAdAttributes attrs = new QhVideoAdAttributes();
        attrs.setCategory(1);
        attrs.setSource("http://v.163.com");
        attrs.setTitle("Demo");

        loader.setAdAttributes(attrs);

        LinearLayout rl = new LinearLayout(this);
        rl.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        rl.setOrientation(LinearLayout.VERTICAL);

        setContentView(rl);

        Button but = new Button(this);
        but.setText("Load ads");

        but.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                loader.loadAds();
            }
        });

        rl.addView(but);
        rl.addView(vv);

        loader.loadAds();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
