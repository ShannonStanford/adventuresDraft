package com.example.shannonyan.adventuresdraft.ongoingflow;

import com.example.shannonyan.adventuresdraft.Api;
import com.example.shannonyan.adventuresdraft.constants.Database;

import org.json.JSONException;
import org.json.JSONObject;

public class EventDataModel {
    //attributes
    public String name;
    public int rating;
    public String downloadUrl;

    //deserialize the JSON
    public static EventDataModel fromJSON(JSONObject json) throws JSONException {
        EventDataModel eventDataModel = new EventDataModel();

        //extract and fill the values
        eventDataModel.name = json.getString(Database.NAME);
        eventDataModel.rating = json.getInt(Database.RATING);
        eventDataModel.downloadUrl = json.getString(Database.DOWNLOAD_URL);

        return eventDataModel;
    }
}
