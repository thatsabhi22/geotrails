package com.theleafapps.pro.geotrails.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.util.Log;

import com.facebook.AccessToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by aviator on 02/09/16.
 */
public class Commons {

    public static AccessToken accessT;

    public static String insert_usr_st = "INSERT INTO gt_user "+
                "(user_dev_id,fb_id,first_name,last_name,gender,email,current_location) values (?,?,?,?,?,?,?);";

    public static String get_usr_by_fb_id_st = "SELECT user_id from gt_user where fb_id = ?";


    public static String update_usr_st =  "UPDATE gt_user SET user_dev_id = ?,fb_id = ?,first_name = ?,last_name = ?,gender = ?," +
            "email = ?,current_location = ? WHERE user_id = ?";

    public static String insert_marker_st = "INSERT INTO marker (user_lat,user_long,user_id,user_add,loca_title," +
            "loca_desc,geocode_add,is_star,is_sync,ofl_loca_id) values (?,?,?,?,?,?,?,?,?,(SELECT IFNULL(MAX(ofl_loca_id), 10000) + 1 FROM marker))";

    public static String get_all_markers  = "SELECT loca_id,ofl_loca_id,user_lat,user_long,user_id,user_add,loca_title,geocode_add, " +
            "loca_desc,is_star,is_sync,created_on,modified_on from marker ORDER BY modified_on DESC";

    public static String update_marker_star_sync_ofl = "UPDATE marker SET is_star = ?, is_sync = ? where ofl_loca_id = ?;";

    public static String update_marker_sync = "UPDATE marker SET is_sync=1 where ofl_loca_id=?";

    public static String update_marker_loca_id = "UPDATE marker SET loca_id=? where ofl_loca_id=?";

    public static String select_last_inserted_loca_id = "SELECT ofl_loca_id from marker order by ofl_loca_id DESC limit 1";

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static boolean hasActiveInternetConnection(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
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
}
