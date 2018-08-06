package com.example.shannonyan.adventuresdraft;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
    //attributes
    public String name;
    public int rating;
    public String downloadUrl;

    //deserialize the JSON
    public static Event fromJSON(JSONObject json) throws JSONException {
        Event event = new Event();

        //extract and fill the values
        event.name = json.getString(Constants.NAME);
        event.rating = json.getInt(Constants.RATING);
        event.downloadUrl = json.getString(Constants.DOWNLOAD_URL);

        return event;
    }
}
