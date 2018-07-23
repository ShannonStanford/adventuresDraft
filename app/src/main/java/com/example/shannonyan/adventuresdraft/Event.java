package com.example.shannonyan.adventuresdraft;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
    //attributes
    public String title;
    public int numStars;
    public String storageUrl;

    //deserialize the JSON
    public static Event fromJSON(JSONObject json) throws JSONException {
        Event event = new Event();

        //extract and fill the values
        event.title = json.getString("EventName");
        event.numStars = json.getInt("EventRating");
        event.storageUrl = json.getString("EventImageStorageUrl");

        return event;
    }
}
