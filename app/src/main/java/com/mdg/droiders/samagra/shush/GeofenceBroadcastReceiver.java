package com.mdg.droiders.samagra.shush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.mdg.droiders.samagra.shush.utils.RingerUtils;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = GeofenceBroadcastReceiver.class.getName();

    /***
     * Handles the Broadcast message sent when the Geofence Transition is triggered
     * Careful here though, this is running on the main thread so make sure you start an AsyncTask for
     * anything that takes longer than say 10 second to run
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //get the geofencing event sent from the intent
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.e(LOG_TAG, String.format("Error Code : %s", geofencingEvent.getErrorCode()));
            return;
        }

        //get the transition type
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        //Check which transition type has triggered the event & Send the notification
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            RingerUtils.setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
            RingerUtils.sendNotification(context, true);
        } else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            RingerUtils.setRingerMode(context, AudioManager.RINGER_MODE_NORMAL);
            RingerUtils.sendNotification(context, false);
        } else {
            Log.e(LOG_TAG, String.format("Unknown Transition , %d", geoFenceTransition));
            return;
        }

    }

}
