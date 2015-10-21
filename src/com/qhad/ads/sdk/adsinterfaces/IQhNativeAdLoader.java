package com.qhad.ads.sdk.adsinterfaces;

import java.util.HashSet;

public interface IQhNativeAdLoader {
    void loadAds();

    void loadAds(int n);

    void setKeywords(HashSet<String> keywordList);

    void clearKeywords();

    void setAdAttributes(IQhAdAttributes attributes);

    void clearAdAttributes();
}
