package com.mdg.droiders.samagra.shush;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samagra on 1/8/17.
 */

public class Geofencing implements ResultCallback {

    private static final long GEOFENCE_TIMEOUT = 86400000;//24 hours in milliseconds
    private static final int GEOFENCE_RADIUS = 50;
    private static final String LOG_TAG = Geofencing.class.getName();

    private GoogleApiClient mClient;
    private Context mContext;
    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;

    public Geofencing(GoogleApiClient mClient, Context mContext) {
        this.mClient = mClient;
        this.mContext = mContext;
        mGeofenceList = new ArrayList<>();
        mGeofencePendingIntent = null;
    }

    public void registerAllGeofences() {
        if (mClient == null || !mClient.isConnected()
                || mGeofenceList == null || mGeofenceList.size() == 0) {
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(mClient,
                    getGeofenceRequest(),
                    getGeofencePendingIntent()).setResultCallback(this);
        }
        catch (SecurityException securityException){
            securityException.printStackTrace();
        }
    }

    public void unRegisterAllGeofences(){
        if (mClient==null|| !mClient.isConnected()) return;
        LocationServices.GeofencingApi.removeGeofences(mClient,
                getGeofencePendingIntent()).setResultCallback(this);
    }

    public void updateGeofencesList(PlaceBuffer places){
        mGeofenceList = new ArrayList<>();
        if (places==null || places.getCount()==0) return;
        for (Place place: places){
            String placeUid = place.getId();
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;
            //Build a geofence object
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeUid)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(latitude,longitude,GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            mGeofenceList.add(geofence);
        }
    }

    private GeofencingRequest getGeofenceRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){
        if (mGeofencePendingIntent!=null){
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(mContext,GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(LOG_TAG,String.format("Error adding or removing geofences : %s",result.getStatus().toString()));
    }
}
