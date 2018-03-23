package com.theleafapps.pro.geotrails.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.facebook.AccessToken;
import com.theleafapps.pro.geotrails.models.Mark;
import com.theleafapps.pro.geotrails.models.multiples.Marks;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by aviator on 02/09/16.
 */
public class Commons {

    public static final int REQUEST_APP_SETTINGS = 168;
    public static final String[] requiredPermissions = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            /* ETC.. */
    };
    final static int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static int user_id;
    public static AccessToken accessT;

    public static boolean hasPermissions(Context context, @NonNull String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(context, permission))
                return false;
        return true;
    }

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    private static void goToSettings(Activity activity) {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivityForResult(myAppSettings, MY_PERMISSIONS_REQUEST_LOCATION);
    }

    public static void showPermissionDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setMessage("Please grant all required permissions\n " +
                "The App may not function well otherwise.");
        builder.setTitle("GeoTrails");
        builder.setPositiveButton("App Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
                goToSettings(activity);
            }
        });

        builder.show();
    }

    public static void showNonCancellablePermissionDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setMessage("Please grant all required permissions\n" +
                "The App may not function well otherwise.");
        builder.setTitle("GeoTrails");
        builder.setPositiveButton("App Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goToSettings(activity);
            }
        });

        builder.show();
    }

    public static boolean hasActiveInternetConnection(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (isConnected) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000);
                urlc.connect();

                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("Tangho", "Error checking internet connection", e);
            }
        } else {
            Log.d("Tangho", "No network available!");
        }
        return false;
    }

    public static Marks getAllMarkers(String query) {

        Marks markers = new Marks();
        Cursor c = DbHelper.GtrailsDB.rawQuery(query, null);

        populateMarkers(markers, c);
        c.close();
        return markers;
    }

    public static Marks getAllMarkersWithId(String query, int user_id) {

        Marks markers = new Marks();
        Cursor c = DbHelper.GtrailsDB.rawQuery(query, new String[]{String.valueOf(user_id)});

        populateMarkers(markers, c);
        c.close();
        return markers;
    }

    public static void executeLocalQuery(Context context, String query, List<Object> params) {

        int i = 1;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(query);

        for (Object param : params) {
            stmt.bindString(i, String.valueOf(param));
            i++;
        }

        stmt.execute();
        params.clear();
    }

    private static void populateMarkers(Marks markers, Cursor c) {
        int locIdIndex = c.getColumnIndex("loca_id");
        int oflLocIdIndex = c.getColumnIndex("ofl_loca_id");
        int userLatIndex = c.getColumnIndex("user_lat");
        int userLongIndex = c.getColumnIndex("user_long");
        int userIdIndex = c.getColumnIndex("user_id");
        int userAddIndex = c.getColumnIndex("user_add");
        int locTitleIndex = c.getColumnIndex("loca_title");
        int locaDescIndex = c.getColumnIndex("loca_desc");
        int geocodeAddIndex = c.getColumnIndex("geocode_add");
        int isStarIndex = c.getColumnIndex("is_star");
        int isSyncIndex = c.getColumnIndex("is_sync");
        int isDeletedIndex = c.getColumnIndex("is_deleted");
        int c_on = c.getColumnIndex("created_on");
        int m_on = c.getColumnIndex("modified_on");


        if (c != null && c.getCount() != 0) {
            c.moveToFirst();
            do {

                Mark marker = new Mark();
                marker.loca_id = c.getInt(locIdIndex);
                marker.ofl_loca_id = c.getInt(oflLocIdIndex);
                marker.user_lat = c.getDouble(userLatIndex);
                marker.user_long = c.getDouble(userLongIndex);
                marker.user_id = c.getInt(userIdIndex);
                marker.user_add = c.getString(userAddIndex);
                marker.loca_title = c.getString(locTitleIndex);
                marker.loca_desc = c.getString(locaDescIndex);
                marker.geo_code_add = c.getString(geocodeAddIndex);
                marker.is_star = c.getInt(isStarIndex) == 1 ? "true" : "false";
                marker.is_sync = c.getInt(isSyncIndex);
                marker.is_deleted = c.getInt(isDeletedIndex);
                marker.created_on = c.getString(c_on);
                marker.modified_on = c.getString(m_on);

                markers.markerList.add(marker);
            } while (c.moveToNext());
        }
    }

    public static Marks getAllMarkersWithIds(String multiMarkerString) {
        String query = "";
        if (multiMarkerString.matches("^\\d+(,\\d+)*$")) {
            query = DbHelper.get_all_markers_with_ids.replace("?", multiMarkerString);
        }
        return Commons.getAllMarkers(query);
    }
}
