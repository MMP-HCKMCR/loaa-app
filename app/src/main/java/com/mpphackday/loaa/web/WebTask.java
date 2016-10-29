package com.mpphackday.loaa.web;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by Badgerati on 29/10/2016.
 */

public class WebTask extends BaseAsyncTask<Request, Void, JsonDocument>
{

    private final String mTag = "WebTask";

    public WebTask(String tag)
    {
        super(tag);
    }

    @Override
    public JsonDocument doInBackground(Request... requests)
    {
        try
        {
            if (requests == null || requests.length != 1)
            {
                return null;
            }

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(requests[0]).execute();
            WebResponse _response = new WebResponse(response, requests[0].urlString());

            return _response.getBody();
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return null;
    }

}
