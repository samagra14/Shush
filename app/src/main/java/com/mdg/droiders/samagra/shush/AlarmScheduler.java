package com.mdg.droiders.samagra.shush;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mdg.droiders.samagra.shush.receivers.AlarmBroadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by rohan on 12/8/17.
 * <br>
 * This class schedules an alarm which changes ringer mode of the phone when it is triggered.
 */
public class AlarmScheduler {

    private static final SimpleDateFormat LOG_DATE_FORMAT
            = new SimpleDateFormat("hh:mm a, EE ,dd MMM yyyy", Locale.ENGLISH);
    private static final int START_ALARM_ID_INCREMENTER = 10000;
    private static final int END_ALARM_ID_INCREMENTER = 1000;
    private static final String LOG_TAG = "Samagra/AS/";

    private AlarmManager alarmManager;
    private Context context;

    public AlarmScheduler(Context context) {
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.context = context;
    }

    /**
     * Sets an alarm to shush the phone for a single day.
     *
     * @param startTime The time at which phone is to be shushed
     * @param endTime   The time at which phone is to be un-shushed
     * @param day       int type for week days starting from monday and zero indexed.
     *                  For e.g. - Monday is 0, Tuesday is 1, ... and so on
     * @param rowID     Unique primary key of the shush alarm row.
     */
    public void setSingleDayAlarm(Calendar startTime, Calendar endTime, int day, Integer rowID) {
        if (day > 6 || rowID == null) {
            return;
        }
        boolean[] days = new boolean[7];
        for (int i = 0; i < 7; i++) {
            days[i] = day == i;
        }
        setWeeklyAlarm(startTime, endTime, days, rowID);
    }

    /**
     * Cancels a shush alarm for a particular day if it exists.
     *
     * @param day   int type for week days starting from monday and zero indexed.
     *              For e.g. - Monday is 0, Tuesday is 1, ... and so on
     * @param rowID Unique primary key of the shush alarm row.
     */
    public void cancelSingleAlarm(int day, Integer rowID) {
        if (rowID == null) {
            return;
        }
        alarmManager.cancel(getDefaultPendingIntent(getStartDayID(day, rowID)));
        alarmManager.cancel(getDefaultPendingIntent(getEndDayID(day, rowID)));
    }

