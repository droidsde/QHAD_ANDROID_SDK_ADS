package com.qhad.ads.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.qhad.ads.sdk.interfaces.ServiceBridge;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by chengsiy on 2015/5/19.
 */
abstract class AbstractServiceBridge implements ServiceBridge {

    private final Service service;

    protected AbstractServiceBridge(Service service) {
        this.service = service;
    }

    protected Service getService() {
        return service;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

    }

    @Override
    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
}
