package com.theleafapps.pro.geotrails.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by aviator on 07/01/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geoTrailsDB";
    private Context context;
    public static SQLiteDatabase GtrailsDB;


    public DbHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
        this.context = context;
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteNote(int id){

        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement("DELETE FROM notes where id = ?;");
        stmt.bindString(1, String.valueOf(id));
        stmt.execute();
        //MainActivity.arrayAdapter.notifyDataSetChanged();

        Log.i("Life","Note Deleted ..");
    }

    public static void createDB(Context context) {
        GtrailsDB     = context.openOrCreateDatabase("geoTrailsDB", Context.MODE_PRIVATE, null);
        GtrailsDB.execSQL(
        "CREATE TABLE IF NOT EXISTS gt_user (user_id INTEGER PRIMARY KEY AUTOINCREMENT, user_dev_id INTEGER, " +
                "fb_id VARCHAR,first_name VARCHAR, last_name VARCHAR, gender VARCHAR, email VARCHAR, current_location VARCHAR, is_sync INTEGER, " +
                "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, modified_on DATETIME DEFAULT CURRENT_TIMESTAMP);");

        GtrailsDB.execSQL(
        "CREATE TABLE IF NOT EXISTS marker (loca_id INTEGER, ofl_loca_id INTEGER, user_lat DOUBLE, " +
                "user_long DOUBLE, user_id INTEGER, user_add VARCHAR, loca_title VARCHAR, loca_desc VARCHAR, geocode_add VARCHAR, " +
                "is_star INTEGER,is_sync INTEGER, created_on DATETIME DEFAULT CURRENT_TIMESTAMP, modified_on DATETIME DEFAULT CURRENT_TIMESTAMP);");

        GtrailsDB.execSQL(
        "CREATE TABLE IF NOT EXISTS image (image_id INTEGER PRIMARY KEY AUTOINCREMENT, loca_id INTEGER, " +
                "user_id INTEGER, image_name VARCHAR, image_desc VARCHAR,"+
                "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, modified_on DATETIME DEFAULT CURRENT_TIMESTAMP);");
    }
}
