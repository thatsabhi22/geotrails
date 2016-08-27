package com.theleafapps.pro.geotrails.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by aviator on 07/01/16.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "geoTrailsDB";
    private Context context;
    public static SQLiteDatabase GtrailsDB;


    public DbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
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
        GtrailsDB     = context.openOrCreateDatabase("featherNotesDB", Context.MODE_PRIVATE, null);
        GtrailsDB.execSQL(
        "CREATE TABLE IF NOT EXISTS user (user_id INTEGER PRIMARY KEY AUTOINCREMENT, user_dev_id INTEGER, " +
                "first_name VARCHAR, last_name VARCHAR, gender VARCHAR, email VARCHAR, city VARCHAR, country VARCHAR, " +
                "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, modified_on DATETIME DEFAULT CURRENT_TIMESTAMP);");

        GtrailsDB.execSQL(
        "CREATE TABLE IF NOT EXISTS marker (loca_id INTEGER PRIMARY KEY AUTOINCREMENT, user_lat DOUBLE, " +
                "user_long DOUBLE, user_id INTEGER, user_add VARCHAR, loca_title VARCHAR, loca_desc VARCHAR, geocode_add VARCHAR, " +
                "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, modified_on DATETIME DEFAULT CURRENT_TIMESTAMP);");

        GtrailsDB.execSQL(
        "CREATE TABLE IF NOT EXISTS image (image_id INTEGER PRIMARY KEY AUTOINCREMENT, loca_id INTEGER, " +
                "user_id INTEGER, image_name VARCHAR, image_desc VARCHAR,"+
                "created_on DATETIME DEFAULT CURRENT_TIMESTAMP, modified_on DATETIME DEFAULT CURRENT_TIMESTAMP);");
    }
}
