package com.example.smartify.geofence;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.smartify.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

/**
 * Implements an activity that needs location services. It's responsible for obtaining the location
 * permission and turning on the user's location.
 */
public abstract class LocationActivity extends AppCompatActivity {

    private static final String TAG = LocationActivity.class.getSimpleName();

    private static final int RC_PERMISSION_LOCATION = 0;
    private static final int RC_ENABLE_LOCATION = 1;


    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestLocationPermission()) {
                requestLocationSettings();
                Log.i(TAG, "Location permission granted.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. Do location related task.
            } else {
                // Permission denied, disable the functionality that depends on this permission.
                finish(); // TODO
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != Activity.RESULT_OK) {
            finish();
        }
    }

    protected Location getLastLocation() {
        if (!mGoogleApiClient.isConnected()) {
            Log.e(TAG, getString(R.string.not_connected));
            return null;
        }
        // Redundant, should be requestLocationPermission() but IDE gives errors.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Permission Required.")
                        .setMessage("Location is required for geofencing.")
                        .setIcon(R.drawable.ic_warning)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showLocationPermissionDialog();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // No explanation needed, we can request the permission.
                showLocationPermissionDialog();
            }
            return null;
        }
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Returns whether location permissions were set at time of calling. If not set, they are
     * requested from the user with a dialog.
     *
     * @return True if location permissions were granted, false otherwise.
     */
    private boolean requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Permission Required.")
                        .setMessage("Location is required for geofencing.")
//                        .setIcon(R.drawable.ic_action_warning)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showLocationPermissionDialog();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                // No explanation needed, we can request the permission.
                showLocationPermissionDialog();
            }
            return false;
        }
        return true;
    }

    private void showLocationPermissionDialog() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                .ACCESS_FINE_LOCATION}, RC_PERMISSION_LOCATION);
    }

    private void requestLocationSettings() {
        LocationRequest locationRequest = new LocationRequest().setPriority
                (LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder locationRequestBuilder = new LocationSettingsRequest
                .Builder().addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient, locationRequestBuilder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
//                        final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(LocationActivity.this, RC_ENABLE_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
                        builder.setTitle("Location Permission Required.")
                                .setMessage("Location is required for geofencing.")
                                .setIcon(R.drawable.ic_warning)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                }
            }
        });
    }
}
