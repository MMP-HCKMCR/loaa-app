package com.mpphackday.loaa.web;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

/**
 * Created by Badgerati on 29/10/2016.
 */

public class WebRequest
{

    public static void send(String url, String data, String method, String tag, IAsyncTask.OnPostExecuteListener listener)
    {
        RequestBody body = data == null || data.length() == 0
            ? null
            : RequestBody.create(MediaType.parse("application/json; charset=utf-8"), data);

        Request request = new Request.Builder()
                .url(url)
                .method(method, body)
                .build();

        WebTask task = new WebTask(tag);
        task.setOnPostExecuteListener(listener);
        task.executor(request);
    }

}
