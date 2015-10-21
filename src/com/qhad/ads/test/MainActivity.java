package com.qhad.ads.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.qhad.ads.R;
import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.SwitchConfig;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwitchConfig.LOG = true;
        setContentView(R.layout.activity_main);
        CheckBox cbCustomLandingPage = (CheckBox) findViewById(R.id.cbCustomLandingPage);
        cbCustomLandingPage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    QhAdModel.getInstance().setUserLandingPage(new CustomQhLandingPageView());
                } else {
                    QhAdModel.getInstance().setUserLandingPage(null);
                }
            }
        });
        Button bannerButton = (Button) findViewById(R.id.bannerbutton);
        bannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BannerTest.class);
                startActivity(intent);
            }
        });

        Button splashButton = (Button) findViewById(R.id.splashbutton);
        splashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                startActivity(intent);
            }
        });

        Button nativebannerButton = (Button) findViewById(R.id.nativebannerbutton);
        nativebannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeBannerTest.class);
                startActivity(intent);
            }
        });

        Button nativebannerlvButton = (Button) findViewById(R.id.nativebannerlvbutton);
        nativebannerlvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeBannerInListView.class);
                startActivity(intent);
            }
        });

        Button interButton = (Button) findViewById(R.id.interbutton);
        interButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InterTest.class);
                startActivity(intent);
            }
        });

        Button nativeButton = (Button) findViewById(R.id.nativebutton);
        nativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NativeTest.class);
                startActivity(intent);
            }
        });

        Button browserbutton = (Button) findViewById(R.id.browserbutton);
        browserbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LandingPageTest.class);
                startActivity(intent);
            }
        });

        Button htmladbutton = (Button) findViewById(R.id.htmladbutton);
        htmladbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HtmlActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
