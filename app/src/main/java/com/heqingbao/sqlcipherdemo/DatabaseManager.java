package com.heqingbao.sqlcipherdemo;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by heqingbao on 2016/8/26.
 */
public class DatabaseManager {

    private static SQLiteOpenHelper helper;
    private static DatabaseManager instance;

    private SQLiteDatabase db;

    private int openCounter;

    public static synchronized void init(SQLiteOpenHelper h) {
        if (instance == null) {
            instance = new DatabaseManager();
            helper = h;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call init(..) method first.");
        }
        return instance;
    }

    public synchronized SQLiteDatabase getDB() {
        openCounter++;
        if (openCounter == 1) {
            db = helper.getWritableDatabase();
        }
        return db;
    }

    public synchronized void closeDB() {
        openCounter--;
        if (openCounter == 0) {
            db.close();
        }
    }
}
