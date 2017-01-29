package com.example.smartify.data;

import java.util.List;

/**
 * Defines an interface to the local database service API. All data request to the local database
 * should be piped through this interface.
 */
interface DevicesServiceApi {

    Device getDevice(int id);

    List<Device> getAllDevices();

    void addDevice(Device device);

    void updateDevice(Device device);

    void removeDevice(int id);

}
