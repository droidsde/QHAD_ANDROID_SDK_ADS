/**
 *
 */
package com.qhad.ads.sdk.adsinterfaces;

import java.util.HashSet;

/**
 * @author qihuajun
 */
public interface IQhVideoAdAttributes extends IQhAdAttributes {
    void setCategory(int typeid);

    void setTitle(String title);

    void setEpisode(int n);

    void setRegion(String region);

    void setCast(HashSet<String> names);

    void setYear(int n);

    void setSource(String url);
}
