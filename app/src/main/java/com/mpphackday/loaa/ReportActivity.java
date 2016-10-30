package com.mpphackday.loaa;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.mpphackday.loaa.helpers.AppHelper;
import com.mpphackday.loaa.helpers.GeocodingLocation;
import com.mpphackday.loaa.helpers.ToastHelper;
import com.mpphackday.loaa.web.IAsyncTask;
import com.mpphackday.loaa.web.JsonDocument;
import com.mpphackday.loaa.web.WebRequest;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener,
        IAsyncTask.OnPostExecuteListener, ActivityCompat.OnRequestPermissionsResultCallback
{
    private final String mTag = "ReportActivity";
    private final ReportActivity _this = this;

    public double mLatitude = 0;
    public double mLongitude = 0;

    private String mAccountId = null;
    private ProgressBar mProgress = null;
    private EditText mFirstname = null;
    private EditText mSurname = null;
    private EditText mBirthYear = null;
    private EditText mTown = null;
    private EditText mLocation = null;
    public Button mReportBtn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mAccountId = getIntent().getStringExtra("id");

        mProgress = (ProgressBar)findViewById(R.id.progress);
        mFirstname = (EditText)findViewById(R.id.firstname_txt);
        mSurname = (EditText)findViewById(R.id.surname_txt);
        mBirthYear = (EditText)findViewById(R.id.birth_year_txt);
        mTown = (EditText)findViewById(R.id.town_city_txt);
        mLocation = (EditText)findViewById(R.id.location_txt);

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

        mReportBtn = (Button)findViewById(R.id.report_btn);
        mReportBtn.setOnClickListener(this);
    }

    public void getLocation()
    {
        try
        {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();

            mLocation.setText("Current");
        }
        catch (SecurityException sex)
        {
            ToastHelper.show(this, "GPS needs to be enabled");
        }
    }

    public void onClick(View v)
    {
        if (mLocation.getText().length() == 0)
        {
            ToastHelper.show(this, "No location passed");
            return;
        }

        doReport();
    }

    @Override
    public <T> void onPostExecute(IAsyncTask asyncTask, T result, String tag)
    {
        try
        {
            JsonDocument json = (JsonDocument)result;
            String message = json.getString("message");

            if (message != null && message.length() > 0)
            {
                ToastHelper.show(this, message);
                return;
            }

            mProgress.setVisibility(View.INVISIBLE);
            ToastHelper.show(this, "Person Reported Missing");
            finish();
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
            ToastHelper.show(this, "Submission unsuccessful");
        }
    }

    public void doReport()
    {
        try
        {
            mProgress.setVisibility(View.VISIBLE);

            // check location
            String location = mLocation.getText().toString();
            if (location.toLowerCase().equals("current"))
            {
                String firstname = mFirstname.getText().toString();
                String surname = mSurname.getText().toString();
                String birthYear = mBirthYear.getText().toString();
                String town = mTown.getText().toString();
                String data = "{\"accountId\":\"" + mAccountId + "\", \"latitude\":\"" + mLatitude + "\", \"longitude\":\"" + mLongitude + "\", \"firstname\":\"" + firstname + "\", \"surname\":\"" + surname + "\", \"birthYear\":\"" + birthYear + "\", \"town\":\"" + town + "\"}";
                WebRequest.send(AppHelper.URL + "missing/create", data, "POST", "seen", this);
            }
            else
            {
                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(location, getApplicationContext(), new ReportActivity.GeocoderHandler());
            }
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }
    }



    private class GeocoderHandler extends Handler
    {
        @Override
        public void handleMessage(Message message) {
            String latitude = null;
            String longitude = null;

            switch (message.what)
            {
                case 1:
                    Bundle bundle = message.getData();
                    latitude = bundle.getString("latitude");
                    longitude = bundle.getString("longitude");
                    break;

                default:
                    latitude = null;
                    longitude = null;
            }

            if (longitude == null || latitude == null)
            {
                mProgress.setVisibility(View.INVISIBLE);
                ToastHelper.show(_this, "Failed to find location");
                return;
            }

            Log.d(mTag, "LATITUDE: " + latitude + ", LONGITUDE: " + longitude);

            String firstname = mFirstname.getText().toString();
            String surname = mSurname.getText().toString();
            String birthYear = mBirthYear.getText().toString();
            String town = mTown.getText().toString();
            String data = "{\"accountId\":\"" + mAccountId + "\", \"latitude\":\"" + latitude + "\", \"longitude\":\"" + longitude + "\", \"firstname\":\"" + firstname + "\", \"surname\":\"" + surname + "\", \"birthYear\":\"" + birthYear + "\", \"town\":\"" + town + "\"}";
            WebRequest.send(AppHelper.URL + "missing/create", data, "POST", "seen", _this);
        }
    }
}
