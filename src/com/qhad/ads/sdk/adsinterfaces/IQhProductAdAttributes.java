/**
 *
 */
package com.qhad.ads.sdk.adsinterfaces;

/**
 * @author qihuajun
 */
public interface IQhProductAdAttributes extends IQhAdAttributes {
    void setCategory(String category, int sourceid);

    void setPrice(double price);
}
