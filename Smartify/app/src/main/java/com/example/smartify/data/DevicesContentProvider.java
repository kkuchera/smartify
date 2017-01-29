package com.example.smartify.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Content provider for the database containing the device data.
 */
public class DevicesContentProvider extends ContentProvider {

    /**
     * URI types, each URI type (int) is matched to a URI.
     */
    private static final int DEVICES = 1;
    private static final int DEVICES_ID = 2;

    /**
     * URI matcher which matches URI's to their types (int).
     */
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * Provides access to the database.
     */
    private DatabaseHelper databaseHelper;


    /**
     * Match URI's with URI types.
     */
    static
    {
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, "devices", DEVICES);
        sURIMatcher.addURI(DatabaseContract.AUTHORITY, "devices/#", DEVICES_ID);
    }

    @Override
    public boolean onCreate() {
        databaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns(projection);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case DEVICES:
                queryBuilder.setTables(DatabaseContract.Device.TABLE_NAME);
                break;
            case DEVICES_ID:
                queryBuilder.setTables(DatabaseContract.Device.TABLE_NAME);
                queryBuilder.appendWhere(DatabaseContract.Device._ID + "=");
                queryBuilder.appendWhere(uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        Context context = getContext();
        assert context != null;
        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long id;
        switch (uriType) {
            case DEVICES:
                id = db.insert(DatabaseContract.Device.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Context context = getContext();
        assert context != null;
        context.getContentResolver().notifyChange(uri, null);
        return Uri.parse(uri + "/" + id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsDeleted;
        switch (uriType) {
            case DEVICES:
                rowsDeleted = db.delete(DatabaseContract.Device.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case DEVICES_ID: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(DatabaseContract.Device.TABLE_NAME,
                            DatabaseContract.Device._ID + "=" + id, null);
                } else {
                    rowsDeleted = db.delete(DatabaseContract.Device.TABLE_NAME,
                            DatabaseContract.Device._ID + "=" + id + " AND " + selection,
                            selectionArgs);
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Context context = getContext();
        assert context != null;
        context.getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int rowsUpdated;
        switch (uriType) {
            case DEVICES:
                rowsUpdated = db.update(DatabaseContract.Device.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case DEVICES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(DatabaseContract.Device.TABLE_NAME,
                            values,
                            DatabaseContract.Device._ID+ "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(DatabaseContract.Device.TABLE_NAME,
                            values,
                            DatabaseContract.Device._ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Context context = getContext();
        assert context != null;
        context.getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {  DatabaseContract.Device._ID,
                DatabaseContract.Device.COLUMN_NAME_NAME,
                DatabaseContract.Device.COLUMN_NAME_DEVICE_ID,
                DatabaseContract.Device.COLUMN_NAME_ACCESS_TOKEN,
                DatabaseContract.Device.COLUMN_NAME_RELAY_NUMBER,
                DatabaseContract.Device.COLUMN_NAME_LATITUDE,
                DatabaseContract.Device.COLUMN_NAME_LONGITUDE,
                DatabaseContract.Device.COLUMN_NAME_AUTO_ACTION,
                DatabaseContract.Device.COLUMN_NAME_OPENED};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
