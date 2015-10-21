package com.qhad.ads.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.qhad.ads.sdk.model.QhAdModel;
import com.qhad.ads.sdk.res.LandingType;
import com.qhad.ads.sdk.task.ClickTask;
import com.qhad.ads.sdk.vo.CommonAdVO;

/**
 * Created by miao <liangym@qhad.com> on 2015/7/9.
 */
public class DownloadDialogTestActivity extends Activity {

    private CommonAdVO vo;
    private ClickTask clickTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QhAdModel.getInstance().initGlobal(getApplicationContext());

        vo = new CommonAdVO();
        vo.ld_type = LandingType.DOWNLAND;
        vo.tld = vo.ld;
        vo.ld = "http://product.fenxi.com/static/mat.apk";
        vo.pkg = "com.qhad.apptracker";
        vo.advertiserid = "test";
        vo.campaignid = "test";
        vo.solutionid = "test";
        vo.bannerid = "test";
        vo.impid = "test";
        vo.clickEventId = "test";


        LinearLayout rl = new LinearLayout(this);
        rl.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rl.setOrientation(LinearLayout.VERTICAL);

        setContentView(rl);

        setTitle("下载弹窗优化测试");

        Button btn = new Button(this);
        btn.setText("下载");

        final Context context = this;

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (clickTask == null) {
                    clickTask = new ClickTask(vo, null, null, context);
                }
                clickTask.onClick(null, null);
            }
        });
        LinearLayout.LayoutParams btnlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnlp.setMargins(0, 10, 0, 0);
        rl.addView(btn, btnlp);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
