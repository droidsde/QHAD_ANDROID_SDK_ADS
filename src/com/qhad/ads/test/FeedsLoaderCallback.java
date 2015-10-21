package com.qhad.ads.test;

import java.util.ArrayList;

public interface FeedsLoaderCallback {
    void onLoaded(ArrayList<MyListItem> items);

    void whenLoaded(ArrayList<MListItem> items);

    void duringLoaded(ArrayList<AMyListItem> items);
}
