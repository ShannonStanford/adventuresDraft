package com.example.shannonyan.adventuresdraft;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.uber.sdk.rides.client.model.PriceEstimate;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import com.uber.sdk.rides.client.services.RidesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;


public class CreateThirdFragment extends Fragment {

    private TextView pickupAns;
    private TextView priceAns;
    private TextView cityAns;
    private DatabaseReference mDatabase;
    public Button create;
    public AsyncHttpClient client;
    public final static String YELP_KEY= "@string/yelp_key";
    public final static String SEARCH_API_URL = "@string/search_api_url";
    public List<String> food;
    public float numPeeps;
    public String priceRange;
    public static final int priceRange1H = 10;
    public static final int priceRange2L = 11;
    public static final int priceRange2H = 30;
    public static final int priceRange3L = 31;
    public static final int priceRange3H = 60;
    public static final int priceRange4L = 61;
    public UberClient uberClient;
    public RidesService service;
    public float startLat;
    public float startLong;

    public CreateThirdFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_third, container, false);
        pickupAns = view.findViewById(R.id.pickup_ans);
        priceAns = view.findViewById(R.id.price_ans);
        cityAns = view.findViewById(R.id.city_ans);
        create = view.findViewById(R.id.create);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        create = (Button) view.findViewById(R.id.create);
        uberClient = UberClient.getUberClientInstance(getContext());
//        uberClient = UberClient.getUberClientInstance(getActivity());
        service = uberClient.service;
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: get priceCap and users food preferences
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // test size
//                        food = dataSnapshot.child("user").child("testUser").getValue(List<String>.class);
                        numPeeps = dataSnapshot.child("trips").child("testTrip").child("uber").child("numPeeps").getValue(float.class);
                        startLat = dataSnapshot.child("trips").child("testTrip").child("uber").child("startLoc").child("lat").getValue(long.class);
                        startLong = dataSnapshot.child("trips").child("testTrip").child("uber").child("startLoc").child("long").getValue(long.class);

                        CreateEvent();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                // assembling the foodParam to put in for categories in the search query
//                String[] food2 = {"chinese", "american"};
//                StringBuilder foodParam = new StringBuilder();
//                for (int i = 0; i < food2.length; i++) {
//                    foodParam.append(food2[i]);
//                    if (i != food2.length - 1) {
//                        foodParam.append(",");
//                    }
//                }
//                int priceCap = Integer.parseInt(String.valueOf(priceAns.getText()));
//                final int uberCap = priceCap/4; // one way uber cap
//                float foodCap = priceCap/(2 * numPeeps);
//                // determine the priceRange to query with
//                if (foodCap <= priceRange1H)
//                    priceRange = "1";
//                else if (foodCap >= priceRange2L && foodCap <= priceRange2H)
//                    priceRange = "2";
//                else if(foodCap >= priceRange3L && foodCap <= priceRange3H)
//                    priceRange = "3";
//                else
//                    priceRange = "4";
//
//                //TODO: pass in above info into yelp API call and determine which restaurant/event from response
//                client = new AsyncHttpClient();
//                RequestParams params = new RequestParams();
//                // add token bearer authentication header
//                client.addHeader("Authorization", "Bearer " + YELP_KEY);
//                params.put("term", "restaurant");
//                params.put("location", cityAns.getText());
//                params.put("categories", foodParam);
//                params.put("price", priceRange);
//                params.put("limit", 50);
//                params.put("offset", 0);
//
//                client.get(SEARCH_API_URL, params, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        try {
//                            JSONArray results = response.getJSONArray("businesses");
//                            boolean[] map = new boolean[results.length()];
//                            boolean found = false;
//                            Random rand = new Random();
//                            while(!found) {
//                                int n = rand.nextInt(results.length());
//                                if (map[n]) n = rand.nextInt(results.length());
//                                map[n] = true;
//                                JSONObject item = results.getJSONObject(n);
//                                float endLat = item.getJSONObject("coordinates").getLong("latitude");
//                                float endLon = item.getJSONObject("coordinates").getLong("longitude");
//                                // get uber price estimates to figure out if the location fits under the priceCap
//                                List<PriceEstimate> priceEstimates = service.getPriceEstimates(startLat, startLong, endLat, endLon).execute().body().getPrices();
//                                int highEstimate = priceEstimates.get(6).getHighEstimate(); // get the normal uberX price estimate
//                                if (highEstimate <= uberCap) {
//                                    found = true;
//                                    mDatabase.child("trips").child("testTrip").child("uber").child("endLoc").child("lat").setValue(endLat);
//                                    mDatabase.child("trips").child("testTrip").child("uber").child("endLoc").child("long").setValue(endLon);
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//
//                    }
//                });
                //TODO: store relevant data about restaurant/event in database

