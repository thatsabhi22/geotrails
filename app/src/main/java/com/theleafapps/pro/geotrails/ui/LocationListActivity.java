package com.theleafapps.pro.geotrails.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.adapters.LocationListAdapter;
import com.theleafapps.pro.geotrails.models.Marker;
import com.theleafapps.pro.geotrails.models.multiples.Markers;
import com.theleafapps.pro.geotrails.utils.DbHelper;

public class LocationListActivity extends AppCompatActivity {

    LocationListAdapter locationListAdapter;
    RecyclerView locationListRecyclerView;
    Markers markers;
    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar_location_list);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.logo_small);
        actionBar.setTitle("  GeoTrails");

        markers     =   getAllMarkers();
        locationListRecyclerView
                    =   (RecyclerView) findViewById(R.id.location_list_recycler_view);

        if(markers!=null && markers.markerList.size()>0){
            reloadLocationList();
        }else{
            setEmptyLocationList();
        }
    }

    private void setEmptyLocationList() {
    }

    private void reloadLocationList() {
        locationListAdapter  =  new LocationListAdapter(this,markers);
        locationListRecyclerView.setAdapter(locationListAdapter);

        final LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        locationListRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private Markers getAllMarkers() {

        Markers markers = new Markers();
        Cursor c = DbHelper.GtrailsDB.rawQuery("SELECT loca_id,user_lat,user_long,user_id,user_add,loca_title,geocode_add, " +
                "loca_desc,is_star from marker ORDER BY modified_on DESC", null);

        int locIdIndex      = c.getColumnIndex("loca_id");
        int userLatIndex    = c.getColumnIndex("user_lat");
        int userLongIndex   = c.getColumnIndex("user_long");
        int userIdIndex     = c.getColumnIndex("user_id");
        int userAddIndex    = c.getColumnIndex("user_add");
        int locTitleIndex   = c.getColumnIndex("loca_title");
        int locaDescIndex   = c.getColumnIndex("loca_desc");
        int geocodeAddIndex = c.getColumnIndex("geocode_add");
        int isStarIndex    = c.getColumnIndex("is_star");

        if(c != null && c.getCount()!=0){
            c.moveToFirst();
            do{
                Marker marker       =   new Marker();
                marker.loca_id      =   c.getInt(locIdIndex);
                marker.user_lat     =   c.getDouble(userLatIndex);
                marker.user_long    =   c.getDouble(userLongIndex);
                marker.user_id      =   c.getInt(userIdIndex);
                marker.user_add     =   c.getString(userAddIndex);
                marker.loca_title   =   c.getString(locTitleIndex);
                marker.loca_desc    =   c.getString(locaDescIndex);
                marker.geo_code_add =   c.getString(geocodeAddIndex);
                marker.is_star      =   c.getInt(isStarIndex);
                markers.markerList.add(marker);
            }while(c.moveToNext());
        }
        return markers;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_location_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_location_list:
                intent = new Intent(this,LocationListActivity.class);
                intent.putExtra("caller","LocationListActivity");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
