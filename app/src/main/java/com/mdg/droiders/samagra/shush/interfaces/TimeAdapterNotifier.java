package com.mdg.droiders.samagra.shush.interfaces;

import com.mdg.droiders.samagra.shush.viewholders.TimeRowHolder;

/**
 * Created by rohan on 27/11/17.
 * This interface acts as a communication link between {@link TimeRowHolder} and
 * {@link com.mdg.droiders.samagra.shush.adapters.TimeListAdapter TimeListAdapter}.
 */
public interface TimeAdapterNotifier {

    /**
     * Updates db and reschedules alarm if startTime or endTime changes.
     *
     * @param holder The holder instance that is currently bound to the row whose
     *               time is changed.
     */
    void notifyTimeChanged(TimeRowHolder holder);

    /**
     * Updates db and sets alarm for the corresponding day.
     *
     * @param holder The holder instance that is currently bound to the row whose
     *               alarm is set.
     * @param day    The day for which alarm is set. The day is zero indexed and starts from monday.
     */
    void notifyDayAlarmSet(TimeRowHolder holder, int day);

    /**
     * Updates db and cancels alarm for the corresponding day.
     *
     * @param holder The holder instance that is currently bound to the row whose
     *               alarm is cancelled.
     * @param day    The day for which alarm is cancelled. The day is zero indexed
     *               and starts from monday.
     */
    void notifyDayAlarmCancelled(TimeRowHolder holder, int day);

}
