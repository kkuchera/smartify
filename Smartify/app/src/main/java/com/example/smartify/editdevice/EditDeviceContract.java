package com.example.smartify.editdevice;

/**
 * This specifies the contract between the view and the presenter.
 */
class EditDeviceContract {

    interface View {

        void clearUIErrors();

        void nameRequired();

        void deviceIdRequired();

        void accessTokenRequired();

        void relayNumberRequired();

        void latitudeRequired();

        void longitudeRequired();

        void deviceIdInvalid();

        void accessTokenInvalid();

        void relayNumberInvalid();

        void openDevices();

    }

    interface UserActionsListener {


        void attemptAddDevice(String name, String deviceId, String accessToken, String
                relayNumber, String latitude, String longitude, boolean autoAction);

        void attemptUpdateDevice(int id, String name, String deviceId, String accessToken, String
                relayNumber, String latitude, String longitude, boolean autoAction);

    }

}
