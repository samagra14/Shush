package com.mdg.droiders.samagra.shush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.support.v4.app.NotificationCompat;
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

        //Check which transition type has triggered the event
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            RingerUtils.setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
        } else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            RingerUtils.setRingerMode(context, AudioManager.RINGER_MODE_NORMAL);
        } else {
            Log.e(LOG_TAG, String.format("Unknown Transition , %d", geoFenceTransition));
            return;
        }

        //Send the notification
        sendNotification(context, geoFenceTransition);
    }

    /**
     * Posts a notification in the notification bar when a transition is detected
     * Uses different icon drawables for different transition types
     * If the user clicks the notification, control goes to the MainActivity
     *
     * @param context    The calling context for building a task stack
     * @param transition The geofence transition type, can be Geofence.GEOFENCE_TRANSITION_ENTER
     *                   or Geofence.GEOFENCE_TRANSITION_EXIT
     */
    private void sendNotification(Context context, int transition) {
        //create an explicit content intent that starts the main activity
        Intent notificationIntent = new Intent(context, MainActivity.class);

        //Construct a task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        //Adding mainActivity to the task stack as the parent task
        stackBuilder.addParentStack(MainActivity.class);

        //Push the content intent onto the stack
        stackBuilder.addNextIntent(notificationIntent);

        //Get a pending intent consisting the entire backstack
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //Get a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //Check the transition type to display the relevant icon image

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            builder.setSmallIcon(R.drawable.ic_volume_off_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_volume_off_white_24dp))
                    .setContentTitle(context.getString(R.string.silent_mode_activated));
        } else if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            builder.setSmallIcon(R.drawable.ic_volume_up_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_volume_up_white_24dp))
                    .setContentTitle(context.getString(R.string.back_to_normal));
        }

        // Continue building the notification
        builder.setContentText(context.getString(R.string.touch_to_relaunch));
        builder.setContentIntent(notificationPendingIntent);

        //Auto remove the notification if the user touches it
        builder.setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }

}
