package com.example.shannonyan.adventuresdraft;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

class Uber {
    //attributes
    public String city;
    public Location endLoc;
    public int numPeeps;
    public int priceCap;
    public String rideId;
    public Location startLoc;
    public String pickUpName;

    //deserialize the JSON
    public static Uber fromJSON(JSONObject json) throws JSONException {
        Uber uber = new Uber();

        //extract and fill the values
        uber.city = json.getString(com.example.shannonyan.adventuresdraft.constants.Database.CITY_OF_INTEREST);
        uber.numPeeps = json.getInt(com.example.shannonyan.adventuresdraft.constants.Database.NUM_PEEPS);
        uber.pickUpName = json.getString(com.example.shannonyan.adventuresdraft.constants.Database.PICKUP);
        uber.priceCap = json.getInt(com.example.shannonyan.adventuresdraft.constants.Database.PRICECAP);
        uber.rideId = json.getString(com.example.shannonyan.adventuresdraft.constants.Database.RIDE_ID);
//        uber.endLoc = (Location) json.get(Api.END_LOC);
        return uber;
    }
}
