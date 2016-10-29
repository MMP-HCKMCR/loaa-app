package com.mpphackday.loaa.web;

/**
 * Created by Badgerati on 29/10/2016.
 */

public interface IAsyncTask<V, U, T>
{

    interface OnPostExecuteListener
    {
        <T> void onPostExecute(IAsyncTask asyncTask, T result, String tag);
    }

    interface OnPreExecuteListener
    {
        void onPreExecute(IAsyncTask asyncTask, String tag);
    }

    void setOnPostExecuteListener(OnPostExecuteListener listener);
    void setOnPreExecuteListener(OnPreExecuteListener listener);

    T doInBackground(V... objects);
    void onPostExecute(T result);
    void onPreExecute();
    void executor(V... objects);

}