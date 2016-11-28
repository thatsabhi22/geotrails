package com.theleafapps.pro.geotrails.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.theleafapps.pro.geotrails.R;
import com.theleafapps.pro.geotrails.app.MainApplication;
import com.theleafapps.pro.geotrails.models.User;
import com.theleafapps.pro.geotrails.models.multiples.Users;
import com.theleafapps.pro.geotrails.tasks.AddUserTask;
import com.theleafapps.pro.geotrails.tasks.GetUserByFbIdTask;
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
    Intent intent;
    String TAG = "Tangho";
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onResume() {
        Log.d("Tangho","AuthActivity : onResume Called ");
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_auth);

        loginButton =   (LoginButton)findViewById(R.id.login_button);
        skip_button =   (ImageButton) findViewById(R.id.skip_button);

        sp = getSharedPreferences("g_t_data", Context.MODE_PRIVATE);

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
                                        MainApplication.getInstance().trackException(e);
                                        e.printStackTrace();
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,first_name,last_name,email,gender,location");
                    request.setParameters(parameters);
                    request.executeAsync();

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

                    if (Build.VERSION.SDK_INT > 22 && !Commons.hasPermissions(AuthActivity.this,Commons.requiredPermissions)) {
                        Toast.makeText(AuthActivity.this, "Please grant all permissions", Toast.LENGTH_LONG).show();
                        Commons.showPermissionDialog(AuthActivity.this);
                    }
                    else{
                        if (Commons.accessT != null) {
                            //Toast.makeText(this, "access Token > " + Commons.accessT, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                        intent      =   new Intent(AuthActivity.this,LoadingActivity.class);
                        intent.putExtra("wait_time",3000);
                        intent.putExtra("goto","HomeActivity");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancel() {
//                info.setText("Login attempt canceled.");
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(AuthActivity.this,"Could not login to facebook. Please check your Internet Connectivity.",Toast.LENGTH_LONG).show();
//                  info.setText("Login attempt failed.");
                }
            });

        Commons.accessT = AccessToken.getCurrentAccessToken();
        if (Build.VERSION.SDK_INT > 22 && !Commons.hasPermissions(AuthActivity.this,Commons.requiredPermissions)) {
            Toast.makeText(AuthActivity.this, "Please grant all permissions", Toast.LENGTH_LONG).show();
            Commons.showPermissionDialog(AuthActivity.this);
        }else{
            if (Commons.accessT != null) {
                //Toast.makeText(this, "access Token > " + Commons.accessT, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }



    private void addUser(JSONObject object) {
        String dev_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        try {
            int user_id;
            SQLiteStatement stmt;
            dbHelper             =   new DbHelper(this);
            SQLiteDatabase db    =   dbHelper.getWritableDatabase();

            String first_name    =   object.get("first_name").toString();
            String last_name     =   object.get("last_name").toString();
            String gender        =   object.get("gender").toString();
            String email         =   object.get("email").toString();
            String location      =   "";
            if(object.has("location")){
                JSONObject locationObj
                                 =  (JSONObject) object.get("location");
                location         =  locationObj.get("name").toString();}
            String fb_id         =   object.get("id").toString();

//#######################################################################
//################ Insert record on cloud ###############################

            GetUserByFbIdTask getUserByFbIdTask = new GetUserByFbIdTask(this,fb_id);
            getUserByFbIdTask.execute().get();

            int userRec;

            if(getUserByFbIdTask.userRec == null){

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
                userRec = addUserTask.userId;

            }else{
                userRec               =   getUserByFbIdTask.userRec.user_id;
            }

//######################################################################

            Cursor c            =   db.rawQuery(DbHelper.get_usr_by_fb_id_st, new String[]{fb_id});
            int count           =   c.getCount();

            if(count > 0){
                stmt            =   db.compileStatement(DbHelper.update_usr_st);
            }else{
                stmt            =   db.compileStatement(DbHelper.insert_usr_st);
            }
            stmt.bindString(1, dev_id);
            stmt.bindString(2, String.valueOf(userRec));
            stmt.bindString(3, first_name);
            stmt.bindString(4, last_name);
            stmt.bindString(5, gender);
            stmt.bindString(6, email);
            stmt.bindString(7, location);
            stmt.bindString(8, String.valueOf(fb_id));

            stmt.execute();
            c.close();

            editor = sp.edit();
            editor.putInt("u_id",userRec);
            editor.commit();

        }catch (JSONException e) {
            MainApplication.getInstance().trackException(e);
            e.printStackTrace();
        } catch (InterruptedException e) {
            MainApplication.getInstance().trackException(e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            MainApplication.getInstance().trackException(e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Commons.REQUEST_APP_SETTINGS) {
            if (Commons.hasPermissions(this,Commons.requiredPermissions)) {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
