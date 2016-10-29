package com.mpphackday.loaa.helpers;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Badgerati on 29/10/2016.
 */

public abstract class AppHelper {

    public static String getMyPhoneNumber(Context context){
        TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

}