//                CreateEvent();
                //launch start activity

            }
        });

        mDatabase.child("trips").child("testTrip").child("uber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child("cityOfInterest").getValue(String.class));
                priceAns.setText(dataSnapshot.child("priceCap").getValue(String.class));
                pickupAns.setText(dataSnapshot.child("pickUpName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDatabase.child("trips").child("testTrip").child("uber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child("cityOfInterest").getValue(String.class));
                priceAns.setText(dataSnapshot.child("priceCap").getValue(String.class));
                pickupAns.setText(dataSnapshot.child("pickUpName").getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void CreateEvent() {
        // assembling the foodParam to put in for categories in the search query
        String[] food2 = {"chinese", "american"};
        StringBuilder foodParam = new StringBuilder();
        for (int i = 0; i < food2.length; i++) {
            foodParam.append(food2[i]);
            if (i != food2.length - 1) {
                foodParam.append(",");
            }
        }
        int priceCap = Integer.parseInt(String.valueOf(priceAns.getText()));
        final int uberCap = priceCap/4; // one way uber cap
        float foodCap = priceCap/(2 * numPeeps);
        // determine the priceRange to query with
        if (foodCap <= priceRange1H)
            priceRange = "1";
        else if (foodCap >= priceRange2L && foodCap <= priceRange2H)
            priceRange = "2";
        else if(foodCap >= priceRange3L && foodCap <= priceRange3H)
            priceRange = "3";
        else
            priceRange = "4";

        //TODO: pass in above info into yelp API call and determine which restaurant/event from response
        client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        // add token bearer authentication header
        client.addHeader("Authorization", "Bearer " + YELP_KEY);
        params.put("term", "restaurant");
        params.put("location", cityAns.getText());
        params.put("categories", foodParam);
        params.put("price", priceRange);
        params.put("limit", 50);
        params.put("offset", 0);

        client.get(SEARCH_API_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("businesses");
                    boolean[] map = new boolean[results.length()];
                    boolean found = false;
                    Random rand = new Random();
                    while (!found) {
                        int n = rand.nextInt(results.length());
                        if (map[n]) n = rand.nextInt(results.length());
                        map[n] = true;
                        JSONObject item = results.getJSONObject(n);
                        float endLat = item.getJSONObject("coordinates").getLong("latitude");
                        float endLon = item.getJSONObject("coordinates").getLong("longitude");
                        // get uber price estimates to figure out if the location fits under the priceCap
                        List<PriceEstimate> priceEstimates = service.getPriceEstimates(startLat, startLong, endLat, endLon).execute().body().getPrices();
                        int highEstimate = priceEstimates.get(6).getHighEstimate(); // get the normal uberX price estimate
                        if (highEstimate <= uberCap) {
                            found = true;
                            mDatabase.child("trips").child("testTrip").child("uber").child("endLoc").child("lat").setValue(endLat);
                            mDatabase.child("trips").child("testTrip").child("uber").child("endLoc").child("long").setValue(endLon);
                        }
                    }
                    Intent intent = new Intent(getActivity(), StartActivity.class);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }
        });
    }

    public static CreateThirdFragment newInstance() {
        CreateThirdFragment frag = new CreateThirdFragment();
        return frag;
    }
}