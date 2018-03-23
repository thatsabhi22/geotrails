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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.app.MainApplication;
import com.theleafapps.pro.geotrails.models.Mark;
import com.theleafapps.pro.geotrails.models.multiples.Marks;
import com.theleafapps.pro.geotrails.utils.Commons;

import java.util.List;

public class HomeActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    Intent intent;
    Location location;
    LocationManager locationManager;
    String provider;
    ImageView mark_location_button, list_button, logo_text;
    TextView about_us_tv, credits_tv;
    String multiMarker;
    private GoogleMap mMap;
    private String TAG = "Tangho";
    private int ENABLE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toast.makeText(this, "Get yourself in open to spot exact location.", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "It might take a few seconds to determine your correct location !!", Toast.LENGTH_LONG).show();

        intent = getIntent();
        multiMarker = intent.getStringExtra("multimarker");

        mark_location_button = (ImageView) findViewById(R.id.mark_location_button);
        list_button = (ImageView) findViewById(R.id.list_button);
        logo_text = (ImageView) findViewById(R.id.logo_text);
        about_us_tv = (TextView) findViewById(R.id.about_us);
        credits_tv = (TextView) findViewById(R.id.credits);

        mark_location_button.bringToFront();
        list_button.bringToFront();
        about_us_tv.bringToFront();
        logo_text.bringToFront();
        credits_tv.bringToFront();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (Build.VERSION.SDK_INT > 22 && !Commons.hasPermissions(HomeActivity.this, Commons.requiredPermissions)) {
            Toast.makeText(HomeActivity.this, "Please grant all permissions", Toast.LENGTH_LONG).show();
            Commons.showNonCancellablePermissionDialog(HomeActivity.this);
        }

        if (Commons.accessT != null) {
//            Toast.makeText(this, "Home Activity >>" + Commons.accessT.getApplicationId(), Toast.LENGTH_LONG).show();
        }

        checkIfLocationEnabled(this);

        location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            Log.d(TAG, location.getLatitude() + " ||| " + location.getLongitude());
        } else {
            Log.d(TAG, "Location not found");
        }

        about_us_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(HomeActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });

        credits_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(HomeActivity.this, CreditsActivity.class);
                startActivity(intent);
            }
        });

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
                        intent = new Intent(HomeActivity.this, AddDataActivity.class);
                        intent.putExtra("userLat", location.getLatitude());
                        intent.putExtra("userLong", location.getLongitude());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "Weak or No GPS signals, Try Coming in open", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    intent = new Intent(HomeActivity.this, AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        list_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(HomeActivity.this, LocationListActivity.class);
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
        if (location == null) {
            sydney = new LatLng(-34, 151);
        } else {
            sydney = new LatLng(location.getLatitude(), location.getLongitude());
        }

        if (TextUtils.isEmpty(multiMarker)) {
            Marker mkr = mMap.addMarker(
                    new MarkerOptions()
                            .position(sydney)
                            .title("You are here")
                            .snippet("Mark Your Location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_used)));
            mkr.showInfoWindow();

            //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f));
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
        } else {
            Marks markers = Commons.getAllMarkersWithIds(multiMarker);
            insertMarkers(markers.markerList, sydney);
        }
    }

    @Override
    public void onLocationChanged(Location locationUpdate) {
        LatLng sydney = new LatLng(locationUpdate.getLatitude(), locationUpdate.getLongitude());
        if (TextUtils.isEmpty(multiMarker)) {
            location = locationUpdate;
            mMap.clear();
            Marker mkr =
                    mMap.addMarker(
                            new MarkerOptions()
                                    .position(sydney)
                                    .title("You are Here")
                                    .snippet("Mark Your Location")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_used)));
            mkr.showInfoWindow();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13f));
            Log.d(TAG, "onLocationChanged: lat : " + locationUpdate.getLatitude() + " long : " + locationUpdate.getLongitude());
        } else {
            Marks markers = Commons.getAllMarkersWithIds(multiMarker);
            insertMarkers(markers.markerList, sydney);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (provider == null) {
            provider = locationManager.getBestProvider(new Criteria(), false);
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

    public void checkIfLocationEnabled(Context ctx) {
        final Context context = ctx;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
//        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            MainApplication.getInstance().trackException(ex);
        }

//        try {
//            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}

//        if(!gps_enabled && !network_enabled) {
        if (!gps_enabled) {
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


    private void insertMarkers(List<Mark> markers, LatLng currentLoc) {
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < markers.size(); i++) {
            final LatLng position = new LatLng(markers.get(i).user_lat, markers.get(i).user_long);
            String str = markers.get(i).loca_desc;
            int maxLength = (str.length() < 15) ? str.length() : 15;
            str = str.substring(0, maxLength);

            final MarkerOptions options =
                    new MarkerOptions()
                            .position(position)
                            .title(markers.get(i).loca_title)
                            .snippet(str)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_mini));

            mMap.addMarker(options);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            builder.include(position);
        }

        final LatLng position1 = new LatLng(currentLoc.latitude, currentLoc.longitude);
        Marker current = mMap.addMarker(
                new MarkerOptions()
                        .position(currentLoc)
                        .title("You are Here")
                        .snippet("Mark Your Location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_used)));
        current.showInfoWindow();
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        builder.include(position1);

        LatLngBounds coordsBounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(coordsBounds, 100);
        mMap.animateCamera(cameraUpdate);
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
