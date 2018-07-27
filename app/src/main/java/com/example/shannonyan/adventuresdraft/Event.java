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
        event.name = json.getString("name");
        event.rating = json.getInt("rating");
        event.downloadUrl = json.getString("downloadUrl");

        return event;
    }
}
