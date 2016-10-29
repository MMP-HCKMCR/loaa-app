package com.mpphackday.loaa.web;

import android.util.Log;

import com.mpphackday.loaa.dto.MissingPeopleResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Badgerati on 29/10/2016.
 */

public class JsonDocument
{

    private final String mTag = "JsonDocument";
    private String mRaw = "";
    private JSONObject mJson = null;


    public JsonDocument(String raw)
    {
        mRaw = raw;

        try
        {
            mJson = new JSONObject(mRaw);
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
            mJson = null;
        }
    }

    public JsonDocument(JSONObject json)
    {
        mRaw = json == null ? "" : json.toString();
        mJson = json;
    }

    public ArrayList<JsonDocument> getArray(String tag)
    {
        if (mJson == null)
        {
            return null;
        }

        try
        {
            JSONArray array = mJson.getJSONArray(tag);
            ArrayList<JsonDocument> docs = new ArrayList<>(array.length());

            for (int i = 0; i < array.length(); i++)
            {
                docs.add(new JsonDocument(array.getJSONObject(i)));
            }

            return docs;
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return null;
    }

    public ArrayList<MissingPeopleResult> getMissingPeopleArray(String tag)
    {
        if (mJson == null)
        {
            return null;
        }

        try
        {
            JSONArray array = mJson.getJSONArray(tag);
            ArrayList<MissingPeopleResult> docs = new ArrayList<>(array.length());

            for (int i = 0; i < array.length(); i++)
            {
                JsonDocument doc = new JsonDocument(array.getJSONObject(i));
                docs.add(new MissingPeopleResult(doc));
            }

            return docs;
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return null;
    }

    public ArrayList<String> getStringArray(String tag)
    {
        if (mJson == null)
        {
            return null;
        }

        try
        {
            JSONArray array = mJson.getJSONArray(tag);
            ArrayList<String> docs = new ArrayList<>(array.length());

            for (int i = 0; i < array.length(); i++)
            {
                docs.add(array.getString(i)); //  new JsonDocument(array.getJSONObject(i)));
            }

            return docs;
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return null;
    }

    public JsonDocument getObject(String tag)
    {
        try
        {
            return new JsonDocument(mJson.getJSONObject(tag));
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return null;
    }

    public String getString(String tag)
    {
        try
        {
            return mJson.getString(tag);
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return null;
    }

    public int getInt(String tag)
    {
        try
        {
            return mJson.getInt(tag);
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return 0;
    }

    public int length()
    {
        try
        {
            return mJson.length();
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return 0;
    }

    public boolean getBoolean(String tag)
    {
        try
        {
            return mJson.getBoolean(tag);
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return false;
    }

    public Calendar getCalendar(String tag)
    {
        try
        {
            String date = mJson.getString(tag);
            int ix1 = date.indexOf('(');
            int ix2 = date.indexOf(')');
            int ix3 = date.indexOf('+');

            if (ix3 != -1)
            {
                ix2 = ix3;
            }

            String subDate = date.substring(ix1 + 1, ix2);
            long millis = Long.valueOf(subDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);
            return calendar;
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }

        return Calendar.getInstance();
    }

    @Override
    public String toString()
    {
        return mRaw;
    }

}
