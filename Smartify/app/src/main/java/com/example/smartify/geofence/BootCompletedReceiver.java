package com.example.smartify.geofence;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Listener for when the smartphone boots up. Add all geofences when device boots up.
 */
public class BootCompletedReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofenceIntentService.startActionUpdateAllGeofences(context);
    }
}
