package com.qhad.ads.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qhad.ads.R;
import com.qhad.ads.sdk.adcore.QhAdActivity;
import com.qhad.ads.sdk.adsinterfaces.IQhLandingPageListener;
import com.qhad.ads.sdk.core.LandingPageActivityBridge;
import com.qhad.ads.sdk.model.QhAdModel;

/**
 * Created by chengsiy on 2015/5/29.
 */
public class LandingPageTest extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QhAdModel.getInstance().initGlobal(this);
        final LandingPageActivityBridge bridge = new LandingPageActivityBridge();

        setContentView(R.layout.landingpage);

        final Context context = this.getApplicationContext();

        final EditText et = (EditText) this.findViewById(R.id.editText);
        final Button btn = (Button) this.findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "";
                if (et != null) {
                    Editable urltext = et.getText();
                    if (urltext != null) {
                        url = urltext.toString();
                    }
                    if (!url.startsWith("http://")) {
                        url = "http://" + url;
                    }

//                    if (url.contains("?")) {
//                        url = url + "&_mvosr=" + UUID.randomUUID();
//                    } else {
//                        url = url + "?_mvosr=" + UUID.randomUUID();
//                    }

                    bridge.url = url;

                    bridge.landingPageListener = new IQhLandingPageListener() {
                        @Override
                        public void onPageClose() {
                            Toast.makeText(context, "Page Closed!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPageLoadFinished() {
                            Toast.makeText(context, "Page Load Finished!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPageLoadFailed() {
                            Toast.makeText(context, "Page Load Failed!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public boolean onAppDownload(String url) {
                            Toast.makeText(context, "onAppDownloading!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    };
                    QhAdActivity.activityBridge = bridge;
                    Intent intent = new Intent(LandingPageTest.this, QhAdActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });


    }
}
