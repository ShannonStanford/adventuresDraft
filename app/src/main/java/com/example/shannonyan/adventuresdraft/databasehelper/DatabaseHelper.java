package com.example.shannonyan.adventuresdraft.databasehelper;

import com.example.shannonyan.adventuresdraft.ongoingflow.EventDataModel;

import java.util.ArrayList;

public class DatabaseHelper {

    //setters
    public void setCity(String city){

    }

    public void setPriceCap(String price){

    }

    public void setNumPeople(int num){

    }

    public void setPickUpInfo(String name, double startLat, double startLong){

    }

    public void setDropOffInfo(double endLat, double endLong){

    }

    public void setEventInfo(String url, String name, int rating){

    }

    public void setRideId(String id){

    }

    public void setPreferences(String choice, ArrayList<String> selections){

    }


    //getters
    public ArrayList<String> getFoodPref(){
        ArrayList<String> foodPref;
        return foodPref;
    }

    public int getNumPeeps(){
        int num;
        return num;
    }

    public float getStartLat(){
        float startLat;
        return startLat;
    }

    public float getStartLong(){
        float startLong;
        return startLong;
    }

    public double getEndLat(){
        double endLat;
        return endLat;
    }

    public double getEndLong(){
        double endLong;
        return endLong;
    }

    public String getCityInterest(){
        String city;
        return city;
    }

    public int getPriceCap(){
        int priceCap;
        return priceCap;
    }

    public String getPickUpName(){
        String name;
        return name;
    }

    public EventDataModel getEventInfo(){
        EventDataModel event;
        return event;
    }

}
