package com.example.smartify.data;

import java.util.List;

/**
 * Main entry point for accessing devices data.
 */
public interface DevicesRepository {

    List<Device> getAllDevices();

    void addDevice(Device device);

    void updateDevice(Device device);

    void removeDevice(int id);

    void open(int id);

    void close(int id);

    void loadAllDeviceStatuses();

}
