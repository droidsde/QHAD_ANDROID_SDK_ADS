package com.qhad.ads.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.qhad.ads.R;

/**
 * Created by chengsiy on 2015/5/7.
 */
public class HomeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        findViewById(R.id.amltBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AMLTActivity.class);
                startActivity(intent);
            }
        });
    }
}
