package com.theleafapps.pro.geotrails.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.theleafapps.pro.geotrails.R;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

//        Commons.getActivityTrail(this);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo_medium)
                .setDescription("\nGeoTrails\n\nThis is the very useful app for travellers, explorers, backpackers, surveyors, trekkers and what not !\n" +
                        "\n" +
                        "Mark your location on the map and keep for your remembrance. \n" +
                        "Select Multiple locations you have been to and get displayed on the map\n" +
                        "A Simple interface you can use the app very smoothly\n" +
                        "The map animations are simple cool\n\n" +
                        "Upcoming features :\n" +
                        "Share on Facebook, Twitter, Instagram and much more\n" +
                        "Take photographs of the places you have marked on the map and Watch anytime\n"
                )
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Connect with us")
                .addEmail("getintouch@theleafapps.com")
                .addWebsite("http://theleafapps.com/")
                .addFacebook("theleafapps")
                .addTwitter("theleafapps")
                .addPlayStore("com.theleafapps.pro")
                .addInstagram("theleafapps")
                .create();

        setContentView(aboutPage);
    }


    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(("copy_right"), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIcon(R.drawable.flag);
        copyRightsElement.setColor(ContextCompat.getColor(this, mehdi.sakout.aboutpage.R.color.about_item_icon_color));
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUsActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Tangho", "AboutUs activity >> onRestart Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
        Log.d("Tangho", "AboutUs activity >> onPause Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Tangho", "AboutUs activity >> onDestroy Called");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("Tangho", "AboutUs activity >> onBackPressed Called");
        Intent intent = new Intent(this, HomeActivity.class);
    }
}
