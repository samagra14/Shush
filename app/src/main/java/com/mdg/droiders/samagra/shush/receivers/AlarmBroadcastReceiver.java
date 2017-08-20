package com.mdg.droiders.samagra.shush.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.mdg.droiders.samagra.shush.R;
import com.mdg.droiders.samagra.shush.utils.RingerUtils;

/**
 * Created by rohan on 12/8/17.
 * <br>
 * Broadcast Receiver that receives a broadcast from the alarm manager to silence/un-silence the phone.
 */
public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "Samagra/BR/";

    @Override
    public void onReceive(Context context, Intent intent) {

        // The phone will be shushed(silenced) if the boolean shush is true
        boolean shush = intent.getBooleanExtra(
                context.getString(R.string.alarm_intent_extra_key), false);

        if (shush) {
            RingerUtils.setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
            RingerUtils.sendNotification(context, true);
        } else {
            RingerUtils.setRingerMode(context, AudioManager.RINGER_MODE_NORMAL);
            RingerUtils.sendNotification(context, false);
        }

    }
}
