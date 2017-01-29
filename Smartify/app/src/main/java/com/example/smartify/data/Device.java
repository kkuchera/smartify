package com.example.smartify.data;

import java.util.Objects;

/**
 * Immutable model class for a Device.
 */
public final class Device {

    private final int mId;
    private final String mName;
    private final String mDeviceId;
    private final String mAccessToken;
    private final int mRelayNumber;
    private final double mLatitude;
    private final double mLongitude;
    private final boolean mAutoAction;
    private final boolean mOpened;

    public Device(int id, String name, String deviceId, String accessToken, int relayNumber,
                  double latitude, double longitude, boolean autoAction, boolean opened) {
        mId = id;
        mName = name;
        mDeviceId = deviceId;
        mAccessToken = accessToken;
        mRelayNumber = relayNumber;
        mLatitude = latitude;
        mLongitude = longitude;
        mAutoAction = autoAction;
        mOpened = opened;
    }

    public Device(String name, String deviceId, String accessToken, int relayNumber, double
            latitude, double longitude, boolean autoAction, boolean opened) {
        this(-1, name, deviceId, accessToken, relayNumber, latitude, longitude, autoAction, opened);
    }

    public int getId() {
        if (mId == -1) {
            throw new IllegalArgumentException("No id was set.");
        }
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public int getRelayNumber() {
        return mRelayNumber;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public boolean getAutoAction() {
        return mAutoAction;
    }

    public boolean isOpened() {
        return mOpened;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(mId, device.mId) &&
                Objects.equals(mName, device.mName) &&
                Objects.equals(mDeviceId, device.mDeviceId) &&
                Objects.equals(mAccessToken, device.mAccessToken) &&
                Objects.equals(mRelayNumber, device.mRelayNumber) &&
                Objects.equals(mLatitude, device.mLatitude) &&
                Objects.equals(mLongitude, device.mLongitude) &&
                Objects.equals(mAutoAction, device.mAutoAction) &&
                Objects.equals(mOpened, device.mOpened);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId, mName, mDeviceId, mAccessToken, mRelayNumber, mLatitude,
                mLongitude, mAutoAction, mOpened);
    }
}
