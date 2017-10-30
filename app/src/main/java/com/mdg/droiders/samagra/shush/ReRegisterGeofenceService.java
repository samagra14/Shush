package com.mdg.droiders.samagra.shush;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.mdg.droiders.samagra.shush.data.PlacesContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samagra on 19/8/17.
 */

public class ReRegisterGeofenceService extends JobService implements
GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener{
    private Geofencing mGeofencing;
    private GoogleApiClient mClient;
    @Override
    public boolean onStartJob(JobParameters job) {
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        mClient.connect();
        mGeofencing = new Geofencing(mClient,this);
        refreshPlacesData();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        mClient.disconnect();
        return false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void refreshPlacesData(){
        Uri uri = PlacesContract.PlaceEntry.CONTENT_URI;
        Cursor dataCursor = getContentResolver().query(uri,
                null,
                null,
                null,null,null);
        if (dataCursor==null||dataCursor.getCount()==0) return;
        List<String> placeIds = new ArrayList<String>();
        while (dataCursor.moveToNext()){
            placeIds.add(dataCursor.getString(dataCursor.getColumnIndex(PlacesContract.PlaceEntry.COLUMN_PLACE_ID)));
        }
        PendingResult<PlaceBuffer> placeBufferPendingResult = Places.GeoDataApi.getPlaceById(mClient,
                placeIds.toArray(new String[placeIds.size()]));
        placeBufferPendingResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                mGeofencing.updateGeofencesList(places);
                mGeofencing.registerAllGeofences();
            }
        });
    }
}
