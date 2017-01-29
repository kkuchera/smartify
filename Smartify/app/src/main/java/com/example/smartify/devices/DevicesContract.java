package com.example.smartify.devices;

/**
 * This specifies the contract between the view and the presenter.
 */
class DevicesContract {

    interface View {

        void showAddDevice();

        void showDeviceDetail(int id, String name);

    }

    interface UserActionsListener {

        void addDevice();

        void openDeviceDetail(int id, String name);

        void close(int id);

        void open(int id);

        void deleteDevice(int id);

        void loadUI();

    }

}
