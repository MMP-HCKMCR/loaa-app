package com.mpphackday.loaa.web;

import android.os.AsyncTask;

/**
 * Created by Badgerati on 29/10/2016.
 */

public class BaseAsyncTask<V, U, T> extends AsyncTask<V, U, T> implements IAsyncTask<V, U, T>
{

    private OnPostExecuteListener mOnPostExecuteListener = null;
    private OnPreExecuteListener mOnPreExecuteListener = null;
    private String mTag = "";


    public BaseAsyncTask(String tag)
    {
        super();
        mTag = tag;
    }

    @Override
    public void setOnPostExecuteListener(IAsyncTask.OnPostExecuteListener listener)
    {
        mOnPostExecuteListener = listener;
    }

    @Override
    public void setOnPreExecuteListener(IAsyncTask.OnPreExecuteListener listener)
    {
        mOnPreExecuteListener = listener;
    }

    @Override
    public T doInBackground(V... params)
    {
        return null;
    }

    @Override
    public void onPostExecute(T result)
    {
        super.onPostExecute(result);

        if (mOnPostExecuteListener != null)
        {
            mOnPostExecuteListener.onPostExecute(this, result, mTag);
        }
    }

    @Override
    public void onPreExecute()
    {
        super.onPreExecute();

        if (mOnPreExecuteListener != null)
        {
            mOnPreExecuteListener.onPreExecute(this, mTag);
        }
    }

    @Override
    public void executor(V... objects)
    {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, objects);
    }
}