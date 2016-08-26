package com.heqingbao.sqlcipherdemo;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * Created by heqingbao on 2016/8/26.
 */
public class CipherDatabaseManager {

    private static SQLiteOpenHelper helper;
    private static CipherDatabaseManager instance;

    private SQLiteDatabase db;

    private static final String KYE = "test";

    private int openCounter;

    public static synchronized void init(SQLiteOpenHelper h) {
        if (instance == null) {
            instance = new CipherDatabaseManager();
            helper = h;
        }
    }

    public static synchronized CipherDatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(CipherDatabaseManager.class.getSimpleName() +
                    " is not initialized, call init(..) method first.");
        }
        return instance;
    }

    public synchronized SQLiteDatabase getDB() {
        openCounter++;
        if (openCounter == 1) {
            db = helper.getWritableDatabase(KYE);
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
