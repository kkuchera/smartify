package com.example.smartify.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.smartify.geofence.GeofenceIntentService;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that provides an interface to access the device data while making abstraction of where and
 * how it is stored.
 */
class DevicesServiceApiImpl implements DevicesServiceApi {

    /**
     * Reference to the context of this application.
     */
    private final Context mContext;

    /**
     * Construct an instance to access the lock data repository.
     *
     * @param context The context of this application.
     */
    public DevicesServiceApiImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public Device getDevice(int id) {
        String[] projection = {
                DatabaseContract.Device._ID,
                DatabaseContract.Device.COLUMN_NAME_NAME,
                DatabaseContract.Device.COLUMN_NAME_DEVICE_ID,
                DatabaseContract.Device.COLUMN_NAME_ACCESS_TOKEN,
                DatabaseContract.Device.COLUMN_NAME_RELAY_NUMBER,
                DatabaseContract.Device.COLUMN_NAME_LATITUDE,
                DatabaseContract.Device.COLUMN_NAME_LONGITUDE,
                DatabaseContract.Device.COLUMN_NAME_AUTO_ACTION,
                DatabaseContract.Device.COLUMN_NAME_OPENED,
        };
        Uri uri = DatabaseContract.Device.CONTENT_URI.buildUpon().appendPath(String.valueOf(id))
                .build();
        final Cursor c = mContext.getContentResolver().query(uri, projection, null, null, null);
        if (c != null && c.moveToFirst()) {
            final int cId = c.getInt(0);
            final String name = c.getString(1);
            final String deviceId = c.getString(2);
            final String accessToken = c.getString(3);
            final int relayNumber = c.getInt(4);
            final double latitude = c.getDouble(5);
            final double longitude = c.getDouble(6);
            final boolean autoAction = c.getInt(7) == 1;
            final boolean opened = c.getInt(8) == 1;
            c.close();
            return new Device(cId, name, deviceId, accessToken, relayNumber,
                    latitude,
                    longitude,
                    autoAction,
                    opened);
        } else {
            throw new RuntimeException("Couldn't move cursor to first position.");
        }
    }

    @Override
    public List<Device> getAllDevices() {
        ArrayList<Device> devices = new ArrayList<>();
        String[] projection = {
                DatabaseContract.Device._ID,
                DatabaseContract.Device.COLUMN_NAME_NAME,
                DatabaseContract.Device.COLUMN_NAME_DEVICE_ID,
                DatabaseContract.Device.COLUMN_NAME_ACCESS_TOKEN,
                DatabaseContract.Device.COLUMN_NAME_RELAY_NUMBER,
                DatabaseContract.Device.COLUMN_NAME_LATITUDE,
                DatabaseContract.Device.COLUMN_NAME_LONGITUDE,
                DatabaseContract.Device.COLUMN_NAME_AUTO_ACTION,
                DatabaseContract.Device.COLUMN_NAME_OPENED,
        };
        final Cursor c = mContext.getContentResolver().query(DatabaseContract.Device.CONTENT_URI,
                projection, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                final int cId = c.getInt(0);
                final String name = c.getString(1);
                final String deviceId = c.getString(2);
                final String accessToken = c.getString(3);
                final int relayNumber = c.getInt(4);
                final double latitude = c.getDouble(5);
                final double longitude = c.getDouble(6);
                final boolean autoAction = c.getInt(7) == 1;
                final boolean opened = c.getInt(8) == 1;
                devices.add(new Device(cId, name, deviceId, accessToken,
                        relayNumber, latitude,
                        longitude, autoAction, opened));
            }
            c.close();
        } else {
            throw new RuntimeException("Couldn't move cursor to next position.");
        }
        return devices;

    }

    @Override
    public void addDevice(Device device) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_NAME, device.getName());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_DEVICE_ID, device.getDeviceId());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_ACCESS_TOKEN, device.getAccessToken());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_RELAY_NUMBER, device.getRelayNumber());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_LATITUDE, device.getLatitude());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_LONGITUDE, device.getLongitude());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_AUTO_ACTION, device.getAutoAction());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_OPENED, device.isOpened());
        mContext.getContentResolver().insert(DatabaseContract.Device.CONTENT_URI, contentValues);
        // TODO think about where to put this
        // Update geofence list if auto action is enabled.
        if (device.getAutoAction()) {
            GeofenceIntentService.startActionUpdateAllGeofences(mContext);
        }
    }

    @Override
    public void updateDevice(Device device) {
        Device oldDevice = getDevice(device.getId());

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_NAME, device.getName());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_DEVICE_ID, device.getDeviceId());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_ACCESS_TOKEN, device.getAccessToken());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_RELAY_NUMBER, device.getRelayNumber());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_LATITUDE, device.getLatitude());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_LONGITUDE, device.getLongitude());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_AUTO_ACTION, device.getAutoAction());
        contentValues.put(DatabaseContract.Device.COLUMN_NAME_OPENED, device.isOpened());
        Uri uri = DatabaseContract.Device.CONTENT_URI.buildUpon().appendPath(String.valueOf
                (device.getId())).build();
        mContext.getContentResolver().update(uri, contentValues, null, null);

        //TODO think about where to put this
        // Update geofence list if auto action has changed
        if (oldDevice.getAutoAction() != device.getAutoAction()) {
            GeofenceIntentService.startActionUpdateAllGeofences(mContext);
        }
    }

    @Override
    public void removeDevice(int id) {
        Uri uri = DatabaseContract.Device.CONTENT_URI.buildUpon().appendPath(String.valueOf(id))
                .build();
        mContext.getContentResolver().delete(uri, null, null);
        //TODO think about where to put this
        GeofenceIntentService.startActionUpdateAllGeofences(mContext);
    }

}
