package com.example.shannonyan.adventuresdraft.databasehelper;

import android.support.annotation.NonNull;

import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.ongoingflow.EventDataModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DatabaseHelper {

    private static DatabaseReference uber = FirebaseDatabase.getInstance().getReference().child(com.example.shannonyan.adventuresdraft.constants.Database.TRIPS).child(com.example.shannonyan.adventuresdraft.constants.Database.TEST_TRIPS).child(com.example.shannonyan.adventuresdraft.constants.Database.UBER);
    private static DatabaseReference itinerary = FirebaseDatabase.getInstance().getReference().child(Database.ITINERARY_ARRAY_NAME);
    private static DatabaseReference events = FirebaseDatabase.getInstance().getReference().child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT);
    private static DatabaseReference test_user = FirebaseDatabase.getInstance().getReference().child(Database.USER).child(Database.TEST_USER);

    static String city;
    static String priceCap;
    static String name;
    static int num;
    static ArrayList<String> itineraryList;
    static EventDataModel eventDataModel;
    static float startLat;
    static float startLong;
    static float endLat;
    static float endLong;
    static ArrayList<String> foodPref;

    //setters
    public static void setCity(String city){
        uber.child(com.example.shannonyan.adventuresdraft.constants.Database.CITY_OF_INTEREST).setValue(city);
    }

    public static void setPriceCap(String price){
        uber.child(Database.PRICECAP).setValue(price);
    }

    public static void setNumPeople(int numLocal){
        uber.child(Database.NUM_PEEPS).setValue(numLocal);
        num = numLocal;
    }


    public static void setPickUpInfo(String name, double startLat, double startLong){
        uber.child(com.example.shannonyan.adventuresdraft.constants.Database.PICKUP).setValue(name);
        uber.child(com.example.shannonyan.adventuresdraft.constants.Database.START_LOC).child(com.example.shannonyan.adventuresdraft.constants.Database.LAT).setValue(startLat);
        uber.child(com.example.shannonyan.adventuresdraft.constants.Database.START_LOC).child(com.example.shannonyan.adventuresdraft.constants.Database.LONG).setValue(startLong);
    }

    public static void setDropOffInfo(double endLat, double endLong){
        uber.child(Database.END_LOC).child(Database.LAT).setValue(endLat);
        uber.child(Database.END_LOC).child(Database.LONG).setValue(endLong);
    }

    public static void setEventInfo(String url, String name, int rating){
        events.child(Database.DOWNLOAD_URL).setValue(url);
        events.child(Database.NAME).setValue(name);
        events.child(Database.RATING).setValue(rating);
    }

    public static ArrayList<String> getFoodPref(){
        test_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodPref = (ArrayList<String>) dataSnapshot.child(Database.FOOD_PREF).getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return foodPref;
    }

//    public static double getEndLat(){
//
//        return endLat;
//    }
//
//    public static double getEndLong(){
//        double endLong;
//        return endLong;
//    }


    public static void setRideId(String id){
        uber.child(Database.RIDE_ID).setValue(id);
    }

    public static void setPreferences(String choice, ArrayList<String> selections){
        test_user.child(choice).setValue(selections);
    }

    public static void setItinerary(ArrayList<String> itineraryList){
        itinerary.setValue(itineraryList);
    }

    public static float getStartLat(){
        uber.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startLat = (float) dataSnapshot.child(Database.START_LOC).child(Database.LAT).getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return startLat;
    }

    public static float getStartLong(){
        uber.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startLong = (float) dataSnapshot.child(Database.START_LOC).child(Database.LONG).getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return startLong;
    }

    public static ArrayList<String> getItinerary(){
        itinerary.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itineraryList = (ArrayList<String>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return itineraryList;
    }

    public static int getNumPeeps(){
//        uber.child(Database.NUM_PEEPS).addListenerForSingleValueEvent(new ValueEventListener(){
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                num = dataSnapshot.getValue(int.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
        return num;
    }

    public static String getCityInterest(){
        uber.child(com.example.shannonyan.adventuresdraft.constants.Database.CITY_OF_INTEREST).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                city = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return city;
    }

    public static String getPriceCap(){
        uber.child(Database.PRICECAP).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                priceCap = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return priceCap;
    }

    public static String getPickUpName(){

        uber.child(Database.PICKUP).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return name;
    }

    public static EventDataModel getEventInfo(){
        events.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventDataModel = dataSnapshot.getValue(EventDataModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return eventDataModel;
    }

}
