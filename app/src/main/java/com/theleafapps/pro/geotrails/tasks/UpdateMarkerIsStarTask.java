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
 * Created by aviator on 14/11/16.
 */

public class UpdateMarkerIsStarTask extends BaseAsyncRequest {

    public int locaId;
    public Markers markers;
    Context context;

    public UpdateMarkerIsStarTask(Context context, Markers markers) {
        this.context = context;
        this.markers = markers;
    }

    @Override
    protected void doSetup() throws ApiException {
        callerName = "UpdateMarkerIsStarTask";

        serviceName = AppConstants.DB_SVC;
        endPoint = "marker";

        verb = "PUT";

        requestString = ApiInvoker.serialize(markers);

        applicationApiKey = AppConstants.API_KEY;
        sessionToken = PrefUtil.getString(context, AppConstants.SESSION_TOKEN);
    }

    @Override
    protected void processResponse(String response) throws ApiException, org.json.JSONException {
        markers = (Markers) ApiInvoker.deserialize(response, "", Markers.class);
        locaId = markers.markList.get(0).loca_id;
    }

    @Override
    protected void onCompletion(boolean success) {
        if (success) {
            Log.d("Tang Ho", "Success");
        }
    }
}
