package com.theleafapps.pro.geotrails.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.theleafapps.pro.geotrails.models.Mark;
import com.theleafapps.pro.geotrails.models.multiples.Marks;
import com.theleafapps.pro.geotrails.tasks.AddMarkerTask;
import com.theleafapps.pro.geotrails.utils.Commons;
import com.theleafapps.pro.geotrails.utils.DbHelper;
import com.theleafapps.pro.geotrails.utils.ImagePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class AddDataActivity extends AppCompatActivity implements OnMapReadyCallback {

    DbHelper dbHelper;
    GoogleMap mMap;
    Toolbar toolbar;
    boolean result;
    double userLat,userLong;
    TextView geo_code_add_tv;
    EditText location_title_et,location_user_address_et,location_desc_et;
    ImageButton mark_button;
    ImageView add_location_image_button;
    String TAG = "Tangho";
    Address geoAddress;
    ActionBar actionBar;
    String imageFolderName;
    List<String> imageList;
    LayoutInflater inflater;
    RelativeLayout thumbnailContainer;
    ImageView thumbnail;
    private static final int PICK_IMAGE_ID = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        toolbar = (Toolbar) findViewById(R.id.toolbar_add_data);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.logo_small);
        actionBar.setTitle("  Add Info");

        Intent recIntent            =   getIntent();
        imageList                   =   new ArrayList<String>();
        userLat                     =   recIntent.getDoubleExtra("userLat",0);
        userLong                    =   recIntent.getDoubleExtra("userLong",0);
        inflater                    =   (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        thumbnailContainer          =   (RelativeLayout) findViewById(R.id.thumbnailContainer);
        geo_code_add_tv             =   (TextView) findViewById(R.id.reverse_geo_add_tv);
        location_title_et           =   (EditText) findViewById(R.id.location_title_et);
        location_user_address_et    =   (EditText) findViewById(R.id.location_user_address_et);
        location_desc_et            =   (EditText) findViewById(R.id.location_desc_et);
        mark_button                 =   (ImageButton) findViewById(R.id.mark_button);
        add_location_image_button   =   (ImageView) findViewById(R.id.add_location_image_button);

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
                    geo_code_add_tv.setText(sb.toString());
                }
            }

        } catch (IOException e) {
            geo_code_add_tv.setVisibility(View.GONE);
            Toast.makeText(this,"You're Offline!! , Address could not be determined.",Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }

        add_location_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(AddDataActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });

        mark_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(location_title_et.getText())){
                    location_title_et.setError("Atleast Put a title to this location");
                }else{
                    try{
                        dbHelper                =   new DbHelper(AddDataActivity.this);
                        SQLiteDatabase db       =   dbHelper.getWritableDatabase();
                        SQLiteStatement stmt    =   db.compileStatement(Commons.insert_marker_st);
                        stmt.bindDouble(1, userLat);
                        stmt.bindDouble(2, userLong);
                        stmt.bindLong(3, 1);
                        stmt.bindString(4, location_user_address_et.getText().toString());
                        stmt.bindString(5, location_title_et.getText().toString());
                        stmt.bindString(6, location_desc_et.getText().toString());
                        stmt.bindString(7, geo_code_add_tv.getText().toString());
                        stmt.bindLong(8, 0);
                        stmt.bindLong(9, 0);
                        stmt.execute();
                        Log.d(TAG, "Records added");

                        Marks markers = new Marks();

                        Mark marker         =   new Mark();
                        marker.user_lat     =   userLat;
                        marker.user_long    =   userLong;
                        marker.user_id      =   1;
                        marker.user_add     =   location_user_address_et.getText().toString();
                        marker.loca_title   =   location_title_et.getText().toString();
                        marker.loca_desc    =   location_desc_et.getText().toString();
                        marker.geo_code_add =   geo_code_add_tv.getText().toString();
                        marker.is_star      =   0;
                        markers.markerList.add(marker);

                        AddMarkerTask addMarkerTask = new AddMarkerTask(AddDataActivity.this,markers);
                        result = addMarkerTask.execute().get();

                        if(result) {
                            SQLiteStatement stmt1 = db.compileStatement(Commons.update_marker_sync);
                            stmt1.execute();
                        }

                        Intent intent = new Intent(AddDataActivity.this,LocationListActivity.class);
                        intent.putExtra("caller", "HomeActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case PICK_IMAGE_ID:
                    Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                    saveToInternalStorage(bitmap);

                    // TODO use bitmap
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) throws IOException {

        ContextWrapper cw   =   new ContextWrapper(getApplicationContext());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logicalDensity = metrics.density;

        int square = convertToDp(50);
        Bitmap bitmap = Bitmap.createScaledBitmap(bitmapImage, square, square, true);

        // path to /data/data/yourapp/app_data/imageDir

        imageFolderName     =   Commons.randomAlphaNumeric(10);
        File directory      =   cw.getDir("imageDir", Context.MODE_PRIVATE);
//        FileUtils.forceMkdir(new File(directory.getPath()+"/"+imageFolderName));

//        directory           =   new File(directory.getPath()+"/"+imageFolderName);

        // Create imageDir
        int minimum = 1;
        int maximum = 100;
        int random ;

        random      =   minimum + (int)(Math.random() * maximum);
        File mypath =   new File(directory,"img_"+ random +".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            imageList.add("img_"+ random);

            thumbnail  = (ImageView) inflater.inflate(R.layout.thumbnail_image_add_data,null);
            thumbnail.setImageBitmap(bitmap);
            thumbnailContainer.addView(thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return directory.getAbsolutePath();
    }

    public int convertToDp(int input) {
        // Get the screen's density scale
         final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
            return (int) (input * scale + 0.5f);
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
        mMap.getUiSettings().setZoomGesturesEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_location_list:
                Intent intent;
                intent = new Intent(this,LocationListActivity.class);
                intent.putExtra("caller","AddDataActivity");
                startActivity(intent);
                return true;
            case R.id.menu_map:
                intent = new Intent(this,HomeActivity.class);
                intent.putExtra("caller","AddDataActivity");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}