package com.mpphackday.loaa;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ProgressBar;

import com.mpphackday.loaa.dto.AccountResult;
import com.mpphackday.loaa.helpers.AppHelper;
import com.mpphackday.loaa.helpers.ToastHelper;
import com.mpphackday.loaa.web.IAsyncTask;
import com.mpphackday.loaa.web.JsonDocument;
import com.mpphackday.loaa.web.WebRequest;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener, IAsyncTask.OnPostExecuteListener
{

    private final String mTag = "LoginActivity";

    private EditText mPhoneNumber = null;
    private Button mSignUp = null;
    private ProgressBar mProgress = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        SharedPreferences shared = getSharedPreferences("LOAA_GUID", MODE_PRIVATE);
        String guid = shared.getString("guid", "");


        mPhoneNumber = (EditText) findViewById(R.id.phone_no_text);
        mSignUp = (Button) findViewById(R.id.signup_btn);
        mProgress = (ProgressBar) findViewById(R.id.progress);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS},
                    5);
        } else {
            populatePhoneNumber();
        }

        mSignUp.setOnClickListener(this);

        if (guid != null && guid.length() > 0)
        {
            doLogin(guid);
        }
    }

    public void onClick(View view)
    {
        if (mSignUp.equals(view))
        {
            doSignUp();
        }
    }

    @Override
    public <T> void onPostExecute(IAsyncTask asyncTask, T result, String tag)
    {
        try
        {
            JsonDocument json = (JsonDocument)result;
            AccountResult accountResult = null;

            if (tag.equals("create"))
            {
                mProgress.setVisibility(View.INVISIBLE);
                accountResult = new AccountResult(json);
            }
            else if (tag.equals("login"))
            {
                mProgress.setVisibility(View.INVISIBLE);
                accountResult = new AccountResult(json);
            }

            if (accountResult.message != null && accountResult.message.length() > 0)
            {
                ToastHelper.show(this, accountResult.message);
                return;
            }

            if (accountResult.guid != null && accountResult.guid.length() > 0)
            {
                SharedPreferences.Editor editor = getSharedPreferences("LOAA_GUID", MODE_PRIVATE).edit();
                editor.putString("guid", accountResult.guid);
                editor.apply();

                Intent i = new Intent(this, HomeActivity.class);
                i.putExtra("id", accountResult.id);
                startActivity(i);
                finish();
            }
            else
            {
                ToastHelper.show(this, "Sign in unsuccessful");
            }
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
            ToastHelper.show(this, "Sign in unsuccessful");
        }
    }

    public void doLogin(String guid)
    {
        try
        {
            ToastHelper.show(this, "Signing you in");
            mProgress.setVisibility(View.VISIBLE);
            String data = "{\"token\":\"" + guid + "\"}";
            WebRequest.send(AppHelper.URL + "account/login", data, "POST", "login", this);
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }
    }

    public void doSignUp()
    {
        String number = mPhoneNumber.getText().toString();

        if (number == null || number.length() == 0)
        {
            ToastHelper.show(this, "No phone number");
            return;
        }

        try
        {
            mProgress.setVisibility(View.VISIBLE);
            String data = "{\"phoneNumber\":\"" + number + "\"}";
            WebRequest.send(AppHelper.URL + "account", data, "POST", "create", this);
        }
        catch (Exception ex)
        {
            Log.e(mTag, ex.getMessage());
        }
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