    /**
     * Sets a weekly alarm to silence your phone for days given by boolean array days.
     * <br>
     * <b>NOTE</b> : This method only schedules alarms and does NOT cancels for some day whose
     * corresponding index in days array is false. To cancel alarms
     * use {@link #cancelSingleAlarm(int, Integer)}
     * <br><br>
     * <b>Uses</b> : {@link #setAlarm(long, long, Integer, Integer)} to set each alarm one by one.
     *
     * @param startTime The time of day at which phone is to be shushed
     * @param endTime   The time of day at which phone is to be un-shushed
     * @param days      A boolean array that is true if the alarm is to be
     *                  scheduled for the corresponding day. i.e. days[0] is set to be true
     *                  then a weekly alarm will be set such that it will shush the phone every Monday.
     * @param rowID     Unique primary key of the shush alarm row.
     */
    public void setWeeklyAlarm(Calendar startTime, Calendar endTime, boolean[] days, Integer rowID) {
        // To avoid various exceptions
        if (rowID == null || days.length != 7) {
            return;
        }
        // Instance of calendar that holds current system time
        Calendar rightNow = Calendar.getInstance();

        // Set YEAR, MONTH and DAY fields of startTime
        // and endTime to today's YEAR, MONTH and DAY respectively
        startTime.set(Calendar.YEAR, rightNow.get(Calendar.YEAR));
        startTime.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        startTime.set(Calendar.DAY_OF_MONTH, rightNow.get(Calendar.DAY_OF_MONTH));
        endTime.set(Calendar.YEAR, rightNow.get(Calendar.YEAR));
        endTime.set(Calendar.MONTH, rightNow.get(Calendar.MONTH));
        endTime.set(Calendar.DAY_OF_MONTH, rightNow.get(Calendar.DAY_OF_MONTH));
        Log.d(LOG_TAG + "now", LOG_DATE_FORMAT.format(rightNow.getTime()));

        // If startTime's hour is ahead of endTime's, assume that
        // endTime is of that of next day
        if (startTime.get(Calendar.HOUR_OF_DAY)
                > endTime.get(Calendar.HOUR_OF_DAY)) {
            endTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        // If endTime is in the past increment both start and end time
        if (endTime.compareTo(rightNow) < 0) {
            startTime.add(Calendar.DAY_OF_MONTH, 1);
            endTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        // get Day index of start Time
        int i = getDay(startTime);
        for (int j = i; j < 7; j++) {
            Log.d(LOG_TAG + "I-Val", String.valueOf(j));
            if (days[j]) {
                setAlarm(startTime.getTimeInMillis(),
                        endTime.getTimeInMillis(),
                        getStartDayID(j, rowID),
                        getEndDayID(j, rowID));
            }
            // Update startTime and endTime
            startTime.add(Calendar.DAY_OF_MONTH, 1);
            endTime.add(Calendar.DAY_OF_MONTH, 1);
        }
        for (int j = 0; j < i; j++) {
            startTime.add(Calendar.DAY_OF_MONTH, 1);
            endTime.add(Calendar.DAY_OF_MONTH, 1);
            Log.d(LOG_TAG + "I-Val", String.valueOf(j));
            if (days[j]) {
                setAlarm(startTime.getTimeInMillis(),
                        endTime.getTimeInMillis(),
                        getStartDayID(j, rowID),
                        getEndDayID(j, rowID));
            }
        }
    }

    /**
     * Sets a weekly alarm to silence your phone for every day.
     * <br><br>
     * <b>Uses</b> : {@link #setWeeklyAlarm(Calendar, Calendar, boolean[], Integer)} to set weekly alarms.
     *
     * @param startHour    The hour at which phone is to be shushed in 24 hour format
     * @param startMinutes The minute at which phone is to be shushed
     * @param endHour      The hour at which phone is to be un-shushed in 24 hour format
     * @param endMinutes   The minute at which phone is to be un-shushed
     * @param days         A boolean array that is true if the alarm is to be
     *                     scheduled for the corresponding day. i.e. days[0] is set to be true
     *                     then a weekly alarm will be set such that it will shush the phone every Monday.
     * @param rowID        Unique primary key of the shush alarm row.
     */
    public void setWeeklyAlarm(int startHour, int startMinutes, int endHour,
                               int endMinutes, boolean[] days, Integer rowID) {
        if (rowID == null || days.length != 7) {
            return;
        }
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, startHour);
        startTime.set(Calendar.MINUTE, startMinutes);
        endTime.set(Calendar.HOUR_OF_DAY, endHour);
        endTime.set(Calendar.MINUTE, endMinutes);
        setWeeklyAlarm(startTime, endTime, days, rowID);
    }

    /**
     * Set an alarm to silence the phone.
     * <br><br>
     * <b>Uses</b> : {@link #setAlarm(long, Integer, boolean)} to set start alarm
     * and the end alarm
     *
     * @param startTimeInMillis The time at which phone is to be shushed
     * @param endTimeInMillis   The time at which phone is to be un-shushed
     * @param startAlarmID      The id that uniquely identifies the start time alarm
     * @param endAlarmID        The id that uniquely identifies the end time alarm
     */
    public void setAlarm(long startTimeInMillis, long endTimeInMillis, Integer startAlarmID, Integer endAlarmID) {
        if (startTimeInMillis >= endTimeInMillis) {
            return;
        }
        if (startAlarmID == null || endAlarmID == null) {
            return;
        }
        if (startAlarmID.equals(endAlarmID)) {
            Log.e(LOG_TAG + "error", "start alarm id and end alarm ids are same");
            return;
        }

        Log.d(LOG_TAG + "start", LOG_DATE_FORMAT.format(new Date(startTimeInMillis)));
        Log.d(LOG_TAG + "end", LOG_DATE_FORMAT.format(new Date(endTimeInMillis)));
        setAlarm(startTimeInMillis, startAlarmID, true);
        setAlarm(endTimeInMillis, endAlarmID, false);
    }

    /**
     * Set an alarm to silence the phone.
     *
     * @param timeInMillis The time at which phone is to be shushed/un-shushed
     * @param alarmID      The alarm id used when scheduling the alarm
     * @param shush        A boolean indicating whether or not to
     *                     silence the phone when the alarm is fired by the system.
     *                     Silent mode will be activated if shouldShush is set to be true
     * @see #setAlarm(long, long, Integer, Integer)
     */
    public void setAlarm(long timeInMillis, Integer alarmID, boolean shush) {
        if (alarmID == null) {
            return;
        }

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                getDefaultPendingIntent(shush, timeInMillis, alarmID)
        );
    }

