package com.qhad.ads.sdk.adsinterfaces;

import java.util.ArrayList;

public interface IQhVideoAdListener {
    void onVideoAdLoadSucceeded(ArrayList<IQhVideoAd> ads);

    void onVideoAdLoadFailed();
}
