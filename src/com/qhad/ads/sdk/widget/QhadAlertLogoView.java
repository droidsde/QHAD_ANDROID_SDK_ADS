package com.qhad.ads.sdk.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qhad.ads.sdk.httpcache.HttpRequester;
import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.logs.QhAdErrorCode;

/**
 * Created by 37X21=777 on 2015/10/15.
 */
final class QhadAlertLogoView extends FrameLayout {

    private ImageView mImageViewLogo;
    private TextView mTextViewLogoTitle;
    private TextView mTextViewLogoDes;
    private TextView mTextViewLogoMessage;
    private Button mButtonNegative;
    private Button mButtonPositive;

    private View topLineView;
    private View midLineView;
    private View bottomLineView;

    private int mLineColor = Color.parseColor("#1C1C1C");

    public QhadAlertLogoView(Context context, int layoutId) {
        super(context);
        View.inflate(context, layoutId, this);
    }

    public QhadAlertLogoView(Context context) {
        super(context);
        buildLayoutDynamic(context);
    }

    private void buildLayoutDynamic(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        //容器
        LinearLayout outContainer = new LinearLayout(context);
        outContainer.setOrientation(LinearLayout.VERTICAL);
        outContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        outContainer.setGravity(Gravity.CENTER);

        //上方LinearLayout
        LinearLayout topContainer = new LinearLayout(context);
        topContainer.setOrientation(LinearLayout.HORIZONTAL);
        topContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        topContainer.setGravity(Gravity.CENTER_VERTICAL);
        topContainer.setPadding(20, 20, 20, 20);

        //ImageView
        mImageViewLogo = new ImageView(context);
        mImageViewLogo.setLayoutParams(new LinearLayout.LayoutParams(getPx(178, density), getPx(178, density)));
        topContainer.addView(mImageViewLogo);

        //文本Linearlayout
        LinearLayout textContainer = new LinearLayout(context);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        textContainer.setGravity(Gravity.CENTER_VERTICAL);
        textContainer.setPadding(20, 0, 0, 0);

        //文本
        mTextViewLogoTitle = new TextView(context);
        mTextViewLogoTitle.setSingleLine();
        mTextViewLogoTitle.setEllipsize(TextUtils.TruncateAt.END);
        mTextViewLogoTitle.setPadding(0, 0, 0, 0);
        mTextViewLogoTitle.setGravity(Gravity.TOP);
        textContainer.addView(mTextViewLogoTitle, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textLayoutParams.weight = 1;
        mTextViewLogoDes = new TextView(context);
        mTextViewLogoDes.setPadding(0, 0, 0, 0);
        mTextViewLogoDes.setGravity(Gravity.BOTTOM);
        mTextViewLogoDes.setMaxLines(2);
        mTextViewLogoDes.setEllipsize(TextUtils.TruncateAt.END);
        textContainer.addView(mTextViewLogoDes, textLayoutParams);
        topContainer.addView(textContainer);
        outContainer.addView(topContainer);

        topLineView = new View(context);
        topLineView.setBackgroundColor(mLineColor);
        topLineView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getPx(3, density)));
        outContainer.addView(topLineView);

        mTextViewLogoMessage = new TextView(context);
        mTextViewLogoMessage.setPadding(40, 40, 40, 40);
        mTextViewLogoMessage.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        outContainer.addView(mTextViewLogoMessage);

