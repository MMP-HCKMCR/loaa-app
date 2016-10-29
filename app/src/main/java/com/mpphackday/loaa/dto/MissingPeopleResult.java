package com.mpphackday.loaa.dto;

import com.mpphackday.loaa.web.JsonDocument;

/**
 * Created by Badgerati on 29/10/2016.
 */

public class MissingPeopleResult
{

    public String message = null;
    public String forenames = null;
    public String surname = null;
    public String gender = null;
    public String borough = null;
    public String dateWentMissing = null;


    public MissingPeopleResult(JsonDocument document)
    {
        message = document.getString("message");
        forenames = document.getString("forenames");
        surname = document.getString("surname");
        gender = document.getString("gender");
        borough = document.getString("borough");
        dateWentMissing = document.getString("dateWentMissing");
    }

    public String getFullname()
    {
        return forenames + " " + surname;
    }

}
