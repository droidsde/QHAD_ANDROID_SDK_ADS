package com.qhad.ads.sdk.core;

import com.qhad.ads.sdk.adcore._D;
import com.qhad.ads.sdk.adsinterfaces.IQhAdAttributes;
import com.qhad.ads.sdk.adsinterfaces.IQhProductAdAttributes;
import com.qhad.ads.sdk.adsinterfaces.IQhVideoAdAttributes;
import com.qhad.ads.sdk.interfaces.DynamicObject;
import com.qhad.ads.sdk.interfaces.ObjectDescriptor;
import com.qhad.ads.sdk.logs.QHADLog;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by chengsiy on 2015/6/30.
 */
class QhAdAttributesProxy implements IQhAdAttributes {

    private final DynamicObject dynamicObject;

    private QhAdAttributesProxy(DynamicObject dynamicObject) {
        if (dynamicObject == null)
            dynamicObject = new DynamicObject() {
                @Override
                public Object invoke(int funcId, Object arg) {
                    return null;
                }
            };
        this.dynamicObject = dynamicObject;
    }

    public static IQhAdAttributes create(DynamicObject dynamicObject) {
        if (dynamicObject instanceof ObjectDescriptor) {
            int type = (int) ((ObjectDescriptor) dynamicObject).getDescriptor();
            switch (type) {
                case _D.TYPE_QHPRODUCTADATTRIBUTES:
                    QHADLog.d("ADS", "TYPE_QHPRODUCTADATTRIBUTES");
                    return new QhProductAdAttributesProxy(dynamicObject);
                case _D.TYPE_QHVIDEOADATTRIBUTES:
                    QHADLog.d("ADS", "TYPE_QHVIDEOADATTRIBUTES");
                    return new QhVideoAdAttributesProxy(dynamicObject);
            }
        }
        return new QhAdAttributesProxy(dynamicObject);
    }

    @Override
    public HashMap<String, String> getAttributes() {
        QHADLog.d("ADS", "QHADATTRIBUTES_getAttributes");
        return (HashMap<String, String>) dynamicObject.invoke(_D.QHADATTRIBUTES_getAttributes, null);
    }

    private static class QhProductAdAttributesProxy extends QhAdAttributesProxy implements IQhProductAdAttributes {

        private QhProductAdAttributesProxy(DynamicObject dynamicObject) {
            super(dynamicObject);
        }

        @Override
        public void setCategory(String category, int sourceid) {

        }

        @Override
        public void setPrice(double price) {

        }
    }

    private static class QhVideoAdAttributesProxy extends QhAdAttributesProxy implements IQhVideoAdAttributes {

        private QhVideoAdAttributesProxy(DynamicObject dynamicObject) {
            super(dynamicObject);
        }

        @Override
        public void setCategory(int typeid) {

        }

        @Override
        public void setTitle(String title) {

        }

        @Override
        public void setEpisode(int n) {

        }

        @Override
        public void setRegion(String region) {

        }

        @Override
        public void setCast(HashSet<String> names) {

        }

        @Override
        public void setYear(int n) {

        }

        @Override
        public void setSource(String url) {

        }
    }

}



