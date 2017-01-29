package com.example.smartify.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Implementation of the devices repository used to access device data. This class makes
 * abstraction of service API used to retrieve data. Useful when adding e.g.
 * PreferencesServiceApi or RestServiceApi.
 */
class DevicesRepositoryImpl implements DevicesRepository {

    // TODO: Perform database access in background thread.

    private final DevicesServiceApi mDevicesServiceApi;
    private final RestServiceApi mRestServiceApi;

    DevicesRepositoryImpl(DevicesServiceApi devicesServiceApi, RestServiceApi
            restServiceApi) {
        mDevicesServiceApi = devicesServiceApi;
        mRestServiceApi = restServiceApi;
    }

    @Override
    public List<Device> getAllDevices() {
        return mDevicesServiceApi.getAllDevices();
    }

    @Override
    public void addDevice(Device device) {
        mDevicesServiceApi.addDevice(device);
    }

    @Override
    public void updateDevice(Device device) {
        mDevicesServiceApi.updateDevice(device);
    }

    @Override
    public void removeDevice(int id) {
        mDevicesServiceApi.removeDevice(id);
    }

    @Override
    public void open(int id) {
        final Device device = mDevicesServiceApi.getDevice(id);
        final String urlString =  "https://api.particle.io/v1/devices/" +
        device.getDeviceId() + "/open";
        final String arg = String.valueOf(device.getRelayNumber());
        try {
            final URL url = new URL(urlString);
            mRestServiceApi.post(url, device.getAccessToken(), arg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close(int id) {
        final Device device = mDevicesServiceApi.getDevice(id);
        final String urlString =  "https://api.particle.io/v1/devices/" +
                device.getDeviceId() + "/close";
        final String arg = String.valueOf(device.getRelayNumber());
        try {
            final URL url = new URL(urlString);
            mRestServiceApi.post(url, device.getAccessToken(), arg);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAllDeviceStatuses() {
        final List<Device> devices = mDevicesServiceApi.getAllDevices();
        for (final Device device: devices) {
            final String urlString =  "https://api.particle.io/v1/devices/" +
                    device.getDeviceId() + "/sensorvalue" + device.getRelayNumber();
            try {
                mRestServiceApi.get(new URL(urlString), device.getAccessToken(), new RestServiceApi.RestServiceCallback() {
                    @Override
                    public void onResult(boolean success, boolean opened) {
                        if (success) {
                            Device updatedDevice = new Device(device.getId(), device.getName(),
                                    device.getDeviceId(), device.getAccessToken(), device
                                    .getRelayNumber(), device.getLatitude(), device.getLongitude
                                    (), device.getAutoAction(), opened);
                            mDevicesServiceApi.updateDevice(updatedDevice);
                        }
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

}
