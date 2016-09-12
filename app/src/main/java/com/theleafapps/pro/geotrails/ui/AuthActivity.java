package com.theleafapps.pro.geotrails.ui;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.models.User;
import com.theleafapps.pro.geotrails.models.multiples.Users;
import com.theleafapps.pro.geotrails.tasks.AddUserTask;
import com.theleafapps.pro.geotrails.utils.Commons;
import com.theleafapps.pro.geotrails.utils.DbHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class AuthActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ImageButton skip_button;
    private DbHelper dbHelper;
    String TAG = "Tangho";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_auth);

        loginButton =   (LoginButton)findViewById(R.id.login_button);
        skip_button =   (ImageButton) findViewById(R.id.skip_button);

        skip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthActivity.this,HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

            loginButton.setReadPermissions(Arrays.asList("public_profile", "email","user_location"));
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    loginResult.getAccessToken();
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        Log.d(TAG, "GraphRequest onCompleted: " + object.get("email").toString());
                                        addUser(object);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,first_name,last_name,email,gender,location");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
//                info.setText("Login attempt canceled.");
                }

                @Override
                public void onError(FacebookException error) {
//                info.setText("Login attempt failed.");
                }
            });

        
            accessTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(
                        AccessToken oldAccessToken,
                        AccessToken currentAccessToken) {
                    // Set the access token using
                    // currentAccessToken when it's loaded or set.
                }
            };
            // If the access token is available already assign it.
            Commons.accessT = AccessToken.getCurrentAccessToken();

            if (Commons.accessT != null) {
                Toast.makeText(this, "access Token > " + Commons.accessT, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
    }

    private void addUser(JSONObject object) {
        String dev_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            int user_id;
            SQLiteStatement stmt;
            dbHelper            =   new DbHelper(this);
            SQLiteDatabase db   =   dbHelper.getWritableDatabase();

            String first_name    =   object.get("first_name").toString();
            String last_name     =   object.get("last_name").toString();
            String gender        =   object.get("gender").toString();
            String email         =   object.get("email").toString();
            JSONObject locationObj = (JSONObject) object.get("location");
            String location =  locationObj.get("name").toString();
            if(TextUtils.isEmpty(location)){location = "";}

            String fb_id        =   object.get("id").toString();
            Cursor c            =   db.rawQuery(Commons.get_usr_by_fb_id_st, new String[]{fb_id});
            int count           =   c.getCount();

            if(count > 0){
                int userIdIndex =   c.getColumnIndex("user_id");
                c.moveToFirst();
                user_id         =   c.getInt(userIdIndex);
                stmt            =   db.compileStatement(Commons.update_usr_st);
                stmt.bindString(8,String.valueOf(user_id));
            }else{
                stmt            =   db.compileStatement(Commons.insert_usr_st);
            }
            stmt.bindString(1, dev_id);
            stmt.bindString(2, fb_id);
            stmt.bindString(3, first_name);
            stmt.bindString(4, last_name);
            stmt.bindString(5, gender);
            stmt.bindString(6, email);
            stmt.bindString(7, location);
            stmt.execute();
            c.close();

            Users users           =   new Users();
            User user             =   new User();
            user.user_dev_id      =   dev_id;
            user.current_location =   location;
            user.email            =   email;
            user.first_name       =   first_name;
            user.last_name        =   last_name;
            user.gender           =   gender;
            user.fb_id            =   fb_id;

            users.userList.add(user);
            AddUserTask addUserTask = new AddUserTask(this,users);
            addUserTask.execute().get();


        }catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
