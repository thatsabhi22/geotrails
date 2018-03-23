package com.theleafapps.pro.geotrails.tasks;

import android.content.Context;
import android.util.Log;

import com.theleafapps.pro.geotrails.models.User;
import com.theleafapps.pro.geotrails.models.multiples.Users;
import com.theleafapps.pro.geotrails.utils.AppConstants;
import com.theleafapps.pro.geotrails.utils.PrefUtil;

import org.json.JSONException;

import java.util.HashMap;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;

/**
 * Created by aviator on 23/11/16.
 */

public class GetUserByFbIdTask extends BaseAsyncRequest {

    public Users usersRec;
    public User userRec;
    Context context;
    String fbId;

    public GetUserByFbIdTask(Context context, String fbId) {
        this.context = context;
        this.fbId = fbId;
    }

    @Override
    protected void doSetup() throws ApiException, JSONException {
        callerName = "getCustomerById";

        serviceName = AppConstants.DB_SVC;
        endPoint = "user";
        verb = "GET";

        // filter to only select the contacts in this group
        queryParams = new HashMap<>();
        queryParams.put("filter", "fb_id=" + fbId);

        // request without related would return just {id, contact_group_id, contact_id}
        // set the related field to go get the contact mRecordsList referenced by
        // each contact_group_relationship record
        // queryParams.put("related", "contact_by_contact_id");

        // need to include the API key and session token
        applicationApiKey = AppConstants.API_KEY;
        sessionToken = PrefUtil.getString(context, AppConstants.SESSION_TOKEN);
    }

    @Override
    protected void processResponse(String response) throws ApiException, JSONException {
        usersRec =
                (Users) ApiInvoker.deserialize(response, "", Users.class);
        if (usersRec.userList.size() > 0)
            userRec = usersRec.userList.get(0);
    }

    @Override
    protected void onCompletion(boolean success) {
        if (success && usersRec != null) {
            Log.d("Tang Ho", " >>>>> Success");
        }
    }

}
