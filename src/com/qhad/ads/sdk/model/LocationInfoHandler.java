package com.qhad.ads.sdk.model;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.SwitchConfig;

public class LocationInfoHandler {

    /**
     * 基站信息回调接口
     */
    private LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location mlocation) {
            if (mlocation != null) {
                StaticConfig.longitude = mlocation.getLongitude() + "";
                StaticConfig.latitude = mlocation.getLatitude() + "";
            }
        }
    };

    public LocationInfoHandler(Context context) {
        try {

            if (SwitchConfig.DEV) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
            }
        } catch (Exception e) {
            QHADLog.e(e.getMessage());
        }
    }
}
