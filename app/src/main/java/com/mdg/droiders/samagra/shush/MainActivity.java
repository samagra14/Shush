package com.mdg.droiders.samagra.shush;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mdg.droiders.samagra.shush.data.PlacesContract;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = "MainActivity";

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;

    private PlaceListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Button addPlaceButton;

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
        addPlaceButton = (Button) findViewById(R.id.add_location_button);

        addPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddPlaceButtonClicked();
            }
        });

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this,this)
                .build();
    }

    /**
     * Button click event handler for the add place button.
     */
    private void onAddPlaceButtonClicked() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, getString(R.string.need_location_permission_message), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, getString(R.string.location_permissions_granted_message), Toast.LENGTH_SHORT).show();

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent placePickerIntent = builder.build(this);
            startActivityForResult(placePickerIntent,PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==PLACE_PICKER_REQUEST&&resultCode==RESULT_OK){
            Place place = PlacePicker.getPlace(this,data);
            if (place==null){
                Log.i(LOG_TAG,"No place selected");
                return;
            }
            String placeName = place.getName().toString();
            String placeAddress = place.getAddress().toString();
            String placeId = place.getId();

            ContentValues values = new ContentValues();
            values.put(PlacesContract.PlaceEntry.COLUMN_PLACE_ID,placeId);
            getContentResolver().insert(PlacesContract.PlaceEntry.CONTENT_URI,values);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //initialise location permissions checkbox
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
