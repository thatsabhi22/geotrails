package com.theleafapps.pro.geotrails.tasks;

import android.content.Context;
import android.util.Log;

import com.theleafapps.pro.geotrails.models.multiples.Markers;
import com.theleafapps.pro.geotrails.utils.AppConstants;
import com.theleafapps.pro.geotrails.utils.PrefUtil;

import dfapi.ApiException;
import dfapi.ApiInvoker;
import dfapi.BaseAsyncRequest;

/**
 * Created by aviator on 14/09/16.
 */
public class AddMarkerTask extends BaseAsyncRequest {

    public int locaId;
    public Markers markersObj;
    Context context;

    public AddMarkerTask(Context context, Markers markers) {
        this.context = context;
        this.markersObj = markers;
    }

    @Override
    protected void doSetup() throws ApiException {
        callerName = "AddMarkerTask";
        serviceName = AppConstants.DB_SVC;
        endPoint = "marker";

        verb = "POST";

        requestString = ApiInvoker.serialize(markersObj).replace("\"loca_id\":0,", "");
        requestString = requestString.replace(",\"loca_id\":0", "");

        applicationApiKey = AppConstants.API_KEY;
        sessionToken = PrefUtil.getString(context, AppConstants.SESSION_TOKEN);
    }

    @Override
    protected void processResponse(String response) throws ApiException, org.json.JSONException {
        // response has whole contact record, but we just want the id
        markersObj = (Markers) ApiInvoker.deserialize(response, "", Markers.class);
        locaId = markersObj.markList.get(0).loca_id;
    }

    @Override
    protected void onCompletion(boolean success) {
        if (success) {
            Log.d("Tang Ho", "Success");
        }
    }
}
