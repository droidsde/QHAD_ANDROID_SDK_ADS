package com.qhad.ads.sdk.interfaces;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import java.io.FileDescriptor;
import java.io.PrintWriter;

public interface ServiceBridge {
    IBinder onBind(Intent intent);

    void onCreate();

    void onDestroy();

    void onConfigurationChanged(Configuration newConfig);

    void onLowMemory();

    void onTrimMemory(int level);

    boolean onUnbind(Intent intent);

    void onRebind(Intent intent);

    void onTaskRemoved(Intent rootIntent);

    void dump(FileDescriptor fd, PrintWriter writer, String[] args);

    int onStartCommand(Intent intent, int flags, int startId);
}
