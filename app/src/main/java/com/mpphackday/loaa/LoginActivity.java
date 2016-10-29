package com.mpphackday.loaa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.mpphackday.loaa.helpers.AppHelper;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener
{

    private final String mTag = "LoginActivity";

    private EditText mPhoneNumber = null;
    private Button mSignUp = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPhoneNumber = (EditText)findViewById(R.id.phone_no_text);
        mSignUp = (Button)findViewById(R.id.signup_btn);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    5);
        }
        else
        {
            populatePhoneNumber();
        }

        mSignUp.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        if (mSignUp.equals(view))
        {
            doSignUp();
        }
    }

    public void doSignUp()
    {

    }

    public void onRequestPermissionsResult(int requestCode, java.lang.String[] permissions, int[] codes)
    {
        populatePhoneNumber();
    }

    public void populatePhoneNumber()
    {
        String phoneNumber = AppHelper.getMyPhoneNumber(this);

        if (phoneNumber != null && phoneNumber.length() > 0)
        {
            mPhoneNumber.setText(phoneNumber);
        }
    }

}
