package com.mdg.droiders.samagra.shush.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

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
    public static void setRingerMode(Context context, int mode){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT<24 || (Build.VERSION.SDK_INT>=24 && notificationManager.isNotificationPolicyAccessGranted())){
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }

}
