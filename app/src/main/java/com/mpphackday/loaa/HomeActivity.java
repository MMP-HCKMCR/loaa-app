package com.mpphackday.loaa;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mpphackday.loaa.dto.MissingPeopleResult;
import com.mpphackday.loaa.helpers.AppHelper;
import com.mpphackday.loaa.helpers.ToastHelper;
import com.mpphackday.loaa.web.IAsyncTask;
import com.mpphackday.loaa.web.JsonDocument;
import com.mpphackday.loaa.web.WebRequest;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        IAsyncTask.OnPostExecuteListener
{

    private final String mTag = "HomeActivity";

    public ProgressBar mProgress = null;
    public ListView mListView = null;
    public ArrayAdapter<String> mAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mProgress = (ProgressBar)findViewById(R.id.progress);
        mListView = (ListView)findViewById(R.id.missing_list);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    6);
        }
        else
        {
            getLocation();
        }
    }

    @Override
    public <T> void onPostExecute(IAsyncTask asyncTask, T result, String tag)
    {
        try
        {
            JsonDocument json = (JsonDocument)result;
            ArrayList<MissingPeopleResult> people = json.getMissingPeopleArray("missing");
            mProgress.setVisibility(View.INVISIBLE);

            String[] names = new String[people.size()];
            for (int i = 0; i < people.size(); i++)
            {
                names[i] = people.get(i).getFullname();
            }

            mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
            mListView.setAdapter(mAdapter);

            Log.d(mTag, "" + people.size());
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
            ToastHelper.show(this, "Retrieving missing people unsuccessful");
        }
    }

    public void onRequestPermissionsResult(int requestCode, java.lang.String[] permissions, int[] codes)
    {
        if (requestCode == 6)
        {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        7);
            }
            else
            {
                getLocation();
            }
        }
    }

    public void getLocation()
    {
        try
        {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            Log.d(mTag, "LAT: " + latitude + ", LONG: " + longitude);

            WebRequest.send(AppHelper.URL + "missing", "{}", "POST", "missing", this);
        }
        catch (SecurityException sex)
        {
            ToastHelper.show(this, "GPS needs to be enabled");
        }
    }

}
