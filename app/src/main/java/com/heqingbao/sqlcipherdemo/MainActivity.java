package com.heqingbao.sqlcipherdemo;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final List<String> data = Arrays.asList("50", "100", "1000", "10000", "100000");

    private Button btnInsertNormal;
    private Button btnInsertCipher;

    private Button btnQueryNormal;
    private Button btnQueryCipher;

    private Button btnClearNormal;
    private Button btnClearCipher;

    private Spinner spinnerCount;
    private TextView logTextView;

    private int currentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInsertNormal = (Button) findViewById(R.id.btn_insert_normal);
        btnInsertCipher = (Button) findViewById(R.id.btn_insert_cipher);

        btnQueryNormal = (Button) findViewById(R.id.btn_query_normal);
        btnQueryCipher = (Button) findViewById(R.id.btn_query_cipher);

        btnClearNormal = (Button) findViewById(R.id.btn_clear_normal);
        btnClearCipher = (Button) findViewById(R.id.btn_clear_cipher);

        spinnerCount = (Spinner) findViewById(R.id.spinner_count);
        logTextView = (TextView) findViewById(R.id.tv_log);

        btnInsertNormal.setOnClickListener(this);
        btnQueryNormal.setOnClickListener(this);
        btnInsertCipher.setOnClickListener(this);
        btnQueryCipher.setOnClickListener(this);
        btnClearNormal.setOnClickListener(this);
        btnClearCipher.setOnClickListener(this);

        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinnerCount.setAdapter(adapter);

        spinnerCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCount = Integer.parseInt(data.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SQLiteDatabase.loadLibs(this);

        DatabaseManager.init(new DatabaseHelper(this));
        CipherDatabaseManager.init(new SQLCipherOpenHelper(this));
    }

    private void InitializeSQLCipher() {
//        SQLiteDatabase.loadLibs(this);
//        File databaseFile = getDatabasePath("demo.db");
//        databaseFile.mkdirs();
//        databaseFile.delete();
//        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "test123", null);
//        database.execSQL("create table t1(a, b)");
//        database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
//                "two for the show"});
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_insert_normal) {
            testBatchInsertByNormal();
        } else if (v.getId() == R.id.btn_query_normal) {
            testBatchQueryByNormal();
        } else if (v.getId() == R.id.btn_insert_cipher) {
            testBatchInsertByCipher();
        } else if (v.getId() == R.id.btn_query_cipher) {
            testBatchQueryByCipher();
        } else if (v.getId() == R.id.btn_clear_normal) {
            clearDbByNormal();
        } else if (v.getId() == R.id.btn_clear_cipher) {
            clearDbByCipher();
        }
    }

    // insert
    private void testBatchInsertByNormal() {
        List<Contact> cs = createTestData(currentCount);

        long start = System.nanoTime();
        android.database.sqlite.SQLiteDatabase db = DatabaseManager.getInstance().getDB();
        db.beginTransaction();
        try {
            for (Contact c : cs) {
                ContentValues values = new ContentValues();
                values.put("name", c.getName());
                values.put("phone_number", c.getPhoneNumber());
                db.insert("contacts", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        DatabaseManager.getInstance().closeDB();

        logTextView.append("\n（normal）插入" + currentCount + "条数据完成，耗时" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }

    private void testBatchInsertByCipher() {
        List<Contact> cs = createTestData(currentCount);

        long start = System.nanoTime();
        SQLiteDatabase db = CipherDatabaseManager.getInstance().getDB();
        db.beginTransaction();
        try {
            for (Contact c : cs) {
                ContentValues values = new ContentValues();
                values.put("name", c.getName());
                values.put("phone_number", c.getPhoneNumber());
                db.insert("contacts", null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        CipherDatabaseManager.getInstance().closeDB();

        logTextView.append("\n（SQLCipher）插入" + currentCount + "条数据完成，耗时" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }

    // query
    private void testBatchQueryByNormal() {
        long start = System.nanoTime();
        List<Contact> contacts = new ArrayList<>();
        android.database.sqlite.SQLiteDatabase db = DatabaseManager.getInstance().getDB();
        Cursor cursor = null;
        try {
            cursor = db.query("contacts", null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                contacts.add(contact);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        DatabaseManager.getInstance().closeDB();

        logTextView.append("\n（normal）查询" + contacts.size() + "条数据完成，耗时" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }

    private void testBatchQueryByCipher() {
        long start = System.nanoTime();
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = CipherDatabaseManager.getInstance().getDB();
        Cursor cursor = null;
        try {
            cursor = db.query("contacts", null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
                contacts.add(contact);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        CipherDatabaseManager.getInstance().closeDB();

        logTextView.append("\n（SQLCipher）查询" + contacts.size() + "条数据完成，耗时" + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) + "ms");
    }

    // clear
    private void clearDbByNormal() {
        android.database.sqlite.SQLiteDatabase db = DatabaseManager.getInstance().getDB();
        db.execSQL("delete from contacts");
        DatabaseManager.getInstance().closeDB();
    }

    private void clearDbByCipher() {
        SQLiteDatabase db = CipherDatabaseManager.getInstance().getDB();
        db.execSQL("delete from contacts");
        CipherDatabaseManager.getInstance().closeDB();
    }

    private List<Contact> createTestData(int count) {
        List<Contact> data = new ArrayList<>();
        for (int i = 0; i < count; i ++) {
            data.add(new Contact("Tom" + i, "010-" + i));
        }
        return data;
    }


}
