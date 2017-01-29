package com.example.smartify.devices;

import com.example.smartify.data.DevicesRepository;

/**
 * Listens to user actions from the UI ({@link DevicesActivity}), retrieves the data and updates the
 * UI as required.
 */
class DevicesPresenter implements DevicesContract.UserActionsListener {

    private final DevicesContract.View mView;
    private final DevicesRepository mRepository;

    DevicesPresenter(DevicesContract.View view, DevicesRepository devicesRepository) {
        mRepository = devicesRepository;
        mView = view;
    }

    @Override
    public void addDevice() {
        mView.showAddDevice();
    }

    @Override
    public void openDeviceDetail(int id, String name) {
        mView.showDeviceDetail(id, name);
    }

    @Override
    public void close(int id) {
        mRepository.close(id);
    }

    @Override
    public void open(int id) {
        mRepository.open(id);
    }

    @Override
    public void deleteDevice(int id) {
        mRepository.removeDevice(id);
    }

    @Override
    public void loadUI() {
        mRepository.loadAllDeviceStatuses();
    }
}