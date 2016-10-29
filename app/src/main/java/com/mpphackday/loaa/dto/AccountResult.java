package com.mpphackday.loaa.dto;

import com.mpphackday.loaa.web.JsonDocument;

/**
 * Created by Badgerati on 29/10/2016.
 */

public class AccountResult
{

    public String message = null;
    public String guid = null;
    public String phoneNumber = null;
    public String forename = null;
    public String surname = null;

    public AccountResult(JsonDocument document)
    {
        message = document.getString("message");
        guid = document.getString("guid");
        phoneNumber = document.getString("phoneNumber");
        forename = document.getString("forename");
        surname = document.getString("surname");
    }

}
