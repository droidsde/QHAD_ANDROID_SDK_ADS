package com.qhad.ads.sdk.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qhad.ads.sdk.vo.CommonAdVO;

import java.util.HashMap;
import java.util.Map;

public class DownloadDBManager {
    private DownloadDBHelper dbhelper;
    private SQLiteDatabase db;

    public DownloadDBManager(Context context) {
        if (dbhelper == null) {
            dbhelper = new DownloadDBHelper(context);

            db = dbhelper.getWritableDatabase();
        }
    }

    public void createDownload(CommonAdVO vo, long downloadid) {
        ContentValues values = new ContentValues();
        values.put("downloadid", downloadid);
        values.put("url", vo.ld);
        values.put("pkg", vo.pkg);
        values.put("impid", vo.impid);
        values.put("clickid", vo.clickEventId);
        values.put("advertiserid", vo.advertiserid);
        values.put("campaignid", vo.campaignid);
        values.put("solutionid", vo.solutionid);
        values.put("bannerid", vo.bannerid);
        values.put("status", 0);
        values.put("createdtime", vo.downloadStartTime);

        db.insert(DownloadDBHelper.DB_TABLE_NAME, null, values);
    }

    public Map<Long, Long> getDownloadIds() {
        Map<Long, Long> idMap = new HashMap<>();
        Cursor cursor = db.rawQuery(String.format("select downloadid,createdtime from %s", DownloadDBHelper.DB_TABLE_NAME), null);
        int downloadColIdx = cursor.getColumnIndex("downloadid");
        int createdTimeColIdx = cursor.getColumnIndex("createdtime");
        while (cursor.moveToNext()) {
            long id = cursor.getLong(downloadColIdx);
            long createdTime = cursor.getLong(createdTimeColIdx);
            idMap.put(id, createdTime);
        }
        cursor.close();
        return idMap;
    }

    public CommonAdVO getDownloadInfo(long id) {
        Cursor cursor = db.rawQuery("select * from " + DownloadDBHelper.DB_TABLE_NAME + " where downloadid=?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            CommonAdVO vo = new CommonAdVO();
            vo.ld = cursor.getString(cursor.getColumnIndex("url"));
            vo.pkg = cursor.getString(cursor.getColumnIndex("pkg"));
            vo.impid = cursor.getString(cursor.getColumnIndex("impid"));
            vo.clickEventId = cursor.getString(cursor.getColumnIndex("clickid"));
            vo.downloadStartTime = cursor.getLong(cursor.getColumnIndex("createdtime"));
            vo.advertiserid = cursor.getString(cursor.getColumnIndex("advertiserid"));
            vo.campaignid = cursor.getString(cursor.getColumnIndex("campaignid"));
            vo.solutionid = cursor.getString(cursor.getColumnIndex("solutionid"));
            vo.bannerid = cursor.getString(cursor.getColumnIndex("bannerid"));
            cursor.close();
            return vo;
        }
        cursor.close();
        return null;
    }

    public long getDownloadId(String pkg) {
        Cursor cursor = db.rawQuery("select * from " + DownloadDBHelper.DB_TABLE_NAME + " where pkg=?", new String[]{pkg});
        if (cursor.moveToFirst()) {
            long downloadid = cursor.getLong(cursor.getColumnIndex("downloadid"));
            cursor.close();
            return downloadid;
        }
        cursor.close();
        return 0;
    }

    public void deleteDownload(long id) {
        db.delete(DownloadDBHelper.DB_TABLE_NAME, "downloadid=?", new String[]{String.valueOf(id)});
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
