package com.mpphackday.loaa;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.daprlabs.aaron.swipedeck.SwipeDeck;
import com.mpphackday.loaa.adapters.SwipeDeckAdapter;
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

    public ArrayList<MissingPeopleResult> mPeople = null;
    public String mGuid = null;

    public ProgressBar mProgress = null;
    public SwipeDeck mSwipeDeck = null;
    public SwipeDeckAdapter mSwipeAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mGuid = getIntent().getStringExtra("guid");

        mSwipeDeck = (SwipeDeck) findViewById(R.id.swipe_deck);
        mProgress = (ProgressBar)findViewById(R.id.progress);
        final Activity _this = this;

        mSwipeDeck.SWIPE_ENABLED = true;
        mSwipeDeck.setCallback(new SwipeDeck.SwipeDeckCallback() {
            @Override
            public void cardSwipedLeft(long stableId) {
                Log.d("MainActivity", "card was swiped left, position in adapter: " + stableId);

                MissingPeopleResult person = mPeople.get((int)stableId);

                Intent i = new Intent(_this, SubmitActivity.class);
                i.putExtra("guid", mGuid);
                i.putExtra("personId", person.id);
                startActivity(i);
            }

            @Override
            public void cardSwipedRight(long stableId) {
                Log.d("MainActivity", "card was swiped right, position in adapter: " + stableId);
            }
        });

        Button btn = (Button) findViewById(R.id.swipe_left_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeDeck.swipeTopCardLeft(180);
            }
        });

        Button btn2 = (Button) findViewById(R.id.swipe_right_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeDeck.swipeTopCardRight(180);
            }
        });


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
            mPeople = json.getMissingPeopleArray("missing");
            mProgress.setVisibility(View.INVISIBLE);

            ArrayList<String> names = new ArrayList<>(mPeople.size());
            for (int i = 0; i < mPeople.size(); i++)
            {
                names.add(mPeople.get(i).getFullname());
            }

            mSwipeAdapter = new SwipeDeckAdapter(names, this);
            mSwipeDeck.setAdapter(mSwipeAdapter);

            Log.d(mTag, "" + mPeople.size());
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
            String data = "{\"latitude\":\"" + latitude + "\", \"longitude\":\"" + longitude + "\"}";
            WebRequest.send(AppHelper.URL + "missing", data, "POST", "missing", this);
        }
        catch (SecurityException sex)
        {
            ToastHelper.show(this, "GPS needs to be enabled");
        }
    }

}
