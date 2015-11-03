package com.qhad.ads.sdk.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 37X21=777 on 2015/10/15.
 * <p/>
 * 目前对外只提供动态代码布局的构造方法，
 * {@link #QhadAlertDialog(Context context, int layoutId) }等到有需要时再对外放开，
 * 此种xml布局方式涉及到的{@link #setImageId(int)},{@link #setPositiveId(int)}等方法需同步对外放开。
 */
public final class QhadAlertDialog extends AlertDialog {

    private QhadAlertLogoView qhadAlertLogoView;
    private IMvadAlertListener qhadAlertListener;
    private boolean isDynamic = false;

    private QhadAlertDialog(Context context, int layoutId) {
        super(context);
        qhadAlertLogoView = new QhadAlertLogoView(context, layoutId);
        isDynamic = false;
        initDynamic();
    }

    public QhadAlertDialog(Context context) {
        super(context);
        qhadAlertLogoView = new QhadAlertLogoView(context);
        isDynamic = true;
        initDynamic();
    }

    private void initDynamic() {
        super.setView(qhadAlertLogoView, 0, 0, 0, 0);

        if (isDynamic) {
            MvadAlertButtonTouchListener touchListener = new MvadAlertButtonTouchListener();
            qhadAlertLogoView.getTextViewNegative().setOnTouchListener(touchListener);
            qhadAlertLogoView.getTextViewPositive().setOnTouchListener(touchListener);

            registerNegativeListener();
            registerPositiveListener();
        }

        registerCancelListener();
    }

    private void registerCancelListener() {
        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                if (qhadAlertListener != null) {
                    qhadAlertListener.onCancel();
                }
            }
        });
    }

    private void registerPositiveListener() {
        qhadAlertLogoView.getTextViewPositive().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (qhadAlertListener != null) {
                    qhadAlertListener.onPositiveClicked(com.qhad.ads.sdk.widget.QhadAlertDialog.this);
                }
            }
        });
    }

    private void registerNegativeListener() {
        qhadAlertLogoView.getTextViewNegative().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (qhadAlertListener != null) {
                    qhadAlertListener.onNegativeClicked(com.qhad.ads.sdk.widget.QhadAlertDialog.this);
                }
            }
        });
    }

    /**
     * @param mvadAlertListener 点击事件监听器
     */
    public void setMvadAlertListener(IMvadAlertListener mvadAlertListener) {
        this.qhadAlertListener = mvadAlertListener;
    }

    @Override
    public void setMessage(CharSequence message) {
//        super.setMessage(message);
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setMessage(message);
        }
    }

    @Deprecated
    @Override
    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
//        super.setButton(whichButton, text, listener);
    }

    @Deprecated
    @Override
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
//        super.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
    }

    @Override
    public void setTitle(CharSequence title) {
//        super.setTitle(title);
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setTitle(title);
        }
    }

    /**
     * @param description 描述
     */
    public void setDescription(CharSequence description) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setDescription(description);
        }
    }

    @Deprecated
    @Override
    public void setCustomTitle(View customTitleView) {
//        super.setCustomTitle(customTitleView);
    }

    @Deprecated
    @Override
    public void setView(View view) {
//        super.setView(view);
    }

    @Override
    public void setIcon(int resId) {
//        super.setIcon(resId);
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setIcon(resId);
        }
    }

    @Override
    public void setIcon(Drawable icon) {
//        super.setIcon(icon);
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setIcon(icon);
        }
    }

    /**
     * @param url 图片地址
     */
    public void setIcon(String url) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setIconUrl(url);
        }
    }

    /**
     * @param charSequence 文本
     */
    public void setNegativeText(CharSequence charSequence) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setNegativeText(charSequence);
        }
    }

    /**
     * @param charSequence 文本
     */
    public void setPositiveText(CharSequence charSequence) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setPositiveText(charSequence);
        }
    }


    /**
     * @param lineColor 分割线颜色
     *                  仅支持使用{@link #QhadAlertDialog(Context context) }构造的实例
     */
    public void setLineColor(int lineColor) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setLineColor(lineColor);
        }
    }

    /**
     * @param resId 本地图片资源id
     *              设置弹框背景
     */
    private void setBackgroundResource(int resId) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.setBackgroundResource(resId);
        }
    }

    private class MvadAlertButtonTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setBackgroundColor(Color.argb(130, 0, 0, 0));
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.setBackgroundColor(Color.argb(0, 0, 0, 0));
            }
            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.setBackgroundColor(Color.argb(0, 0, 0, 0));
            }
            return false;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.destory();
            qhadAlertLogoView = null;
        }
    }

    /**
     * @param id 设置ImageView控件的id
     *           仅支持使用{@link #QhadAlertDialog(Context context, int layoutId) }构造的实例
     */
    private void setImageId(int id) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.findImageViewLogo(id);
        }
    }

    /**
     * @param id 设置标题TextView控件的id
     *           仅支持使用{@link #QhadAlertDialog(Context context, int layoutId) }构造的实例
     */
    private void setTitleId(int id) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.findTextViewTitle(id);
        }
    }

    /**
     * @param id 设置描述TextView控件的id
     *           仅支持使用{@link #QhadAlertDialog(Context context, int layoutId) }构造的实例
     */
    private void setDescId(int id) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.findTextViewDesc(id);
        }
    }

    /**
     * @param id 设置消息TextView控件的id
     *           仅支持使用{@link #QhadAlertDialog(Context context, int layoutId) }构造的实例
     */
    private void setMessageId(int id) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.findTextViewMessage(id);
        }
    }

    /**
     * @param id 设置取消Button控件的id
     *           仅支持使用{@link #QhadAlertDialog(Context context, int layoutId) }构造的实例
     */
    private void setNegativeId(int id) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.findButtonNegative(id);
            registerNegativeListener();
        }
    }

    /**
     * @param id 设置确定Button控件的id
     *           仅支持使用{@link #QhadAlertDialog(Context context, int layoutId) }构造的实例
     */
    private void setPositiveId(int id) {
        if (qhadAlertLogoView != null) {
            qhadAlertLogoView.findButtonPositive(id);
            registerPositiveListener();
        }
    }

    public interface IMvadAlertListener {

        void onNegativeClicked(com.qhad.ads.sdk.widget.QhadAlertDialog dialog);

        void onPositiveClicked(com.qhad.ads.sdk.widget.QhadAlertDialog dialog);

        void onCancel();
    }
}
