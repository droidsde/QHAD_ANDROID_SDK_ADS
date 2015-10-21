package com.qhad.ads.test;

import com.qhad.ads.sdk.logs.QHADLog;
import com.qhad.ads.sdk.res.StaticConfig;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class FeedsLoader {

    private static final FeedsLoader ISNTANCE = new FeedsLoader();
    private ArrayList<MyListItem> items = new ArrayList<MyListItem>();
    private ArrayList<MListItem> mitems = new ArrayList<MListItem>();
    private ArrayList<AMyListItem> mmitems = new ArrayList<AMyListItem>();
    private ScheduledExecutorService executor = Executors
            .newSingleThreadScheduledExecutor();
    private AtomicInteger id = new AtomicInteger();

    private FeedsLoader() {
    }

    public static final FeedsLoader getInstance() {
        return ISNTANCE;
    }

    public void load(final FeedsLoaderCallback callback) {
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.ifanr.com/feed");
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setConnectTimeout(StaticConfig.NET_TIMEOUT);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream is = conn.getInputStream();

                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        // 2.创建解析器
                        SAXParser parser = factory.newSAXParser();
                        // 3.获取需要解析的文档，生成解析器,最后解析文档
                        FeedParser dh = new FeedParser();
                        parser.parse(is, dh);
                        ArrayList<HashMap> maps = dh.maps;

                        for (HashMap hashMap : maps) {
                            MyListItem item = new MyListItem();
                            MListItem mitem = new MListItem();
                            AMyListItem mmitem = new AMyListItem();

                            item.title = (String) hashMap.get("title");
                            item.logo = (String) hashMap.get("image");
                            item.desc = (String) hashMap.get("description");
                            item.link = (String) hashMap.get("link");

                            mitem.title = (String) hashMap.get("title");
                            mitem.logo = (String) hashMap.get("image");
                            mitem.desc = (String) hashMap.get("description");
                            mitem.link = (String) hashMap.get("link");

                            mmitem.title = (String) hashMap.get("title");
                            mmitem.logo = (String) hashMap.get("image");
                            mmitem.desc = (String) hashMap.get("description");
                            mmitem.link = (String) hashMap.get("link");

                            items.add(item);
                            mitems.add(mitem);
                            mmitems.add(mmitem);
                        }

                        is.close();
                        QHADLog.d("获取数据:完成");
                        conn.disconnect();
                    } else {
                        QHADLog.d("获取数据: HttpResponseCode:"
                                + conn.getResponseCode());
                        conn.disconnect();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                callback.onLoaded(items);
                callback.duringLoaded(mmitems);
                callback.whenLoaded(mitems);
            }
        }, (long) (Math.random() * 500 + 300), TimeUnit.MILLISECONDS);
    }

}
