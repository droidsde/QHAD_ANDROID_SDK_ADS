package com.qhad.ads.test;

import com.qhad.ads.sdk.adsinterfaces.IQhProductAdAttributes;

import java.util.HashMap;

public class QhProductAdAttributes implements IQhProductAdAttributes {
    private HashMap<String, String> productInfo;

    public QhProductAdAttributes() {
        productInfo = new HashMap<String, String>();
    }

    @Override
    public HashMap<String, String> getAttributes() {
        // TODO Auto-generated method stub
        return productInfo;
    }

    @Override
    public void setCategory(String category, int sourceid) {
        productInfo.put("qhcn", category);
        productInfo.put("qhtid", sourceid + "");
    }

    @Override
    public void setPrice(double price) {
        productInfo.put("price", price + "");
    }

}
