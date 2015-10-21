package com.qhad.ads.sdk.view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.qhad.ads.sdk.res.LandingType;
import com.qhad.ads.sdk.vo.CommonAdVO;

public class ConfirmView extends RelativeLayout {

    private Button btn = null;
    private onClickCallback callback = null;

    public ConfirmView(Context context) {
        super(context);
    }

    public ConfirmView(Context context, int width, int height) {
        super(context);

        this.setClickable(true);
        this.setBackgroundColor(Color.argb(178, 0, 0, 0));
        this.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                callback.onClickCancel();
                hideConfirm();
            }
        });

        btn = new Button(context);
        btn.setBackgroundColor(Color.argb(255, 0, 160, 232));
        int btnw = 150;
        int btnh = 70;
        if (height < 75) {
            btnh = height - 5;
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(btnw, btnh);
        lp.rightMargin = 30;
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (height > 150) {
            lp.bottomMargin = 30;
        } else {
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
        }
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callback.onClickOk();
            }
        });

        this.addView(btn, lp);
    }

    public void updateBtnText(CommonAdVO vo) {
        String text = "去看看";
        if (vo.ld_type == LandingType.DOWNLAND) {
            text = "下载";
        }
        btn.setText(text);
        btn.setTextColor(Color.WHITE);
    }

    public void setClickCallBack(onClickCallback callback) {
        this.callback = callback;
    }

    public void showConfirm() {
        this.setVisibility(View.VISIBLE);
    }

    public void hideConfirm() {
        this.setVisibility(View.INVISIBLE);
    }

    public interface onClickCallback {
        void onClickOk();

        void onClickCancel();
    }
}
