package com.example.smartify.data;

import java.net.URL;

/**
 * Defines an interface to the REST service API. All data request to the REST API should be piped
 * through this interface.
 */
interface RestServiceApi {

    interface RestServiceCallback {
        void onResult(boolean success, boolean value);
    }

    void get(URL url, String accessToken, RestServiceCallback callback);

    void post(URL url, String accessToken, String arg);

}