        midLineView = new View(context);
        midLineView.setBackgroundColor(mLineColor);
        midLineView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getPx(3, density)));
        outContainer.addView(midLineView);

        //底部LinearLayout
        LinearLayout bottomContainer = new LinearLayout(context);
        bottomContainer.setOrientation(LinearLayout.HORIZONTAL);
        bottomContainer.setWeightSum(2);
        bottomContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getPx(120, density)));

        mButtonNegative = new Button(context);
        mButtonNegative.setGravity(Gravity.CENTER);
        mButtonNegative.setText("取消");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        bottomContainer.addView(mButtonNegative, layoutParams);

        bottomLineView = new View(context);
        bottomLineView.setBackgroundColor(mLineColor);
        bottomLineView.setLayoutParams(new LinearLayout.LayoutParams(getPx(3, density), ViewGroup.LayoutParams.MATCH_PARENT));
        bottomContainer.addView(bottomLineView);

        mButtonPositive = new Button(context);
        mButtonPositive.setGravity(Gravity.CENTER);
        mButtonPositive.setText("确定");
        bottomContainer.addView(mButtonPositive, layoutParams);
        outContainer.addView(bottomContainer);

        mButtonNegative.setBackgroundColor(Color.argb(0, 0, 0, 0));
        mButtonPositive.setBackgroundColor(Color.argb(0, 0, 0, 0));

        addView(outContainer);

        setLineColor(mTextViewLogoTitle.getTextColors().getDefaultColor());
        mButtonPositive.setTextColor(mTextViewLogoTitle.getTextColors().getDefaultColor());
        mButtonNegative.setTextColor(mTextViewLogoTitle.getTextColors().getDefaultColor());

    }

    private static int getPx(int dPx, float density) {
        return (int) Math.floor(dPx * density / 3);
    }

    /**
     * @param id 根据id查找控件
     */
    public void findImageViewLogo(int id) {
        mImageViewLogo = findView(this, id);
    }

    /**
     * @param id 根据id查找控件
     */
    public void findTextViewTitle(int id) {
        mTextViewLogoTitle = findView(this, id);
    }

    /**
     * @param id 根据id查找控件
     */
    public void findTextViewDesc(int id) {
        mTextViewLogoDes = findView(this, id);
    }

    /**
     * @param id 根据id查找控件
     */
    public void findTextViewMessage(int id) {
        mTextViewLogoMessage = findView(this, id);
    }

    /**
     * @param id 根据id查找控件
     */
    public void findButtonNegative(int id) {
        mButtonNegative = findView(this, id);
    }

    /**
     * @param id 根据id查找控件
     */
    public void findButtonPositive(int id) {
        mButtonPositive = findView(this, id);
    }

    /**
     * @param view 父控件
     * @param id   子控件
     */
    private static <T extends View> T findView(View view, int id) {
        return (T) view.findViewById(id);
    }

    /**
     * @param title 标题
     */
    public void setTitle(CharSequence title) {
        bindTextToView(mTextViewLogoTitle, title);
    }

    /**
     * @param description 描述
     */
    public void setDescription(CharSequence description) {
        bindTextToView(mTextViewLogoDes, description);
    }

    /**
     * @param negativeText 左侧文本
     */
    public void setNegativeText(CharSequence negativeText) {
        bindTextToView(mButtonNegative, negativeText);
    }

    /**
     * @param positiveText 右侧文本
     */
    public void setPositiveText(CharSequence positiveText) {
        bindTextToView(mButtonPositive, positiveText);
    }

    /**
     * @param message 提示信息
     */
    public void setMessage(CharSequence message) {
        bindTextToView(mTextViewLogoMessage, message);
    }

    /**
     * @param url 图片地址
     */
    public void setIconUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("the url of icon can not be empty");
        } else {
            HttpRequester.getAsynData(getContext(), url, true, new HttpRequester.Listener() {
                @Override
                public void onGetDataSucceed(byte[] data) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (mImageViewLogo != null) {
                        mImageViewLogo.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onGetDataFailed(String error) {
                    QHADLog.e(QhAdErrorCode.COMMON_ERROR, error);
                }
            });

        }
    }

    /**
     * @param resId 资源id
     */
    public void setIcon(int resId) {
        if (resId != 0) {
            mImageViewLogo.setImageResource(resId);
        }
    }

    /**
     * @param icon Drawable
     */
    public void setIcon(Drawable icon) {
        if (icon != null) {
            mImageViewLogo.setImageDrawable(icon);
        }
    }

    /**
     * @param mTextView    文本控件
     * @param charSequence 文本内容
     *                     绑定文本内容到视图上
     */
    private void bindTextToView(TextView mTextView, CharSequence charSequence) {
        if (mTextView != null) {
            if (TextUtils.isEmpty(charSequence)) {
                mTextView.setText("");
            } else {
                mTextView.setText(charSequence);
            }
        }
    }

    /**
     * 获取右边文本控件
     */
    public Button getTextViewPositive() {
        return mButtonPositive;
    }

    /**
     * 获取左边文本控件
     */
    public Button getTextViewNegative() {
        return mButtonNegative;
    }

    /**
     * @param lineColor 分割线颜色
     */
    public void setLineColor(int lineColor) {
        this.mLineColor = lineColor;
        if (topLineView != null && midLineView != null && bottomLineView != null) {
            topLineView.setBackgroundColor(lineColor);
            midLineView.setBackgroundColor(lineColor);
            bottomLineView.setBackgroundColor(lineColor);
            setLineAlpha(0.3f);
            topLineView.invalidate();
            midLineView.invalidate();
            bottomLineView.invalidate();
        }
    }

    /**
     * @param alpha 设置透明度
     */
    private void setLineAlpha(float alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            topLineView.setAlpha(alpha);
            midLineView.setAlpha(alpha);
            bottomLineView.setAlpha(alpha);
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
//        super.setBackgroundResource(resid);
        if (getChildCount() > 0 && getChildAt(0) != null) {
            this.getChildAt(0).setBackgroundResource(resid);
        }
    }

    /**
     * 销毁
     */
    public void destory() {
        mImageViewLogo = null;
        mTextViewLogoTitle = null;
        mTextViewLogoDes = null;
        mTextViewLogoMessage = null;
        mButtonNegative = null;
        mButtonPositive = null;
        topLineView = null;
        midLineView = null;
        bottomLineView = null;
    }
}
