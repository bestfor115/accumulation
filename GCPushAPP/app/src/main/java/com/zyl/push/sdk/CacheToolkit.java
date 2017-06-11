/**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.sdk;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class CacheToolkit extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CIM_CONFIG_INFO.db";
    private static final int DATABASE_VERSION = 20160406;

    private static final String TABLE_SQL = "CREATE TABLE IF NOT EXISTS T_CIM_CONFIG (KEY VARCHAR(64) PRIMARY KEY,VALUE TEXT)";

    private static final String DELETE_SQL = "DELETE FROM T_CIM_CONFIG WHERE KEY = ?";

    private static final String SAVE_SQL = "INSERT INTO T_CIM_CONFIG (KEY,VALUE) VALUES(?,?)";

    private static final String QUERY_SQL = "SELECT VALUE FROM T_CIM_CONFIG WHERE KEY = ?";

    public static final String CIM_CONFIG_INFO = "CIM_CONFIG_INFO";

    public static final String KEY_ACCOUNT = "KEY_ACCOUNT";

    public static final String KEY_MANUAL_STOP = "KEY_MANUAL_STOP";

    public static final String KEY_CIM_DESTROYED = "KEY_CIM_DESTROYED";

    public static final String KEY_CIM_SERVIER_HOST = "KEY_CIM_SERVIER_HOST";

    public static final String KEY_CIM_SERVIER_PORT = "KEY_CIM_SERVIER_PORT";

    public static final String KEY_CIM_CONNECTION_STATE = "KEY_CIM_CONNECTION_STATE";

    static CacheToolkit mToolkit;

    public static CacheToolkit getInstance(Context context) {
        if (mToolkit == null) {
            mToolkit = new CacheToolkit(context.getApplicationContext());
        }
        return mToolkit;
    }

    public CacheToolkit(Context context, String name, SQLiteDatabase.CursorFactory factory,
            int version) {
        super(context, name, factory, version);
    }

    public CacheToolkit(Context context) {
        this(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void remove(String key) {
        SQLiteDatabase database = mToolkit.getWritableDatabase();
        database.execSQL(DELETE_SQL, new String[] { key });
        database.close();
        mToolkit.close();
        mToolkit = null;
    }

    public void putString(String key, String value) {
        SQLiteDatabase database = mToolkit.getWritableDatabase();
        database.execSQL(DELETE_SQL, new String[] { key });
        database.execSQL(SAVE_SQL, new String[] { key, value });
        database.close();

        mToolkit.close();
        mToolkit = null;

    }

    public String getString(String key) {
        String value = null;
        SQLiteDatabase database = mToolkit.getWritableDatabase();
        Cursor cursor = database.rawQuery(QUERY_SQL, new String[] { key });
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getString(0);
        }

        cursor.close();
        database.close();
        mToolkit.close();
        mToolkit = null;

        return value;
    }

    public void putBoolean(String key, boolean value) {
        putString(key, Boolean.toString(value));
    }

    public boolean getBoolean(String key) {
        String value = getString(key);
        return value == null ? false : Boolean.parseBoolean(value);
    }

    public void putInt(String key, int value) {
        putString(key, String.valueOf(value));
    }

    public int getInt(String key) {
        String value = getString(key);
        return value == null ? 0 : Integer.parseInt(value);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

    }
}
