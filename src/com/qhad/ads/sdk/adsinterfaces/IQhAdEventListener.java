package com.qhad.ads.sdk.adsinterfaces;

/**
 * @author Duan
 */
public interface IQhAdEventListener {

    /**
     * 获取广告成功
     *
     * @param adView
     */
    void onAdviewGotAdSucceed();

    /**
     * 获取广告失败
     *
     * @param adView
     */
    void onAdviewGotAdFail();

    /**
     * 广告渲染完成
     *
     * @param adView
     */
    void onAdviewRendered();

    /**
     * 进入落地页
     *
     * @param adView
     */
    void onAdviewIntoLandpage();

    /**
     * 离开落地页
     *
     * @param adView
     */
    void onAdviewDismissedLandpage();

    /**
     * 广告被点击
     *
     * @param adView
     */
    void onAdviewClicked();

    /**
     * 广告被关闭
     */
    void onAdviewClosed();

    /**
     * 当广告实例被销毁
     *
     * @param adView
     */
    void onAdviewDestroyed();
}
