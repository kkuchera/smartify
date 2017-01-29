package com.example.smartify.data;


import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

/**
 * Provides loaders so the activities can load data asynchronously
 */
public class LoaderProvider {

    private final Context mContext;

    /**
     * Construct a new loader provider.
     *
     * @param context The application's context.
     */
    public LoaderProvider(Context context) {
        mContext = context;
    }

    /**
     * Return a CursorLoader that queries the database for all existing devices and returns their
     * id, name, agent URL and API key.
     *
     * @return CursorLoader to query the devices.
     */
    public Loader<Cursor> getDevicesCursorLoader() {
        String[] projection = {
                DatabaseContract.Device._ID,
                DatabaseContract.Device.COLUMN_NAME_NAME,
                DatabaseContract.Device.COLUMN_NAME_OPENED,
        };
        final String orderBy = DatabaseContract.Device.COLUMN_NAME_NAME + " ASC";
        return new CursorLoader(mContext, DatabaseContract.Device.CONTENT_URI, projection, null,
                null, orderBy);
    }

    /**
     * Return a CursorLoader that queries the database device with given id and returns its name,
     * agent URL and API key.
     *
     * @param id Id of the device for which to query the data
     * @return CursorLoader to query the devices.
     */
    public Loader<Cursor> getDeviceDetailCursorLoader(int id) {
        String[] projection = {
                DatabaseContract.Device.COLUMN_NAME_NAME,
                DatabaseContract.Device.COLUMN_NAME_DEVICE_ID,
                DatabaseContract.Device.COLUMN_NAME_ACCESS_TOKEN,
                DatabaseContract.Device.COLUMN_NAME_RELAY_NUMBER,
                DatabaseContract.Device.COLUMN_NAME_LATITUDE,
                DatabaseContract.Device.COLUMN_NAME_LONGITUDE,
                DatabaseContract.Device.COLUMN_NAME_AUTO_ACTION,
        };
        Uri uri = DatabaseContract.Device.CONTENT_URI.buildUpon().appendPath(Integer.toString(id))
                .build();
        return new CursorLoader(mContext, uri, projection, null, null, null);
    }

}
