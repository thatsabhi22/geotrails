package com.theleafapps.pro.geotrails.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.app.MainApplication;

public class LoadingActivity extends AppCompatActivity {

    //Introduce a delay
    int wait_time;
    Class callerClass;
    Intent intent;
    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            intent = getIntent();
            String gotoActivity = intent.getStringExtra("goto");
            wait_time = intent.getIntExtra("wait_time", 1000);
            callerClass = Class.forName(getPackageName() + ".ui." + gotoActivity);
            uiHandler = new Handler(); // anything posted to this handler will run on the UI Thread
            System.out.println("LoadingScreenActivity  screen started");
            setContentView(R.layout.activity_loading);
            findViewById(R.id.mainSpinner).setVisibility(View.VISIBLE);

            final Runnable onUi = new Runnable() {
                @Override
                public void run() {
                    // this will run on the main UI thread
                    Intent mainIntent = new Intent(LoadingActivity.this, callerClass);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    LoadingActivity.this.startActivity(mainIntent);
                    LoadingActivity.this.finish();
                }
            };

            final Runnable background = new Runnable() {
                @Override
                public void run() {
                    try {
                        // This is the delay
                        Thread.sleep(wait_time);
                        // This will run on a background thread
                        //Simulating a long running task
                        Thread.sleep(1000);
                        System.out.println("Going to Profile Data");
                        uiHandler.post(onUi);

                    } catch (InterruptedException e) {
                        MainApplication.getInstance().trackException(e);
                        e.printStackTrace();
                    }
                }
            };
            new Thread(background).start();
        } catch (ClassNotFoundException e) {
            MainApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }
}
