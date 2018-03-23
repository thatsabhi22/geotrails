package com.theleafapps.pro.geotrails.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by aviator on 07/01/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geoTrailsDB";
    public static SQLiteDatabase GtrailsDB;
    public static String insert_usr_st = "INSERT INTO gt_user " +
            "(user_dev_id,user_id,first_name,last_name,gender,email,current_location,fb_id) values (?,?,?,?,?,?,?,?);";
    public static String update_usr_st = "UPDATE gt_user SET user_dev_id = ?,user_id = ?,first_name = ?,last_name = ?," +
            "gender = ?,email = ?,current_location = ? WHERE fb_id = ?";
    public static String get_usr_by_fb_id_st = "SELECT user_id FROM gt_user where fb_id = ?";
    public static String get_all_new_unsynced_marker = "SELECT * FROM marker where is_sync = 0 AND loca_id IS NULL";
    public static String get_all_old_unsynced_marker = "SELECT * FROM marker where is_sync = 0 AND loca_id IS NOT NULL";
    public static String update_all_new_unsync_markers = "UPDATE marker SET is_sync = 1, loca_id = ? where ofl_loca_id = ?";
    public static String update_all_old_unsync_markers = "UPDATE marker SET is_sync = 1 where ofl_loca_id = ?";
    public static String insert_marker_st = "INSERT INTO marker (user_lat,user_long,user_id,user_add,loca_title," +
            "loca_desc,geocode_add,is_star,is_sync,ofl_loca_id,is_deleted) values (?,?,?,?,?,?,?,?,?,(SELECT IFNULL(MAX(ofl_loca_id), 10000) + 1 FROM marker),0)";
    public static String update_marker_st = "UPDATE marker set " +
            "user_lat = ? ," +
            "user_long = ? ," +
            "user_id = ? ," +
            "user_add = ? ," +
            "loca_title = ? ," +
            "loca_desc = ? ," +
            "geocode_add = ? ," +
            "is_star = ? ," +
            "is_sync = ?  ," +
            "modified_on = CURRENT_TIMESTAMP " +
            "where ofl_loca_id = ? ";
    public static String get_all_markers = "SELECT loca_id,ofl_loca_id,user_lat,user_long,user_id,user_add,loca_title,geocode_add, " +
            "loca_desc,is_star,is_sync,is_deleted,created_on,modified_on from marker where is_deleted = 0 and user_id = ? ORDER BY modified_on DESC";
    public static String get_all_markers_with_ids = "SELECT * FROM marker where ofl_loca_id in (?)";
    public static String update_marker_star_sync_ofl = "UPDATE marker SET is_star = ?, is_sync = ? where ofl_loca_id = ?;";
    public static String update_marker_sync = "UPDATE marker SET is_sync=1 where ofl_loca_id=?";
    public static String update_marker_loca_id = "UPDATE marker SET loca_id=? where ofl_loca_id=?";
    public static String delete_marker_loca_id = "UPDATE marker SET is_deleted = 1, is_sync = 0 where ofl_loca_id = ?";
    public static String select_last_inserted_loca_id = "SELECT ofl_loca_id from marker order by ofl_loca_id DESC limit 1";
    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void createDB(Context context) {
        GtrailsDB = context.openOrCreateDatabase("geoTrailsDB", Context.MODE_PRIVATE, null);
        GtrailsDB.execSQL(
                "CREATE TABLE IF NOT EXISTS gt_user (" +
                        "user_id INTEGER PRIMARY KEY, " +
                        "user_dev_id INTEGER, " +
                        "fb_id VARCHAR," +
                        "first_name VARCHAR, " +
                        "last_name VARCHAR, " +
                        "gender VARCHAR, " +
                        "email VARCHAR, " +
                        "current_location VARCHAR, " +
                        "is_sync INTEGER, " +
                        "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "modified_on DATETIME DEFAULT CURRENT_TIMESTAMP);");

        GtrailsDB.execSQL(
                "CREATE TABLE IF NOT EXISTS marker (" +
                        "loca_id INTEGER, " +
                        "ofl_loca_id INTEGER, " +
                        "user_lat DOUBLE, " +
                        "user_long DOUBLE, " +
                        "user_id INTEGER, " +
                        "user_add VARCHAR, " +
                        "loca_title VARCHAR, " +
                        "loca_desc VARCHAR, " +
                        "geocode_add VARCHAR, " +
                        "is_star INTEGER," +
                        "is_sync INTEGER, " +
                        "is_deleted INTEGER, " +
                        "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "modified_on DATETIME DEFAULT CURRENT_TIMESTAMP);");

        GtrailsDB.execSQL(
                "CREATE TABLE IF NOT EXISTS image (" +
                        "image_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "loca_id INTEGER, " +
                        "user_id INTEGER, " +
                        "image_name VARCHAR, " +
                        "image_desc VARCHAR," +
                        "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "modified_on DATETIME DEFAULT CURRENT_TIMESTAMP);");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteMarker(int marker_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement("UPDATE marker SET is_deleted = 1 where ofl_loca_id = ?;");
        stmt.bindString(1, String.valueOf(marker_id));
        stmt.execute();
    }
}
