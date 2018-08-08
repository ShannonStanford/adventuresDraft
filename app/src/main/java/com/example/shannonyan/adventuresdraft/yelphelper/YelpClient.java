package com.example.shannonyan.adventuresdraft.yelphelper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.ActionProvider;
import android.view.ContextMenu;

import com.example.shannonyan.adventuresdraft.UberClient;
import com.example.shannonyan.adventuresdraft.constants.Api;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.rides.client.model.PriceEstimate;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import com.uber.sdk.rides.client.services.RidesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.client.utils.URIBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public final class YelpClient {

    private static YelpClient yelpClientInstance;
    private DatabaseReference mDatabase;
    private boolean firstEvent;
    private String priceRange;
    private ArrayList<String> food;
    private UberClient uberClient;
    private RidesService service;
    private double startLat;
    private double startLong;
    private float numPeeps;
    private String totalCap;
    private int highEstimate;
    private Context contextActivity;

    public boolean found = false;
    public String city;

    private YelpClient(Context context, final String eventType, boolean first){
        contextActivity = context;
        uberClient = UberClient.getUberClientInstance(context);
        service = uberClient.service;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Create(eventType,first);
    }

    public void Create(final String eventType, boolean first){
        firstEvent = first;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                food = (ArrayList<String>) dataSnapshot.child(Database.USER).child(Database.TEST_USER).child(Database.FOOD_PREF).getValue();
                numPeeps = dataSnapshot.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.NUM_PEEPS).getValue(float.class);
                startLat = dataSnapshot.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.START_LOC).child(Database.LAT).getValue(long.class);
                startLong = dataSnapshot.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.START_LOC).child(Database.LONG).getValue(long.class);
                city = dataSnapshot.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.CITY_OF_INTEREST).getValue(String.class);
                totalCap = dataSnapshot.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.PRICECAP).getValue(String.class);
                StringBuilder foodParam = new StringBuilder();
                for (int i = 0; i < food.size(); i++) {
                    foodParam.append(food.get(i));
                    if (i != food.size() - 1) {
                        foodParam.append(",");
                    }
                }
                CreateEvent(foodParam, eventType);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //TODO: optimize 3 pricing algorithms below
    public int determineUberCap(int priceCap){
        return priceCap/ Api.UBER_DIVID;
    }
    public float determineFoodCap(int priceCap){
        return priceCap/(Api.FOOD_CAP_DIVID * numPeeps);
    }
    public float determineEventCap(int priceCap){
        return priceCap/(Api.FOOD_CAP_DIVID * numPeeps);
    }

    public void CreateEvent(StringBuilder foodPar, String eventType) {
        //determine/set priceCap vars required for algorithm
        int priceCap = Integer.parseInt(String.valueOf(totalCap));
        final int uberCap = determineUberCap(priceCap);

        if(eventType.equals(Database.EVENT_TYPE_NORM)){
            float eventCap = determineEventCap(priceCap);
            priceRange = PriceRange(eventCap);
        }else{
            float foodCap = determineFoodCap(priceCap);
            priceRange = PriceRange(foodCap);
        }


        OkHttpClient client = new OkHttpClient();
        try {
            String url = BuildUri(foodPar, eventType);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader(Api.AUTHORIZATION, Api.YELP_KEY)
                    .addHeader(Api.CACHE_CONTROL, Api.NO_CACHE)
                    .addHeader(Api.POSTMAN, Api.POSTMAN_TOKEN)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonData = response.body().string();
                    JSONArray results = null;
                    try {
                        JSONObject jsonObject = new JSONObject(jsonData);
                        results = jsonObject.getJSONArray(Api.BUSINESS);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                    boolean[] map = new boolean[results.length()];
                    Random rand = new Random();

                    while (!found) {
                        int n = rand.nextInt(results.length());
                        // keep creating new random numbers until one is created that has not been visited yet.
                        while (map[n]) {
                            n = rand.nextInt(results.length());
                        }
                        map[n] = true;
                        try {
                            JSONObject item = results.getJSONObject(n);
                            float endLat = item.getJSONObject(Database.COORDINATES).getLong(Database.LATITUDE);
                            float endLon = item.getJSONObject(Database.COORDINATES).getLong(Database.LONGITUDE);
                            PriceEstimatesResponse response2 = service.getPriceEstimates((float) startLat, (float) startLong, (float) endLat, (float) endLon).execute().body();
                            List<PriceEstimate> priceEstimates = response2.getPrices();
                            highEstimate = -1;
                            for (int i = 0; i < priceEstimates.size(); i++) {
                                if (priceEstimates.get(i).getDisplayName().equals(Database.UBERX)) {
                                    highEstimate = priceEstimates.get(i).getHighEstimate();
                                }
                            }
                            if (highEstimate <= uberCap) {
                                found = true;
                                if(firstEvent){
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.END_LOC).child(Database.LAT).setValue(endLat);
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.END_LOC).child(Database.LONG).setValue(endLon);
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT).child(Database.DOWNLOAD_URL).setValue(item.get(Database.IMAGE_URL));
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT).child(Database.NAME).setValue(item.get(Database.NAME));
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT).child(Database.RATING).setValue(item.get(Database.RATING));
                                    Intent intent = new Intent(contextActivity,  com.example.shannonyan.adventuresdraft.ongoingflow.StartActivity.class);
                                    contextActivity.startActivity(intent);
                                }else {
                                    //TODO: store old end lat/long from database into vars then set start lat/long in database to those values before writing new end lat/long to database
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.END_LOC).child(Database.LAT).setValue(endLat);
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.END_LOC).child(Database.LONG).setValue(endLon);
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT).child(Database.DOWNLOAD_URL).setValue(item.get(Database.IMAGE_URL));
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT).child(Database.NAME).setValue(item.get(Database.NAME));
                                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT).child(Database.RATING).setValue(item.get(Database.RATING));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public String BuildUri(StringBuilder foodPar, String eventType) throws URISyntaxException {
        if(eventType.equals(Database.EVENT_TYPE_NORM)){
            Random ran = new Random();
            int ranN = ran.nextInt(Api.YELP_LIMIT);
            URIBuilder builder = new URIBuilder(Api.SEARCH_API_URL);
            builder.addParameter(Api.TERM, "Active Life");
            builder.addParameter(Api.LOCATION, String.valueOf(city));
            builder.addParameter(Api.LIMIT, String.valueOf(Api.YELP_LIMIT));
            builder.addParameter(Api.OFFSET, "0");
            builder.addParameter(Api.PRICE, priceRange);
            String url = builder.build().toString();
            return url;
        }else{
            Random ran = new Random();
            int ranN = ran.nextInt(Api.YELP_LIMIT);
            URIBuilder builder = new URIBuilder(Api.SEARCH_API_URL);
            builder.addParameter(Api.TERM, Api.RESTAURANT);
            builder.addParameter(Api.LOCATION, String.valueOf(city));
            builder.addParameter(Api.CATEGORIES, foodPar.toString());
            builder.addParameter(Api.LIMIT, String.valueOf(Api.YELP_LIMIT));
            builder.addParameter(Api.OFFSET, "0");
            builder.addParameter(Api.PRICE, priceRange);
            String url = builder.build().toString();
            return url;
        }
    }

    public String PriceRange(float foodCap) {
        if (foodCap <= Api.PRICE_RANGE_1H)
            return Api.PRICE_TIER_1;
        else if (foodCap >= Api.PRICE_RANGE_2L && foodCap <= Api.PRICE_RANGE_2H)
            return Api.PRICE_TIER_2;
        else if(foodCap >= Api.PRICE_RANGE_3L && foodCap <= Api.PRICE_RANGE_3H)
            return Api.PRICE_TIER_3;
        else
            return Api.PRICE_TIER_4;
    }

    public static YelpClient getYelpClientInstance(Context context, String eventType, boolean first){
        if(null == yelpClientInstance){
            yelpClientInstance = new YelpClient(context, eventType, first);
        }
        return yelpClientInstance;
    }

    public void printSingleton() {System.out.println("Inside print yelpClient");}

}
