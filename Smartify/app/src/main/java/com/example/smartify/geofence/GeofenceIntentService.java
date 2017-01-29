package com.example.smartify.geofence;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.smartify.R;
import com.example.smartify.data.Device;
import com.example.smartify.data.DevicesRepository;
import com.example.smartify.data.Injection;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;


public class GeofenceIntentService extends IntentService implements GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = GeofenceIntentService.class.getSimpleName();

    /**
     * Integers to report if the service executed successfully or not.
     */
    public static final int RESULT_OK = 0;
    public static final int RESULT_ERROR = 1;

    /**
     * String representation of the actions that can be requested in this IntentService.
     */
    private static final String ACTION_UPDATE_ALL_GEOFENCES = "com.example.smartify.action" +
            ".UPDATE_ALL_GEOFENCES";

    /**
     * String representation of the extra data that can be given to this IntentService.
     */
    private static final String EXTRA_RECEIVER = "com.example.smartify.extra.RECEIVER";


    private static final float GEOFENCE_RADIUS_IN_METERS = 100;

    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;

    public GeofenceIntentService() {
        super(TAG);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateAllGeofences(Context context) {
        Intent intent = new Intent(context, GeofenceIntentService.class);
        intent.setAction(ACTION_UPDATE_ALL_GEOFENCES);
//        intent.putExtra(EXTRA_RECEIVER, resultReceiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
//            final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
            final String action = intent.getAction();
            switch (action) {
                case ACTION_UPDATE_ALL_GEOFENCES:
                    buildGoogleApiClient(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
                            handleActionUpdateAllGeofences();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            // The connection to Google Play services was lost for some reason.
                            Log.i(TAG, "Connection suspended");
                            // onConnected() will be called again automatically when the service reconnects
                        }
                    });
                    break;
            }
        }
        mGoogleApiClient.connect();
    }

    /**
     * Handle action update all geofences in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateAllGeofences() {
        removeAllGeofences();
        addAllGeofences();
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            // send success to resultreceiver
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
            throw new RuntimeException("Result not successful.");
        }
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    private void addAllGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Log.e(TAG, getString(R.string.not_connected));
            return;
        }
        try {
            List<Geofence> geofenceList = populateGeofenceList();
            if (!geofenceList.isEmpty()) {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        // The GeofenceRequest object.
                        getGeofencingRequest(geofenceList),
                        // A pending intent that that is reused when calling removeGeofences(). This
                        // pending intent is used to generate an intent when a matched geofence
                        // transition is observed.
                        getGeofencePendingIntent()
                ).setResultCallback(this); // Result processed in onResult().
            }
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void removeAllGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Log.e(TAG, getString(R.string.not_connected));
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    private synchronized void buildGoogleApiClient(GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private PendingIntent getGeofencePendingIntent() {
//        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        Intent intent = new Intent("com.example.smartify.geofence.ACTION_GEOFENCE_TRIGGER");
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest(List<Geofence> geofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(geofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    private List<Geofence> populateGeofenceList() {
        DevicesRepository repository = Injection.provideDevicesRepository(getApplicationContext());
        List<Device> deviceList = repository.getAllDevices();
        List<Geofence> geofenceList = new ArrayList<>();

        for (Device device: deviceList) {
            if (device.getAutoAction()) {
                geofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId(String.valueOf(String.valueOf(device.getId())))

                        // Set the circular region of this geofence.
                        .setCircularRegion(device.getLatitude(), device.getLongitude(),
                                GEOFENCE_RADIUS_IN_METERS)

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)
//                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
//                        .setLoiteringDelay(3000);
                        // Create the geofence.
                        .build());
            }
        }
        return geofenceList;
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

}
