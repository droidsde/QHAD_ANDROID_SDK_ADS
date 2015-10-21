package com.qhad.ads.sdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.qhad.ads.sdk.adsinterfaces.IQhAdEventListener;
import com.qhad.ads.sdk.core.QhAdView;
import com.qhad.ads.sdk.task.ClickTask;
import com.qhad.ads.sdk.vo.CommonAdVO;

public class AdImageView extends ImageView {

    private CommonAdVO vo = null;
    private IQhAdEventListener adEventListener = null;
    private Context context = null;
    private QhAdView adView = null;
    private ClickTask clickTask = null;

    /**
     * 暂不使用
     * private String namespace = "http://shadow.com";
     * private int color;
     */

    public AdImageView(Context context) {
        super(context);
    }

    public AdImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdImageView(Context _context, IQhAdEventListener _adEventListener, QhAdView _adView) {
        super(_context);
        this.adEventListener = _adEventListener;
        this.context = _context;
        this.adView = _adView;

        this.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                String x = String.valueOf((int) arg1.getX());
                String y = String.valueOf((int) arg1.getY());
                String[] xy = {x, y};

                String w = String.valueOf(arg0.getWidth());
                String h = String.valueOf(arg0.getHeight());
                String[] wh = {w, h};

                if (clickTask == null) {
                    clickTask = new ClickTask(vo, adEventListener, adView, context);
                }
                clickTask.onClick(xy, wh);
                return false;
            }
        });
    }

    public void showAD(CommonAdVO _vo) {
        this.vo = _vo;
        this.setImageBitmap(vo.bmp);
    }

    public void dismissBitmap() {
        if (!vo.bmp.isRecycled()) {
            vo.bmp.recycle();
        }
    }
}
