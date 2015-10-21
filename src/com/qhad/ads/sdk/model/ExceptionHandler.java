package com.qhad.ads.sdk.model;

import android.content.Context;
import android.os.Handler;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.MessageConfig;
import com.qhad.ads.sdk.res.StaticConfig;
import com.qhad.ads.sdk.res.SwitchConfig;
import com.qhad.ads.sdk.task.NetsTask;
import com.qhad.ads.sdk.utils.LocalFileManager;
import com.qhad.ads.sdk.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

public class ExceptionHandler {

    private static Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MessageConfig.CRASH_INFO) {
                QHADLog.d("上传崩溃日志完成");
                LocalFileManager.deleteFile(QhAdModel.getInstance().getContext().getFilesDir() + "/" + StaticConfig.CRASH_LOG_FILE_NAME);
            }
        }

    };
    private Thread.UncaughtExceptionHandler exceptionHandler = null;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context context = null;

    public ExceptionHandler(Context _context) {
        context = _context;
        postError2Server();

        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (SwitchConfig.CRASH) {
            if (exceptionHandler == null) {
                exceptionHandler = new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {
                        if (!handlerException(thread, ex) && mDefaultHandler != null) {
                            mDefaultHandler.uncaughtException(thread, ex);
                        } else {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    }
                };
                Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
            } else {
                return;
            }
        }
    }

    private Boolean postError2Server() {
        try {
            String error = LocalFileManager.readFile(StaticConfig.CRASH_LOG_FILE_NAME, context);
            String[] errors = error.split("\n");
            if (errors.length > 3) {
                new Thread(new RunablePostdata(errors, handler)).start();
            }
        } catch (Exception e) {
            QHADLog.e("发回错误日志错误: Error=" + e.getMessage());
        }
        return false;
    }

    private Boolean handlerException(Thread thread, Throwable ex) {
        try {
            QHADLog.e("Crash Error，thread id:" + thread.getId());
            QHADLog.e("Crash Error，thread name:" + thread.getName());
            QHADLog.e("Crash Error，thread net:" + Utils.getCurrentNetWorkInfo());
            QHADLog.e("Crash Error，thread info:" + ex.getLocalizedMessage());
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            StackTraceElement[] trace = ex.getStackTrace();
            StackTraceElement[] trace2 = new StackTraceElement[trace.length + 3];
            System.arraycopy(trace, 0, trace2, 0, trace.length);
            trace2[trace.length + 0] = new StackTraceElement("Android", "MODEL", android.os.Build.MODEL, -1);
            trace2[trace.length + 1] = new StackTraceElement("Android", "VERSION", android.os.Build.VERSION.RELEASE, -1);
            ex.setStackTrace(trace2);
            ex.printStackTrace(printWriter);
            String stacktrace = result.toString();
            printWriter.close();
            QHADLog.e(stacktrace);

            JSONObject obj = new JSONObject();
            obj.put("thread_id", thread.getId());
            obj.put("thread_name", thread.getName());
            obj.put("time", System.currentTimeMillis());
            obj.put("net", Utils.getCurrentNetWorkInfo());
            obj.put("throwable_msg", ex.getLocalizedMessage());

            LocalFileManager.writeAppendFile(StaticConfig.CRASH_LOG_FILE_NAME, obj.toString() + "\n", context);
            return true;
        } catch (Exception e) {
            QHADLog.e("崩溃日志:写入失败 Error=" + e.getMessage());
        }
        return false;
    }

    class RunablePostdata implements Runnable {

        private Handler handler = null;
        private String[] errors = null;

        public RunablePostdata(String[] _errors, Handler _handler) {
            handler = _handler;
            errors = _errors;
        }

        @Override
        public void run() {
            try {
                JSONArray array = new JSONArray();
                JSONObject obj = new JSONObject();
                for (String error : errors) {
                    JSONObject jsonObject = new JSONObject(error);
                    array.put(jsonObject);
                }

                obj.put("info", array);
                JSONObject deviceObj = new JSONObject();
                deviceObj.put("os", Utils.getSysteminfo());
                deviceObj.put("imei", Utils.getIMEI());
                deviceObj.put("imsi", Utils.getIMSI());
                deviceObj.put("mac", Utils.getMac());
                deviceObj.put("model", Utils.getProductModel());
                deviceObj.put("appv", Utils.getAppVersion());
                deviceObj.put("appname", Utils.getAppname());
                deviceObj.put("apppkg", Utils.getAppPackageName());
                deviceObj.put("longitude", StaticConfig.longitude);
                deviceObj.put("latitude", StaticConfig.latitude);
                deviceObj.put("sdkv", StaticConfig.SDK_VERSION);
                deviceObj.put("androidid", Utils.getAndroidid());
                obj.put("deviceinfo", deviceObj);

                HashMap<String, String> param = new HashMap<String, String>();
                param.put("crash", obj.toString());
                NetsTask.postData(StaticConfig.CRASH_LOG_URL, param, handler, MessageConfig.CRASH_INFO);
            } catch (Exception e) {
                QHADLog.e(e.getMessage());
            }
        }
    }
}
