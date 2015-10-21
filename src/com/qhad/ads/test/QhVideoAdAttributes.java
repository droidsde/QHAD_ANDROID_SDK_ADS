/**
 *
 */
package com.qhad.ads.test;

import android.text.TextUtils;

import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author qihuajun
 */
public class QhVideoAdAttributes implements IQhVideoAdAttributes {

    private HashMap<String, String> map = new HashMap<String, String>();
    private HashSet<String> tags = new HashSet<String>();

    /**
     *
     */
    public QhVideoAdAttributes() {
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhAdAttributes#getAttributes()
     */
    @Override
    public HashMap<String, String> getAttributes() {
        // TODO Auto-generated method stub

        map.put("qhtag", TextUtils.join("_", tags));

        return map;
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes#setCategory(java.lang.String)
     */
    @Override
    public void setCategory(int type) {
        map.put("qhchannel", String.valueOf(type));
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title) {
        map.put("qhname", title);
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes#setEpisode(int)
     */
    @Override
    public void setEpisode(int n) {
        map.put("qhepisode", String.valueOf(n));
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes#setRegion(java.lang.String)
     */
    @Override
    public void setRegion(String region) {
        tags.add(region);
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes#setCast(java.util.HashSet)
     */
    @Override
    public void setCast(HashSet<String> names) {
        tags.addAll(names);
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes#setYear(int)
     */
    @Override
    public void setYear(int n) {
        tags.add(String.valueOf(n));
    }

    /* (non-Javadoc)
     * @see com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes#setSource(java.lang.String)
     */
    @Override
    public void setSource(String url) {
        map.put("qhsource", url);
    }

}
