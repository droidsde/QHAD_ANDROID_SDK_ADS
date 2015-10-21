package com.qhad.ads.sdk.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qhad.ads.sdk.httpcache.HttpRequester;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;

import org.json.JSONObject;

/**
 * Created by chengsiy on 2015/6/15.
 */
public class DownloadConfirmLayout extends RelativeLayout {

    private final RelativeLayout iconContainer;
    private final ImageView iconImageView;

    public DownloadConfirmLayout(final Context context, String adm, String message, String appName, String okText, OnClickListener okListener, String cancelText, OnClickListener cancelListener) throws Exception {
        super(context);

        this.setBackgroundColor(Color.WHITE);

        JSONObject nativeAdAdm = new JSONObject(adm);
        String logoUrl = nativeAdAdm.getString("logo");
        String title = appName;
        String desc = nativeAdAdm.getString("desc");
        double density = context.getResources().getDisplayMetrics().density;
        RelativeLayout titleLayout = new RelativeLayout(context);
        titleLayout.setId(titleLayout.hashCode());

        iconContainer = new RelativeLayout(context);
        iconContainer.setId(iconContainer.hashCode());
        iconContainer.setVisibility(GONE);
//        iconContainer.setBackgroundColor(Color.parseColor("#CCCCCC"));
        iconImageView = new ImageView(context);
        iconImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams rllp = new LayoutParams(getPx(178, density), getPx(178, density));
        rllp.addRule(CENTER_IN_PARENT);
        rllp.topMargin = getPx(1, density);
        rllp.leftMargin = getPx(1, density);
        iconContainer.addView(iconImageView, rllp);

        rllp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rllp.addRule(ALIGN_PARENT_TOP);
        titleLayout.addView(iconContainer, rllp);

        RelativeLayout titleDescLayout = new RelativeLayout(context);
        titleDescLayout.setId(titleDescLayout.hashCode());

        TextView titleTextView = new TextView(context);
        titleTextView.setId(titleTextView.hashCode());
        titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        titleTextView.setSingleLine();
        titleTextView.setText(title);
        titleTextView.setTextSize(20);
        titleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        rllp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleDescLayout.addView(titleTextView, rllp);

        TextView descTextView = new TextView(context);
        rllp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rllp.addRule(BELOW, titleTextView.getId());
        rllp.topMargin = getPx(24, density);
        descTextView.setText(desc);
        descTextView.setTextSize(13);
        descTextView.setEllipsize(TextUtils.TruncateAt.END);
        descTextView.setSingleLine();
        titleDescLayout.addView(descTextView, rllp);

        rllp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rllp.leftMargin = getPx(44, density);
        rllp.addRule(RIGHT_OF, iconContainer.getId());
        titleLayout.addView(titleDescLayout, rllp);

        rllp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rllp.leftMargin = getPx(56, density);
        rllp.topMargin = getPx(56, density);
        rllp.addRule(ALIGN_PARENT_TOP);
        addView(titleLayout, rllp);

        View ToplineView = new View(context);
        ToplineView.setId(ToplineView.hashCode());
        ToplineView.setBackgroundColor(Color.parseColor("#1C1C1C"));
        rllp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getPx(3, density));
        rllp.addRule(BELOW, titleLayout.getId());
        rllp.topMargin = getPx(21, density);
        addView(ToplineView, rllp);

        TextView messageTextView = new TextView(context);
        messageTextView.setText(message);
        messageTextView.setTextSize((float) 49.0 / 3);
        messageTextView.setId(messageTextView.hashCode());
        rllp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rllp.addRule(CENTER_HORIZONTAL);
        rllp.addRule(BELOW, ToplineView.getId());
        rllp.topMargin = getPx(39, density);
        rllp.bottomMargin = rllp.topMargin;
        rllp.leftMargin = getPx(56, density);
        addView(messageTextView, rllp);

        View ButtomlineView = new View(context);
        ButtomlineView.setId(ButtomlineView.hashCode());
        ButtomlineView.setBackgroundColor(Color.parseColor("#929292"));
        rllp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getPx(2, density));
        rllp.addRule(BELOW, messageTextView.getId());
        addView(ButtomlineView, rllp);

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        Button okBtn = new Button(context);
        okBtn.setId(okBtn.hashCode());
        okBtn.setText(okText);
        okBtn.setOnClickListener(okListener);
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lllp.weight = 0.5f;
        buttonLayout.addView(okBtn, lllp);

        View MiddlelineView = new View(context);
        MiddlelineView.setId(ButtomlineView.hashCode());
        MiddlelineView.setBackgroundColor(Color.parseColor("#929292"));
        rllp = new LayoutParams(getPx(2, density), ViewGroup.LayoutParams.MATCH_PARENT);
        rllp.addRule(RIGHT_OF, okBtn.getId());
        buttonLayout.addView(MiddlelineView, rllp);

        Button cancelBtn = new Button(context);
        cancelBtn.setOnClickListener(cancelListener);
        cancelBtn.setText(cancelText);
        buttonLayout.addView(cancelBtn, lllp);
        rllp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rllp.addRule(BELOW, messageTextView.getId());
        addView(buttonLayout, rllp);

        okBtn.setBackgroundColor(Color.argb(0, 0, 0, 0));
        cancelBtn.setBackgroundColor(Color.argb(0, 0, 0, 0));

        okBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button btn = (Button) v;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundColor(Color.argb(130, 0, 0, 0));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundColor(Color.argb(0, 0, 0, 0));
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    btn.setBackgroundColor(Color.argb(0, 0, 0, 0));
                }

                return false;
            }
        });

        cancelBtn.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button btn = (Button) v;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    btn.setBackgroundColor(Color.argb(130, 0, 0, 0));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    btn.setBackgroundColor(Color.argb(0, 0, 0, 0));
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    btn.setBackgroundColor(Color.argb(0, 0, 0, 0));
                }

                return false;
            }
        });


        HttpRequester.getAsynData(context, logoUrl, true, new HttpRequester.Listener() {
            @Override
            public void onGetDataSucceed(byte[] data) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                iconImageView.setImageBitmap(bitmap);
                iconContainer.setVisibility(VISIBLE);
            }

            @Override
            public void onGetDataFailed(String error) {
                QHADLog.e(QhAdErrorCode.COMMON_ERROR, error);
            }
        });

    }

    private static int getPx(int dPx, double density) {
        return (int) Math.floor(dPx * density / 3);
    }

}
