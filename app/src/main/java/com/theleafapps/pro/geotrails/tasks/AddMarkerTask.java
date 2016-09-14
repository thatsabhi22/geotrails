package com.theleafapps.pro.geotrails.tasks;

import android.content.Context;
import android.util.Log;

import com.theleafapps.pro.geotrails.models.multiples.Marks;
import com.theleafapps.pro.geotrails.utils.AppConstants;
import com.theleafapps.pro.geotrails.utils.PrefUtil;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;

/**
 * Created by aviator on 14/09/16.
 */
public class AddMarkerTask extends BaseAsyncRequest  {

    Context context;
    public int locaId;
    Marks markersObj;

    public AddMarkerTask(Context context, Marks markers){
        this.context      =   context;
        this.markersObj   =   markers;
    }

    @Override
    protected void doSetup() throws ApiException {
        callerName = "AddMarkerTask";
        serviceName = AppConstants.DB_SVC;
        endPoint = "marker";

        verb = "POST";

        requestString = ApiInvoker.serialize(markersObj).replace("\"loca_id\":0,","");
        requestString = requestString.replace(",\"loca_id\":0","");

        applicationApiKey = AppConstants.API_KEY;
        sessionToken = PrefUtil.getString(context, AppConstants.SESSION_TOKEN);
    }

    @Override
    protected void processResponse(String response) throws ApiException, org.json.JSONException {
        // response has whole contact record, but we just want the id
        Marks markersObj    =   (Marks) ApiInvoker.deserialize(response, "", Marks.class);
        locaId              =   markersObj.markerList.get(0).loca_id;
    }

    @Override
    protected void onCompletion(boolean success) {
        if(success) {
            Log.d("Tang Ho","Success");
        }
    }
}
