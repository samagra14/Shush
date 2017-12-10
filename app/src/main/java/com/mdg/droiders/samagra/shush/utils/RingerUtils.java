package com.mdg.droiders.samagra.shush.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.mdg.droiders.samagra.shush.R;
import com.mdg.droiders.samagra.shush.activities.MainActivity;

/**
 * Created by rohan on 13/8/17.
 * Util class that contains method related to ringer settings of the phone.
 */
public class RingerUtils {

    /**
     * Changes the ringer mode on the device to either silent or back to normal
     *
     * @param context The context to access AUDIO_SERVICE
     * @param mode    The desired mode to switch device to, can be AudioManager.RINGER_MODE_SILENT or
     *                AudioManager.RINGER_MODE_NORMAL
     */
    public static void setRingerMode(Context context, int mode) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT < 24 || (Build.VERSION.SDK_INT >= 24 && notificationManager.isNotificationPolicyAccessGranted())) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }

    /**
     * Posts a notification in the notification bar.
     * Uses different icon drawables for different transition types.
     * If the user clicks the notification, control goes to the MainActivity
     *
     * @param context     The calling context for building a task stack
     * @param shouldShush Boolean indicating whether the notification is created
     *                    because the phone is shushed or because it is un-shushed.
     */
    public static void sendNotification(Context context, boolean shouldShush) {
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

        if (shouldShush) {
            builder.setSmallIcon(R.drawable.ic_volume_off_white_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.ic_volume_off_white_24dp))
                    .setContentTitle(context.getString(R.string.silent_mode_activated));
        } else {
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

        // Notification will be shown with light, vibration and default sound.
        builder.setDefaults(Notification.DEFAULT_ALL);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }
}
