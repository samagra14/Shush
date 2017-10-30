package com.mdg.droiders.samagra.shush.utils;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.mdg.droiders.samagra.shush.ReRegisterGeofenceService;

/**
 * Created by samagra on 19/8/17.
 */

public class GeofenceUtils {
    public static Job createJob(FirebaseJobDispatcher dispatcher){
        Job job = dispatcher.newJobBuilder()
                .setLifetime(Lifetime.FOREVER)
                .setService(ReRegisterGeofenceService.class)
                .setTag("Periodic registration of geofences")
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(24*60*60,48*60*60))
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        return job;
    }

}
