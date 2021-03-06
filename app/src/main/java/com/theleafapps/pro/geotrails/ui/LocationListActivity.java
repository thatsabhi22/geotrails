package com.theleafapps.pro.geotrails.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.adapters.LocationListAdapter;
import com.theleafapps.pro.geotrails.app.MainApplication;
import com.theleafapps.pro.geotrails.models.Mark;
import com.theleafapps.pro.geotrails.models.multiples.Markers;
import com.theleafapps.pro.geotrails.tasks.AddMarkerTask;
import com.theleafapps.pro.geotrails.tasks.UpdateMarkerIsStarTask;
import com.theleafapps.pro.geotrails.utils.Commons;
import com.theleafapps.pro.geotrails.utils.DbHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LocationListActivity extends AppCompatActivity {

    public static String multiMarkerString;
    LocationListAdapter locationListAdapter;
    RecyclerView locationListRecyclerView;
    double userLat, userLong;
    TextView no_location_tv;
    ImageView mark_now_button;
    Markers markers;
    Toolbar toolbar;
    ActionBar actionBar;
    String caller;
    int u_id;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        Intent recIntent = getIntent();
        userLat = recIntent.getDoubleExtra("userLat", 0);
        userLong = recIntent.getDoubleExtra("userLong", 0);
        caller = recIntent.getStringExtra("caller");

        toolbar = (Toolbar) findViewById(R.id.toolbar_location_list);
        no_location_tv = (TextView) findViewById(R.id.no_location_tv);
        mark_now_button = (ImageView) findViewById(R.id.mark_now_button);
        sp = getSharedPreferences("g_t_data", Context.MODE_PRIVATE);
        u_id = sp.getInt("u_id", 0);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.logo_small);
        actionBar.setTitle("  GeoTrails");

        if (u_id != 0)
            markers = Commons.getAllMarkersWithId(DbHelper.get_all_markers, u_id);

        locationListRecyclerView
                = (RecyclerView) findViewById(R.id.location_list_recycler_view);

        if (markers != null && markers.markList.size() > 0) {
            reloadLocationList();
        } else {
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
                Intent intent = new Intent(LocationListActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private void reloadLocationList() {
        locationListRecyclerView.setVisibility(View.VISIBLE);
        no_location_tv.setVisibility(View.GONE);
        mark_now_button.setVisibility(View.GONE);
        locationListAdapter = new LocationListAdapter(this, markers, multiMarkerString, getFragmentManager());
        locationListRecyclerView.setAdapter(locationListAdapter);

        final LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        locationListRecyclerView.setLayoutManager(linearLayoutManager);
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
        if (TextUtils.equals(caller, "AddDataActivity")) {
            intent = new Intent(this, AddDataActivity.class);
            intent.putExtra("userLat", userLat);
            intent.putExtra("userLong", userLong);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public Markers syncAllNewUnsyncedMarkers() {

        List<Integer> ofl_loca_id_list = new ArrayList<>();
        Markers markers = null;
        try {
            markers = Commons.getAllMarkers(DbHelper.get_all_new_unsynced_marker);
            for (Mark marker : markers.markList) {
                ofl_loca_id_list.add(marker.ofl_loca_id);
            }
            if (markers != null && markers.markList.size() > 0) {
                AddMarkerTask addMarkerTask = new AddMarkerTask(this, markers);
                addMarkerTask.execute().get();

                int i = 0;
                Markers responseMarkers = addMarkerTask.markersObj;

                List<Object> param = new ArrayList<>();

                for (Mark mark : responseMarkers.markList) {
                    param.add(String.valueOf(mark.loca_id));
                    param.add(String.valueOf(ofl_loca_id_list.get(i)));
                    Commons.executeLocalQuery(this, DbHelper.update_all_new_unsync_markers, param);
                    i++;
                }
            }
        } catch (InterruptedException e) {
            MainApplication.getInstance().trackException(e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            MainApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        return markers;
    }

    public Markers syncAllOldUnsyncedMarkers() {

        List<Integer> ofl_loca_id_list = new ArrayList<>();
        Markers markers = null;

        try {
            markers = Commons.getAllMarkers(DbHelper.get_all_old_unsynced_marker);
            for (Mark marker : markers.markList) {
                ofl_loca_id_list.add(marker.ofl_loca_id);
            }
            if (markers != null && markers.markList.size() > 0) {
                UpdateMarkerIsStarTask updateMarkerIsStarTask = new UpdateMarkerIsStarTask(this, markers);
                updateMarkerIsStarTask.execute().get();

                int i = 0;
                Markers responseMarkers = updateMarkerIsStarTask.markers;

                List<Object> param = new ArrayList<>();

                for (Mark mark : responseMarkers.markList) {
                    param.add(String.valueOf(ofl_loca_id_list.get(i)));
                    Commons.executeLocalQuery(this, DbHelper.update_all_old_unsync_markers, param);
                    i++;
                }
            }
        } catch (InterruptedException e) {
            MainApplication.getInstance().trackException(e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            MainApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        return markers;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        try {
            switch (item.getItemId()) {
                case R.id.menu_map:
                    intent = new Intent(this, HomeActivity.class);
                    intent.putExtra("multimarker", multiMarkerString);
                    intent.putExtra("caller", "LocationListActivity");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;

                case R.id.sync:
//                    Toast.makeText(this,"sync created",Toast.LENGTH_LONG).show();
                    if (Commons.hasActiveInternetConnection(this)) {

                        syncAllNewUnsyncedMarkers();
                        syncAllOldUnsyncedMarkers();

                        intent = new Intent(this, LoadingActivity.class);
                        intent.putExtra("goto", "LocationListActivity");
                        intent.putExtra("wait_time", 2000);
                        startActivity(intent);

                    } else {
                        Toast.makeText(this, "Please check your internet Connectivity", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case android.R.id.home:
                    String caller = getIntent().getStringExtra("caller");

                    if (!TextUtils.isEmpty(caller)) {
                        Class callerClass = Class.forName(getPackageName() + ".ui." + caller);
                        intent = new Intent(this, callerClass);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        if (TextUtils.equals(caller, "AddDataActivity")) {
                            intent.putExtra("userLat", userLat);
                            intent.putExtra("userLong", userLong);
                        }
                        startActivity(intent);
                        finish();
                    }
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        } catch (ClassNotFoundException e) {
            MainApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
        return false;
    }

}
