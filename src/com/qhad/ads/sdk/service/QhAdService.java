package com.qhad.ads.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.qhad.ads.sdk.interfaces.ServiceBridge;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by chengsiy on 2015/5/11.
 */
public final class QhAdService extends Service {

    private ServiceBridge serviceBridge;

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBridge.onBind(intent);
    }

    @Override
    public void onCreate() {
        serviceBridge = new QhAdServiceBridge(this);
        serviceBridge.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return serviceBridge.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        serviceBridge.onDestroy();
        serviceBridge = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        serviceBridge.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        serviceBridge.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        serviceBridge.onTrimMemory(level);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return serviceBridge.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        serviceBridge.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        serviceBridge.onTaskRemoved(rootIntent);
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        serviceBridge.dump(fd, writer, args);
    }
}
