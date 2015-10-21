package com.qhad.ads.sdk.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Duan
 */
public class DownloadDBHelper extends SQLiteOpenHelper {

    public final static String DB_TABLE_NAME = "downloadinfo";
    private static final String DB_NAME = "qh_ad_db.db";
    private static final int version = 3; //数据库版本
    private final String DB_DOWNLOAD_INFO_CREATE = "" +
            "CREATE TABLE IF NOT EXISTS " + DB_TABLE_NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,url TEXT,downloadid INTEGER,pkg TEXT,impid TEXT,clickid TEXT, advertiserid TEXT, campaignid TEXT, solutionid TEXT, bannerid TEXT, status INTEGER default 0,createdtime INTEGER);" +
            "CREATE INDEX IF NOT EXISTS index_downloadid on  " + DB_TABLE_NAME + " (downloadid);" +
            "CREATE INDEX IF NOT EXISTS index_pkg on  " + DB_TABLE_NAME + " (pkg);";


    public DownloadDBHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_DOWNLOAD_INFO_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < version) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        }
        onCreate(db);
    }
}