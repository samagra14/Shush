package com.mdg.droiders.samagra.shush.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.mdg.droiders.samagra.shush.AlarmScheduler;
import com.mdg.droiders.samagra.shush.R;
import com.mdg.droiders.samagra.shush.utils.RingerUtils;

import java.util.Calendar;

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
                context.getString(R.string.alarm_intent_boolean_extra_key), false);
        // The timeInMillis at which the alarm is triggered
        // Used to trigger an alarm for next week at the same time.
        long timeInMillis = intent.getLongExtra(
                context.getString(R.string.alarm_intent_long_extra_key), -1);
        // The alarm id used in pending intent
        int alarmID = intent.getIntExtra(
                context.getString(R.string.alarm_intent_int_extra_key), -1);

        if (timeInMillis == -1 || alarmID == -1){
            return;
        }

        if (shush) {
            RingerUtils.setRingerMode(context, AudioManager.RINGER_MODE_SILENT);
            RingerUtils.sendNotification(context, true);
        } else {
            RingerUtils.setRingerMode(context, AudioManager.RINGER_MODE_NORMAL);
            RingerUtils.sendNotification(context, false);
        }

        Calendar nextWeek = Calendar.getInstance();
        nextWeek.setTimeInMillis(timeInMillis);
        nextWeek.add(Calendar.DAY_OF_MONTH, 7);

        // Schedule an alarm for next week
        new AlarmScheduler(context).setAlarm(nextWeek.getTimeInMillis(), alarmID, shush);

    }
}
