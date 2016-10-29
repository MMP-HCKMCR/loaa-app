package com.mpphackday.loaa.web;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

/**
 * Created by Badgerati on 29/10/2016.
 */

public class WebRequest
{

    public static void send(String url, String data, String tag, IAsyncTask.OnPostExecuteListener listener)
    {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        WebTask task = new WebTask(tag);
        task.setOnPostExecuteListener(listener);
        task.executor(request);
    }

}
