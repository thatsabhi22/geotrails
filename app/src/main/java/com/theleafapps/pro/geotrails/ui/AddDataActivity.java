package com.theleafapps.pro.geotrails.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.theleafapps.pro.geotrails.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddDataActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    double userLat,userLong;
    TextView reverse_geo_add_tv;
    String TAG = "Tangho";
    Address geoAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        Intent recIntent    = getIntent();
        userLat             = recIntent.getDoubleExtra("userLat",0);
        userLong            = recIntent.getDoubleExtra("userLong",0);

        reverse_geo_add_tv  = (TextView) findViewById(R.id.reverse_geo_add_tv);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.addDataSmallMap);
        mapFragment.getMapAsync(this);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try{
            List<Address> listAddress = geocoder.getFromLocation(userLat,userLong,1);
            if(listAddress!=null && listAddress.size()>0){
                Log.d(TAG, "Address Geocode : " + listAddress.get(0).toString());
                geoAddress = listAddress.get(0);

                if(geoAddress!=null){
                    StringBuilder sb = new StringBuilder();
                    int maxAddlines = geoAddress.getMaxAddressLineIndex();
                    for(int i=0;i < maxAddlines;i++){
                        sb.append(geoAddress.getAddressLine(i));
                        if(i != maxAddlines-1) {
                            sb.append(", ");
                        }
                    }
                    reverse_geo_add_tv.setText(sb.toString());
                }
            }

        } catch (IOException e) {
            reverse_geo_add_tv.setVisibility(View.GONE);
            Toast.makeText(this,"You're Offline!! , Address could not be determined.",Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        LatLng sydney = new LatLng(userLat,userLong);
        Marker mkr = mMap.addMarker(
                new MarkerOptions()
                        .position(sydney)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow_mini)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15f));
    }
}