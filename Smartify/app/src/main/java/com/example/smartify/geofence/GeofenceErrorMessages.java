package com.example.smartify.geofence;

import android.content.Context;
import android.content.res.Resources;

import com.example.smartify.R;
import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Geofence error codes mapped to error messages.
 */
class GeofenceErrorMessages {
    /**
     * Prevents instantiation.
     */
    private GeofenceErrorMessages() {}

    /**
     * Returns the error string for a geofencing error code.
     */
    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return mResources.getString(R.string.geofence_not_available);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return mResources.getString(R.string.geofence_too_many_geofences);
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return mResources.getString(R.string.geofence_too_many_pending_intents);
            default:
                return mResources.getString(R.string.unknown_geofence_error);
        }
    }
}