package com.example.smartify.editdevice;

import com.example.smartify.data.Device;
import com.example.smartify.data.DevicesRepository;

/**
 * Listens to user actions from the UI ({@link EditDeviceActivity}), retrieves the data and
 * updates the UI as required.
 */
class EditDevicePresenter implements EditDeviceContract.UserActionsListener {

    private final EditDeviceContract.View mView;
    private final DevicesRepository mRepository;

    EditDevicePresenter(EditDeviceContract.View view, DevicesRepository devicesRepository) {
        mView = view;
        mRepository = devicesRepository;
    }

    @Override
    public void attemptAddDevice(String name, String deviceId, String accessToken, String
            relayNumber, String latitude, String longitude, boolean autoAction) {
        if(validateInput(name, deviceId, accessToken, relayNumber, latitude, longitude)) {
            Device device = new Device(name, deviceId, accessToken,
                    Integer.valueOf(relayNumber),
                    Double.valueOf(latitude),
                    Double.valueOf(longitude),
                    autoAction, false);
            mRepository.addDevice(device);
            mView.openDevices();
        }
    }

    @Override
    public void attemptUpdateDevice(int id, String name, String deviceId, String accessToken, String
            relayNumber, String latitude, String longitude, boolean autoAction) {
        if (id <= 0) {
            throw new IllegalArgumentException("Id must be greater than 0.");
        }
        if(validateInput(name, deviceId, accessToken, relayNumber, latitude, longitude)) {
            Device device = new Device(id, name, deviceId, accessToken,
                    Integer.valueOf(relayNumber),
                    Double.valueOf(latitude),
                    Double.valueOf(longitude),
                    autoAction, false);
            mRepository.updateDevice(device);
            mView.openDevices();
        }
    }
    private boolean validateInput(String name, String deviceId, String accessToken,
                                 String relayNumber, String latitude, String longitude) {
        // Clear any previous errors
        mView.clearUIErrors();

        if (name.isEmpty()) {
            mView.nameRequired();
            return false;
        }
        if (deviceId.isEmpty()) {
            mView.deviceIdRequired();
            return false;
        }
        if (accessToken.isEmpty()) {
            mView.accessTokenRequired();
            return false;
        }
        if (relayNumber.isEmpty()) {
            mView.relayNumberRequired();
            return false;
        }
        if (latitude.isEmpty()) {
            mView.latitudeRequired();
            return false;
        }
        if (longitude.isEmpty()) {
            mView.longitudeRequired();
            return false;
        }
        if (!deviceId.matches("[0-9]+")){
            mView.deviceIdInvalid();
            return false;
        }
        if (!relayNumber.matches("[1-4]")) {
            mView.relayNumberInvalid();
            return false;
        }
        return true;
    }
}
