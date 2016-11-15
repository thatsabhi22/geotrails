package com.theleafapps.pro.geotrails.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.adapters.LocationListAdapter;
import com.theleafapps.pro.geotrails.models.Mark;
import com.theleafapps.pro.geotrails.models.multiples.Marks;
import com.theleafapps.pro.geotrails.utils.DbHelper;

import java.util.ArrayList;

public class LocationListActivity extends AppCompatActivity {

    LocationListAdapter locationListAdapter;
    RecyclerView locationListRecyclerView;
    double userLat,userLong;
    TextView no_location_tv;
    ImageView mark_now_button;
    Marks markers;
    Toolbar toolbar;
    ActionBar actionBar;
    String caller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        Intent recIntent            =   getIntent();
        userLat                     =   recIntent.getDoubleExtra("userLat",0);
        userLong                    =   recIntent.getDoubleExtra("userLong",0);
        caller                      =   recIntent.getStringExtra("caller");

        toolbar         =   (Toolbar) findViewById(R.id.toolbar_location_list);
        no_location_tv  =   (TextView) findViewById(R.id.no_location_tv);
        mark_now_button =   (ImageView) findViewById(R.id.mark_now_button);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        locationListRecyclerView.setVisibility(View.GONE);
        no_location_tv.setVisibility(View.VISIBLE);
        mark_now_button.setVisibility(View.VISIBLE);

        mark_now_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LocationListActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void reloadLocationList() {
        locationListRecyclerView.setVisibility(View.VISIBLE);
        no_location_tv.setVisibility(View.GONE);
        mark_now_button.setVisibility(View.GONE);
        locationListAdapter  =  new LocationListAdapter(this,markers);
        locationListRecyclerView.setAdapter(locationListAdapter);

        final LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        locationListRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private Marks getAllMarkers() {

        Marks markers = new Marks();
        Cursor c = DbHelper.GtrailsDB.rawQuery("SELECT loca_id,user_lat,user_long,user_id,user_add,loca_title,geocode_add, " +
                "loca_desc,is_star,is_sync from marker ORDER BY modified_on DESC", null);

        int locIdIndex      = c.getColumnIndex("loca_id");
        int userLatIndex    = c.getColumnIndex("user_lat");
        int userLongIndex   = c.getColumnIndex("user_long");
        int userIdIndex     = c.getColumnIndex("user_id");
        int userAddIndex    = c.getColumnIndex("user_add");
        int locTitleIndex   = c.getColumnIndex("loca_title");
        int locaDescIndex   = c.getColumnIndex("loca_desc");
        int geocodeAddIndex = c.getColumnIndex("geocode_add");
        int isStarIndex     = c.getColumnIndex("is_star");
        int isSyncIndex     = c.getColumnIndex("is_sync");

        if(c != null && c.getCount()!=0){
            c.moveToFirst();
            do{
                Mark marker         =   new Mark();
                marker.loca_id      =   c.getInt(locIdIndex);
                marker.user_lat     =   c.getDouble(userLatIndex);
                marker.user_long    =   c.getDouble(userLongIndex);
                marker.user_id      =   c.getInt(userIdIndex);
                marker.user_add     =   c.getString(userAddIndex);
                marker.loca_title   =   c.getString(locTitleIndex);
                marker.loca_desc    =   c.getString(locaDescIndex);
                marker.geo_code_add =   c.getString(geocodeAddIndex);
                marker.is_star      =   c.getInt(isStarIndex) == 1 ? "true" : "false";
                marker.is_sync      =   c.getInt(isSyncIndex);
                markers.markerList.add(marker);
            }while(c.moveToNext());
        }
        c.close();
        return markers;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_location_list, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        if(TextUtils.equals(caller,"AddDataActivity")){
            intent = new Intent(this,AddDataActivity.class);
            intent.putExtra("userLat",userLat);
            intent.putExtra("userLong",userLong);
        }else {
            intent = new Intent(this,HomeActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        try {
            switch (item.getItemId()) {
                case R.id.menu_map:
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("caller", "LocationListActivity");
                    startActivity(intent);
                    return true;
                case android.R.id.home:
                    String caller = getIntent().getStringExtra("caller");

                    if (!TextUtils.isEmpty(caller)) {
                        Class callerClass = Class.forName(getPackageName() + ".ui." + caller);
                        intent = new Intent(this, callerClass);

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if(TextUtils.equals(caller,"AddDataActivity")){
                            intent.putExtra("userLat",userLat);
                            intent.putExtra("userLong",userLong);
                        }
                        startActivity(intent);
                        finish();
                    }
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
