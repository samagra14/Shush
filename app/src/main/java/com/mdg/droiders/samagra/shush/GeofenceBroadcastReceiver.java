package com.mdg.droiders.samagra.shush;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = GeofenceBroadcastReceiver.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()){
            Log.e(LOG_TAG,String.format("Error Code : %s",geofencingEvent.getErrorCode()));
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        if (geoFenceTransition== Geofence.GEOFENCE_TRANSITION_ENTER){
            setRingerMode(context,AudioManager.RINGER_MODE_SILENT);
        }
        else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            setRingerMode(context,AudioManager.RINGER_MODE_NORMAL);
        }
        else {
            Log.e(LOG_TAG,String.format("Unknown Transition , %d",geoFenceTransition));
            return;
        }

        sendNotification(context,geoFenceTransition);
    }

    private void setRingerMode(Context context, int mode){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT<24 || (Build.VERSION.SDK_INT>=24 && !notificationManager.isNotificationPolicyAccessGranted())){
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }

    private void sendNotification(Context context,int transition){
        Intent notificationIntent = new Intent(context, MainActivity.class);

        //Construct a task stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        //Adding mainActivity to the task stack as the parent task
        stackBuilder.addParentStack(MainActivity.class);

        //Push the content intent onto the stack
        stackBuilder.addNextIntent(notificationIntent);

        //Get a pending intent consisting the entire backstack
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);

        //Get a notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //Check the transition type to display the relevant icon image

        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER){
            builder.setSmallIcon(R.drawable.ic_volume_off_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_volume_off_white_24dp))
                    .setContentTitle(context.getString(R.string.silent_mode_activated));
        }
        else if (transition== Geofence.GEOFENCE_TRANSITION_EXIT){
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

        notificationManager.notify(0,builder.build());
    }

}
