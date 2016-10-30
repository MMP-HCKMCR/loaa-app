package com.mpphackday.loaa;

import android.Manifest;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mpphackday.loaa.dto.AccountResult;
import com.mpphackday.loaa.helpers.AppHelper;
import com.mpphackday.loaa.helpers.ToastHelper;
import com.mpphackday.loaa.web.IAsyncTask;
import com.mpphackday.loaa.web.JsonDocument;
import com.mpphackday.loaa.web.WebRequest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SubmitActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener, IAsyncTask.OnPostExecuteListener
{

    private final String mTag = "SubmitActivity";

    public String mGuid = null;
    public String mPersonId = null;
    public String mCurrentDate = null;
    public double mLatitude = 0;
    public double mLongitude = 0;

    public TextView mDate = null;
    public TextView mLocation = null;
    public EditText mDescription = null;
    public Button mSubmitBtn = null;
    public ProgressBar mProgress = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        mGuid = getIntent().getStringExtra("guid");
        mPersonId = getIntent().getStringExtra("personId");

        mDate = (TextView)findViewById(R.id.date_txt);
        mLocation = (TextView)findViewById(R.id.location_txt);
        mDescription = (EditText)findViewById(R.id.desc_txt);
        mProgress = (ProgressBar)findViewById(R.id.progress);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mCurrentDate = sdf.format(new Date());

        mDate.setText(mCurrentDate);

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

        mSubmitBtn = (Button)findViewById(R.id.submit_btn);
        mSubmitBtn.setOnClickListener(this);
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
            ToastHelper.show(this, "Thank You");
            finish();
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
            ToastHelper.show(this, "Submission unsuccessful");
        }
    }

    public void onClick(View view)
    {
        if (mSubmitBtn.equals(view))
        {
            doSubmit();
        }
    }

    public void doSubmit()
    {
        try
        {
            String desc = mDescription.getText().toString();

            mProgress.setVisibility(View.VISIBLE);
            String data = "{\"guid\":\"" + mGuid + "\", \"latitude\":\"" + mLatitude + "\", \"longitude\":\"" + mLongitude + "\", \"description\":\"" + desc + "\", \"date\":\"" + mCurrentDate + "\"}";
            WebRequest.send(AppHelper.URL + "missing/" + mPersonId, data, "PUT", "seen", this);
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
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
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();

            mLocation.setText(mLatitude + ", " + mLongitude);
        }
        catch (SecurityException sex)
        {
            ToastHelper.show(this, "GPS needs to be enabled");
        }
    }

}
