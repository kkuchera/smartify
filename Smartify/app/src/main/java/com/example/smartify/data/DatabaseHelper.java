package com.example.smartify.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class to manage the creation and versioning of the database.
 */
class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "smartify.db";

    private static final String SQL_CREATE_DEVICE = "CREATE TABLE " + DatabaseContract.Device
            .TABLE_NAME + " (" +
            DatabaseContract.Device._ID + " INTEGER PRIMARY KEY, " +
            DatabaseContract.Device.COLUMN_NAME_NAME + " TEXT, " +
            DatabaseContract.Device.COLUMN_NAME_DEVICE_ID + " TEXT, " +
            DatabaseContract.Device.COLUMN_NAME_ACCESS_TOKEN + " TEXT, " +
            DatabaseContract.Device.COLUMN_NAME_RELAY_NUMBER + " INTEGER, " +
            DatabaseContract.Device.COLUMN_NAME_LATITUDE + " REAL, " +
            DatabaseContract.Device.COLUMN_NAME_LONGITUDE + " REAL, " +
            DatabaseContract.Device.COLUMN_NAME_AUTO_ACTION + " INTEGER, " +
            DatabaseContract.Device.COLUMN_NAME_OPENED + " INTEGER " + ")";

    private static final String SQL_DELETE_DEVICE =
            "DROP TABLE IF EXISTS " + DatabaseContract.Device.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DEVICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_DEVICE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
