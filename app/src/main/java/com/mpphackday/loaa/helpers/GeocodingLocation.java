package com.mpphackday.loaa.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Badgerati on 30/10/2016.
 */

public class GeocodingLocation
{

    private static final String TAG = "GeocodingLocation";

    public static void getAddressFromLocation(final String locationAddress,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());

                String latitude = null;
                String longitude = null;

                try
                {
                    List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);

                    if (addressList != null && addressList.size() > 0)
                    {
                        Address address = addressList.get(0);
                        latitude = "" + address.getLatitude();
                        longitude = "" + address.getLongitude();
                    }
                }
                catch (IOException e)
                {
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                }
                finally
                {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("latitude", latitude);
                    bundle.putString("longitude", longitude);
                    message.setData(bundle);
                    message.sendToTarget();
                }
            }
        };

        thread.start();
    }

}
