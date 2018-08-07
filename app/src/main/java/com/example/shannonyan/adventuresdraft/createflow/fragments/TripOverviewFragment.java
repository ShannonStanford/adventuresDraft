package com.example.shannonyan.adventuresdraft.createflow.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.shannonyan.adventuresdraft.Api;
import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.UberClient;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.rides.client.model.PriceEstimate;
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

import static com.example.shannonyan.adventuresdraft.constants.Database.LONG;

public class TripOverviewFragment extends Fragment {

    private TextView pickupAns;
    private TextView priceAns;
    private TextView cityAns;
    private TextView numPeep;
    private TextView numPeepAns;
    private DatabaseReference mDatabase;
    public Button create;
    public final static String YELP_KEY= "Bearer q0zcjpMA9Yfk8Ek0RQcmKX1dyfT-erS7RBpHeaizy0z5OirjaGHO1NThswb9Mi8EXyekovS1HUA4UGsGVUpZ0OS0onBLR2xIzy2ur7XtIIPspOXuXpZyy39YKahQW3Yx";
    public final static String SEARCH_API_URL = "https://api.yelp.com/v3/businesses/search";
    public ArrayList<String> food;
    public float numPeeps;
    public String priceRange;
    public static final int priceRange1H = 10;
    public static final int priceRange2L = 11;
    public static final int priceRange2H = 30;
    public static final int priceRange3L = 31;
    public static final int priceRange3H = 60;
    public static final int limit = 50;
    public UberClient uberClient;
    public RidesService service;
    public double startLat;
    public double startLong;
    public int highEstimate;
    public boolean found = false;

    public TripOverviewFragment() { }

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
        numPeep = view.findViewById(R.id.num_peeps);
        numPeepAns = view.findViewById(R.id.num_peeps_ans);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        create = (Button) view.findViewById(R.id.create);
        uberClient = UberClient.getUberClientInstance(getContext());
        service = uberClient.service;

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // test size
                        food = (ArrayList<String>) dataSnapshot.child(Database.USER).child(Database.TEST_USER).child(Database.FOOD_PREF).getValue();
                        numPeeps = dataSnapshot.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.NUM_PEEPS).getValue(float.class);
                        startLat = dataSnapshot.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.START_LOC).child(Database.LAT).getValue(long.class);
                        startLong = dataSnapshot.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.START_LOC).child(Database.LONG).getValue(long.class);
                        StringBuilder foodParam = new StringBuilder();
                        for (int i = 0; i < food.size(); i++) {
                            foodParam.append(food.get(i));
                            if (i != food.size() - 1) {
                                foodParam.append(",");
                            }
                        }
                        CreateEvent(foodParam);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child(Database.CITY_OF_INTEREST).getValue(String.class));
                priceAns.setText("$" + dataSnapshot.child(Database.PRICECAP).getValue(String.class));
                pickupAns.setText(dataSnapshot.child(Database.PICKUP).getValue(String.class));
                numPeepAns.setText(String.valueOf(dataSnapshot.child(Database.NUM_PEEPS).getValue(Integer.class)));
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
        setValues();
    }

    public void setValues(){
        mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child(Database.CITY_OF_INTEREST).getValue(String.class));
                priceAns.setText(dataSnapshot.child(Database.PRICECAP).getValue(String.class));
                pickupAns.setText(dataSnapshot.child(Database.PICKUP).getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void CreateEvent(StringBuilder foodPar) {
        // assembling the foodParam to put in for categories in the search query
        int priceCap = Integer.parseInt(String.valueOf(priceAns.getText()));
        final int uberCap = priceCap/ Api.UBER_DIVID; // one way uber cap
        float foodCap = priceCap/(Api.FOOD_CAP_DIVID * numPeeps);
        // determine the priceRange to query with
        priceRange = PriceRange(foodCap);
        OkHttpClient client = new OkHttpClient();
        try {
            String url = BuildUri(foodPar);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader(Api.AUTHORIZATION, YELP_KEY)
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
                            double endLat = item.getJSONObject(Database.COORDINATES).getLong(Database.LATITUDE);
                            double endLon = item.getJSONObject(Database.COORDINATES).getLong(Database.LONGITUDE);
                            List<PriceEstimate> priceEstimates = service.getPriceEstimates((float) startLat, (float) startLong, (float) endLat, (float) endLon).execute().body().getPrices();
                            highEstimate = -1;
                            for (int i = 0; i < priceEstimates.size(); i++) {
                                if (priceEstimates.get(i).getDisplayName().equals(Database.UBERX)) {
                                    highEstimate = priceEstimates.get(i).getHighEstimate();
                                }
                            }
                            if (highEstimate <= uberCap) {
                                found = true;
                                mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.END_LOC).child(Database.LAT).setValue(endLat);
                                mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.END_LOC).child(Database.LONG).setValue(endLon);
                                mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT).child(Database.DOWNLOAD_URL).setValue(item.get(Database.IMAGE_URL));
                                mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT).child(Database.NAME).setValue(item.get(Database.NAME));
                                mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT).child(Database.RATING).setValue(item.get(Database.RATING));
                                Intent intent = new Intent(getActivity(),  com.example.shannonyan.adventuresdraft.ongoingflow.StartActivity.class);
                                startActivity(intent);
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

    public String PriceRange(float foodCap) {
        if (foodCap <= priceRange1H)
            return Api.PRICE_TIER_1;
        else if (foodCap >= priceRange2L && foodCap <= priceRange2H)
            return Api.PRICE_TIER_2;
        else if(foodCap >= priceRange3L && foodCap <= priceRange3H)
            return Api.PRICE_TIER_3;
        else
            return Api.PRICE_TIER_4;
    }

    public String BuildUri(StringBuilder foodPar) throws URISyntaxException {
        Random ran = new Random();
        int ranN = ran.nextInt(limit);
        URIBuilder builder = new URIBuilder(SEARCH_API_URL);
        builder.addParameter(Api.TERM, Api.RESTAURANT);
        builder.addParameter(Api.LOCATION, String.valueOf(cityAns.getText()));
        builder.addParameter(Api.CATEGORIES, foodPar.toString());
        builder.addParameter(Api.LIMIT, String.valueOf(limit));
        builder.addParameter(Api.OFFSET, String.valueOf(ranN));
        builder.addParameter(Api.PRICE, priceRange);
        String url = builder.build().toString();
        return url;
    }

    public static TripOverviewFragment newInstance() {
        TripOverviewFragment frag = new TripOverviewFragment();
        return frag;
    }
}