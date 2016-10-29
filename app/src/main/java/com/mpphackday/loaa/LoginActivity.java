package com.mpphackday.loaa;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

import com.mpphackday.loaa.helpers.AppHelper;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private EditText mPhoneNumber = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPhoneNumber = (EditText)findViewById(R.id.phone_no_text);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    5);
        }
        else {
            populatePhoneNumber();
        }
    }

    public void onRequestPermissionsResult(int requestCode, java.lang.String[] permissions, int[] codes) {
        populatePhoneNumber();
    }

    public void populatePhoneNumber() {
        String phoneNumber = AppHelper.getMyPhoneNumber(this);

        if (phoneNumber != null && phoneNumber.length() > 0) {
            mPhoneNumber.setText(phoneNumber);
        }
    }

}
