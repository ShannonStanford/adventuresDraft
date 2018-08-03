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
        uber.city = json.getString(Constants.CITY_OF_INTEREST);
        uber.numPeeps = json.getInt(Constants.NUM_PEEPS);
        uber.pickUpName = json.getString(Constants.PICKUP);
        uber.priceCap = json.getInt(Constants.PRICECAP);
        uber.rideId = json.getString(Constants.RIDE_ID);
        uber.endLoc = (Location) json.get(Constants.END_LOC);


        return uber;
    }
}
