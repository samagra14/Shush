package com.mdg.droiders.samagra.shush;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = "MainActivity";

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;

    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState Bundle that contains the data provided to onSavedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.places_list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlaceListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckBox locationPermissionsCheckBox = (CheckBox) findViewById(R.id.location_permission_checkbox);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            locationPermissionsCheckBox.setChecked(false);
        }
        else {
            locationPermissionsCheckBox.setChecked(true);
            locationPermissionsCheckBox.setEnabled(false);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * Called when the google API client is successfully connected.
     * @param bundle Bundle of data provided to the clients by google play services.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG_TAG,"Api connection successful");
        Toast.makeText(this, "onConnected", Toast.LENGTH_SHORT).show();

    }

    /**
     * Called when the google API client is suspended
     * @param cause  The reason for the disconnection. Defined by the constant CAUSE_*.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(LOG_TAG,"API Client connection suspended.");

    }


    /**
     * Called when the google API client failed to connect to the PlayServices.
     * @param connectionResult A coonectionResult that can be used to solve the error.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG,"API Connection client suspended.");
        Toast.makeText(this, "onConectionFailed", Toast.LENGTH_SHORT).show();

    }

    public void onLocationPermissionClicked (){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);
    }
}
