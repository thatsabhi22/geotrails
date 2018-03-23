package com.theleafapps.pro.geotrails.tasks;

import android.content.Context;
import android.util.Log;

import com.theleafapps.pro.geotrails.models.multiples.Users;
import com.theleafapps.pro.geotrails.utils.AppConstants;
import com.theleafapps.pro.geotrails.utils.PrefUtil;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;

/**
 * Created by aviator on 18/07/16.
 */
public class AddUserTask extends BaseAsyncRequest {

    public int userId;
    Context context;
    Users usersObj;

    public AddUserTask(Context context, Users users) {
        this.context = context;
        this.usersObj = users;
    }

    @Override
    protected void doSetup() throws ApiException {
        callerName = "AddUserTask";
        serviceName = AppConstants.DB_SVC;
        endPoint = "user";

        verb = "POST";

        requestString = ApiInvoker.serialize(usersObj).replace("\"user_id\":0,", "");
        requestString = requestString.replace(",\"user_id\":0", "");

        applicationApiKey = AppConstants.API_KEY;
        sessionToken = PrefUtil.getString(context, AppConstants.SESSION_TOKEN);
    }

    @Override
    protected void processResponse(String response) throws ApiException, org.json.JSONException {
        // response has whole contact record, but we just want the id
        Users usersObj = (Users) ApiInvoker.deserialize(response, "", Users.class);
        userId = usersObj.userList.get(0).user_id;
    }

    @Override
    protected void onCompletion(boolean success) {
        if (success) {
            Log.d("Tang Ho", "Success");
        }
    }
}
