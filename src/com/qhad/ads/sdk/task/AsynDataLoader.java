package com.qhad.ads.sdk.task;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.qhad.ads.sdk.task.AsynDataLoader.Listener;
import com.qhad.ads.sdk.utils.Utils;
import com.qhad.ads.sdk.vo.CommonAdVO;

import java.util.ArrayList;
import java.util.HashMap;

public class AsynDataLoader {

    private static Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            handler.post(new ResultRunable(msg));
        }

    };

    public AsynDataLoader(String url, Listener listener) {
        DataLoaderRunable dataLoaderRunable = new DataLoaderRunable(url, handler, listener);
        Thread thread = new Thread(dataLoaderRunable);
        thread.start();
    }

    public interface Listener {
        void onGetDataSucceed(ArrayList<CommonAdVO> vos);

        void onGetDataSucceed(CommonAdVO vo);

        void onGetDataFailed(String error);
    }
}

class DataLoaderRunable implements Runnable {

    private String url = null;
    private Handler handler = null;
    private Listener listener = null;

    public DataLoaderRunable(String _url, Handler _handler, Listener _listener) {
        this.url = _url;
        this.handler = _handler;
        this.listener = _listener;
    }

    @Override
    public void run() {
        Message msg = new Message();
        HashMap<String, Object> obj = new HashMap<String, Object>();
        obj.put("listener", listener);
        msg.obj = obj;

        //获取数据
        String data = NetsTask.getAdData(url);
        if (data == null) {
            msg.what = 0;
            handler.dispatchMessage(msg);
//            QHADLog.e(QhAdErrorCode.REQUEST_FAILED_ERROR, "ad request data is null");
            return;
        }

        if (data.startsWith("[")) {
            ArrayList<CommonAdVO> vos = ParsedataTask.parseCommonDataSet(data);
            if (vos == null || vos.isEmpty()) {
                msg.what = 0;
                handler.dispatchMessage(msg);
                return;
            }

            msg.what = 2;
            obj.put("vo", vos);
            handler.dispatchMessage(msg);
            return;
        } else {
            //解析数据
            CommonAdVO vo = ParsedataTask.parseCommonData(data);
            if (vo == null || vo.adm == null) {
                msg.what = 0;
                handler.dispatchMessage(msg);
                return;
            }

            //解析资源
            switch (vo.adm_type) {
                case IMAGE: //静态资源
                    Bitmap bmp = NetsTask.getAdresource(vo.adm, Utils.getContext());
                    if (bmp == null) {
                        msg.what = 0;
                        handler.dispatchMessage(msg);
                        return;
                    } else {
                        vo.bmp = bmp;
                        msg.what = 1;
                        obj.put("vo", vo);
                        handler.dispatchMessage(msg);
                        return;
                    }

                case MRAID: //MRAID资源
                    String htmlString = ParsedataTask.checkHtmlData(vo.adm);
                    if (htmlString == null) {
                        msg.what = 0;
                        handler.dispatchMessage(msg);
                        return;
                    } else {
                        vo.html = htmlString;
                        msg.what = 1;
                        obj.put("vo", vo);
                        handler.dispatchMessage(msg);
                        return;
                    }

                case DSP_HTML5:
                    String html5String = ParsedataTask.checkHtmlData(vo.adm);
                    if (html5String == null) {
                        msg.what = 0;
                        handler.dispatchMessage(msg);
                        return;
                    } else {
                        vo.html = html5String;
                        msg.what = 1;
                        obj.put("vo", vo);
                        handler.dispatchMessage(msg);
                        return;
                    }

                case VIDEO: //静态资源
                    msg.what = 1;
                    obj.put("vo", vo);
                    handler.dispatchMessage(msg);
                    return;

                case UNKOWN: //无法识别资源类型
                    msg.what = 0;
                    handler.dispatchMessage(msg);
                    return;

                default:
                    msg.what = 0;
                    handler.sendMessage(msg);
                    return;
            }

        }


    }
}

class ResultRunable implements Runnable {
    private Message msg = null;

    public ResultRunable(Message _msg) {
        msg = _msg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        HashMap<String, Object> data = (HashMap<String, Object>) msg.obj;
        Listener listener = (Listener) data.get("listener");
        if (msg.what == 0) {
            listener.onGetDataFailed("Error");
        } else if (msg.what == 1) {
            CommonAdVO vo = (CommonAdVO) data.get("vo");
            listener.onGetDataSucceed(vo);
        } else if (msg.what == 2) {
            ArrayList<CommonAdVO> vos = (ArrayList<CommonAdVO>) data.get("vo");
            listener.onGetDataSucceed(vos);
        }
    }
}