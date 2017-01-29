package com.example.smartify.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Class that defines the database schemas.
 */
abstract class DatabaseContract {

    static final String AUTHORITY = "com.example.smartify.data.devicescontentprovider";

    // TODO differentiate between devices and relays on devices
    static abstract class Device implements BaseColumns {
        private static final String BASE_PATH = "devices";
        static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
                + "/" + BASE_PATH);
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + BASE_PATH;
        static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/" + BASE_PATH;

        static final String TABLE_NAME = "device";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_DEVICE_ID = "device_id";
        static final String COLUMN_NAME_ACCESS_TOKEN = "access_token";
        static final String COLUMN_NAME_RELAY_NUMBER = "relay_number";
        static final String COLUMN_NAME_LATITUDE = "latitude";
        static final String COLUMN_NAME_LONGITUDE = "longitude";
        static final String COLUMN_NAME_AUTO_ACTION = "auto_action";
        static final String COLUMN_NAME_OPENED = "opened";
    }
}
