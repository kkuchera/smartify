package com.example.smartify.data;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import java.net.URL;

/**
 * Class that provides an interface to access the REST API while making abstraction of where and
 * how it is stored.
 */
class RestServiceApiImpl implements  RestServiceApi {

    private static final String TAG = RestServiceApiImpl.class.getSimpleName();

    /**
     * Reference to the context of this application.
     */
    private final Context mContext;

    /**
     * Construct an instance to access the lock data repository.
     *
     * @param context The context of this application.
     */
    RestServiceApiImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void get(URL url, String accessToken, final RestServiceCallback callback) {
        final ResultReceiver receiver = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == ServerRequestIntentService.RESULT_OK) {
                    int status = resultData.getInt(ServerRequestIntentService.STATUS);
                    Log.i(TAG, "Request status: " + status);
                    callback.onResult(true, resultData.getInt(ServerRequestIntentService.VALUE)
                            == 1);
                } else {
                    Log.i(TAG, "Request error");
                    callback.onResult(false, false);
                }
            }
        };
        ServerRequestIntentService.startActionGet(mContext, receiver, url, accessToken);
    }

    @Override
    public void post(URL url, String accessToken, String arg) {
        final ResultReceiver receiver = new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == ServerRequestIntentService.RESULT_OK) {
                    int status = resultData.getInt(ServerRequestIntentService.STATUS);
                    Log.i(TAG, "Request status: " + status);
                } else {
                    Log.i(TAG, "Request error");
                }
            }
        };
        ServerRequestIntentService.startActionPost(mContext, receiver, url, accessToken, arg);
    }
}
