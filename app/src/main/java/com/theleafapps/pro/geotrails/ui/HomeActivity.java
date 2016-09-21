package com.theleafapps.pro.geotrails.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.utils.Commons;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private String TAG = "Tangho";
    private int ENABLE_LOCATION = 1;

    Location location;
    LocationManager locationManager;
    String provider;
    ImageView mark_location_button;
    ImageView list_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mark_location_button = (ImageView) findViewById(R.id.mark_location_button);
        list_button          = (ImageView) findViewById(R.id.list_button);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider        = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (Commons.accessT != null) {

            Toast.makeText(this, "Home Activity >>" + Commons.accessT.getApplicationId(), Toast.LENGTH_LONG).show();

        }

        checkIfLocationEnabled(this);

        location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            Log.d(TAG, location.getLatitude() + " ||| " + location.getLongitude());
        } else {
            Log.d(TAG, "Location not found");
        }

        mark_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Commons.accessT != null) {
                    checkIfLocationEnabled(HomeActivity.this);
                    if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        Intent intent = new Intent(HomeActivity.this, AddDataActivity.class);
                        intent.putExtra("userLat", location.getLatitude());
                        intent.putExtra("userLong", location.getLongitude());
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this,"Enable your Location",Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Intent intent = new Intent(HomeActivity.this, AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LocationListActivity.class);
                intent.putExtra("caller", "HomeActivity");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ENABLE_LOCATION) {
            // Make sure the request was successful
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                location = locationManager.getLastKnownLocation(provider);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney;
        // Add a marker in Sydney and move the camera
        if(location == null) {
            sydney = new LatLng(-34, 151);
        }else{
            sydney = new LatLng(location.getLatitude(), location.getLongitude());
        }
        Marker mkr = mMap.addMarker(
                        new MarkerOptions()
                                .position(sydney)
                                .title("You are here")
                                .snippet("Mark Your Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_used)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13f));
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onLocationChanged(Location locationUpdate) {
        LatLng sydney = new LatLng(locationUpdate.getLatitude(), locationUpdate.getLongitude());
        location = locationUpdate;
        mMap.clear();
        mMap.addMarker(
                new MarkerOptions()
                        .position(sydney)
                        .title("You are Here")
                        .snippet("Mark Your Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_used)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,13f));
        Log.d(TAG, "onLocationChanged: lat : "+ locationUpdate.getLatitude()+" long : " + locationUpdate.getLongitude());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }

    public void checkIfLocationEnabled(Context ctx){
        final Context context   = ctx;
        LocationManager lm      = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled     = false;
//        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}

//        if(!gps_enabled && !network_enabled) {
        if(!gps_enabled) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
                dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(myIntent);
                        //get gps
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