    /**
     * Sets multiple alarms at once.
     * <br><br>
     * <b>Uses</b> : {@link #setAlarm(long, long, Integer, Integer)} to set each alarm one by one.
     *
     * @param startTimesInMillis The list of start times at which phone is to be shushed
     * @param endTimesInMillis   The list of end times at which phone is to be un-shushed
     */
    public void setAlarms(List<Long> startTimesInMillis, List<Long> endTimesInMillis,
                          List<Integer> startAlarmIds, List<Integer> endAlarmIds) {
        if (startTimesInMillis.size() != endTimesInMillis.size()) {
            return;
        }
        for (int i = 0; i < startTimesInMillis.size(); i++) {
            setAlarm(startTimesInMillis.get(i), endTimesInMillis.get(i),
                    startAlarmIds.get(i), endAlarmIds.get(i));
        }
    }

    /**
     * Get a default pending intent without any extras.
     * <br> Used to cancel any existing alarm whose alarm id is known.
     *
     * @param alarmID The alarm id used when scheduling the alarm
     * @return a pending intent that can be used to cancel
     * any alarm which has same pending intent
     * @see #getDefaultPendingIntent(boolean, long, int)
     */
    private PendingIntent getDefaultPendingIntent(int alarmID) {
        PendingIntent pendingIntent;

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(
                context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        return pendingIntent;
    }

    /**
     * Get a complete pending intent with all the extra values.
     * <br> Used to schedule an alarm for future.
     *
     * @param shouldShush  A boolean indicating whether or not to
     *                     silence the phone when the alarm is fired by the system.
     *                     Silent mode will be activated if shouldShush is set to be true
     * @param timeInMillis The "timeInMillis" at which the alarm is expected to go off.
     *                     It is used to schedule a future alarm for next week.
     * @param alarmId      An int that uniquely identifies this alarm, so that the alarm
     *                     may be updated/canceled in the future using the same.
     * @return A pending intent used to schedule alarms by {@link #setAlarm(long, long, Integer, Integer)} method
     * @see #getDefaultPendingIntent(int)
     */
    private PendingIntent getDefaultPendingIntent(boolean shouldShush, long timeInMillis, int alarmId) {
        PendingIntent pendingIntent;

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(context.getString(R.string.alarm_intent_boolean_extra_key), shouldShush);
        intent.putExtra(context.getString(R.string.alarm_intent_long_extra_key), timeInMillis);
        intent.putExtra(context.getString(R.string.alarm_intent_int_extra_key), alarmId);
        pendingIntent = PendingIntent.getBroadcast(
                context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        return pendingIntent;
    }

    /**
     * Get day ID for start alarms(shush types) that are scheduled by alarm manager.
     * The ID returned identifies a particular day of a particular shush alarm.
     *
     * @param day   int type for week days starting from monday and zero indexed.
     *              For e.g. - Monday is 0, Tuesday is 1, ... and so on
     * @param rowID Unique primary key of the shush alarm row.
     * @return The ID used by {@link AlarmManager} to schedule weekly alarms
     */
    private int getStartDayID(int day, int rowID) {
        return rowID + (day + 1) * START_ALARM_ID_INCREMENTER;
    }

    /**
     * Get day ID for end alarms(un-shush types) that are scheduled by alarm manager.
     * The ID returned identifies a particular day of a particular shush alarm.
     *
     * @param day   int type for week days starting from monday and zero indexed.
     *              For e.g. - Monday is 0, Tuesday is 1, ... and so on
     * @param rowID Unique primary key of the shush alarm row.
     * @return The ID used by {@link AlarmManager} to schedule weekly alarms
     */
    private int getEndDayID(int day, int rowID) {
        return rowID + (day + 1) * END_ALARM_ID_INCREMENTER;
    }

    /**
     * Get day field of calendar indexed at 0 such that start day of week is monday.
     *
     * @param calendar The calendar whose day is to be returned
     * @return int type for week days starting from monday and zero indexed.
     * For e.g. - Monday is 0, Tuesday is 1, ... and so on
     */
    private int getDay(Calendar calendar) {
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        i -= 2;
        if (i == -1) {
            i += 7;
        }
        return i;
    }
}
