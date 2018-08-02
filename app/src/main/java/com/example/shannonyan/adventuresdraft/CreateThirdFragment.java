package com.example.shannonyan.adventuresdraft;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.gson.JsonObject;
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

public class CreateThirdFragment extends Fragment {

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
    public UberClient uberClient;
    public RidesService service;
    public double startLat;
    public double startLong;
    public int highEstimate;
    public boolean found = false;

    private static final String TRIPS = "trips";
    private static final String TEST_TRIP = "testTrip";
    private static final String TEST_USER = "testUser";
    private static final String UBER = "uber";
    private static final String NUM_PEEPS = "numPeeps";
    private static final String START_LOC = "startLoc";
    private static final String END_LOC = "endLoc";
    private static final String LAT = "lat";
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String COORDINATES = "coordinates";
    private static final String LONG = "long";
    private static final String FOOD_PREF = "foodPref";
    private static final String USER = "user";
    private static final String EVENT = "event";
    private static final String DOWNLOAD_URL = "downloadUrl";
    private static final String NAME = "name";
    private static final String RATING = "rating";
    private static final String IMAGE_URL = "image_url";
    private static final String UBERX = "UberX";
    private static final String PRICE_CAP = "priceCap";
    private static final String PICKUP_NAME = "pickUpName";
    private static final String CITY_OF_INTEREST = "cityOfInterest";

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
        numPeep = view.findViewById(R.id.num_peeps);
        numPeepAns = view.findViewById(R.id.num_peeps_ans);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        create = (Button) view.findViewById(R.id.create);
        uberClient = UberClient.getUberClientInstance(getContext());
        service = uberClient.service;

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: get priceCap and users food preferences
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // test size
                        food = (ArrayList<String>) dataSnapshot.child(USER).child(TEST_TRIP).child(FOOD_PREF).getValue();
                        numPeeps = dataSnapshot.child(TRIPS).child(TEST_TRIP).child(UBER).child(NUM_PEEPS).getValue(float.class);
                        startLat = dataSnapshot.child(TRIPS).child(TEST_TRIP).child(UBER).child(START_LOC).child(LAT).getValue(long.class);
                        startLong = dataSnapshot.child(TRIPS).child(TEST_TRIP).child(UBER).child(START_LOC).child(LONG).getValue(long.class);
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

        mDatabase.child(TRIPS).child(TEST_TRIP).child(UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child(CITY_OF_INTEREST).getValue(String.class));
                priceAns.setText("$" + dataSnapshot.child(PRICE_CAP).getValue(String.class));
                pickupAns.setText(dataSnapshot.child(PICKUP_NAME).getValue(String.class));
                numPeepAns.setText(String.valueOf(dataSnapshot.child(NUM_PEEPS).getValue(Integer.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        setValues();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setValues();
    }

    public void setValues(){
        mDatabase.child(TRIPS).child(TEST_TRIP).child(UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child(CITY_OF_INTEREST).getValue(String.class));
                priceAns.setText(dataSnapshot.child(PRICE_CAP).getValue(String.class));
                pickupAns.setText(dataSnapshot.child(PICKUP_NAME).getValue(String.class));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void CreateEvent(StringBuilder foodPar) {
        // assembling the foodParam to put in for categories in the search query
        int priceCap = Integer.parseInt(String.valueOf(priceAns.getText()));
        final int uberCap = priceCap/4; // one way uber cap
        float foodCap = priceCap/(2 * numPeeps);
        // determine the priceRange to query with
        priceRange = PriceRange(foodCap);

        OkHttpClient client = new OkHttpClient();

        try {
            String url = BuildUri(foodPar);
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", YELP_KEY)
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Postman-Token", "25f7d9ed-02e6-46dc-8915-a68121e1a168")
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
                        results = jsonObject.getJSONArray("businesses");
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
                            double endLat = item.getJSONObject(COORDINATES).getLong(LATITUDE);
                            double endLon = item.getJSONObject(COORDINATES).getLong(LONGITUDE);
                            List<PriceEstimate> priceEstimates = service.getPriceEstimates((float) startLat, (float) startLong, (float) endLat, (float) endLon).execute().body().getPrices();
                            highEstimate = -1;
                            for (int i = 0; i < priceEstimates.size(); i++) {
                                if (priceEstimates.get(i).getDisplayName().equals(UBERX)) {
                                    highEstimate = priceEstimates.get(i).getHighEstimate();
                                }
                            }
                            if (highEstimate <= uberCap) {
                                found = true;
                                mDatabase.child(TRIPS).child(TEST_TRIP).child(UBER).child(END_LOC).child(LAT).setValue(endLat);
                                mDatabase.child(TRIPS).child(TEST_TRIP).child(UBER).child(END_LOC).child(LONG).setValue(endLon);
                                mDatabase.child(TRIPS).child(TEST_TRIP).child(EVENT).child(DOWNLOAD_URL).setValue(item.get(IMAGE_URL));
                                mDatabase.child(TRIPS).child(TEST_TRIP).child(EVENT).child(NAME).setValue(item.get(NAME));
                                mDatabase.child(TRIPS).child(TEST_TRIP).child(EVENT).child(RATING).setValue(item.get(RATING));
                                Intent intent = new Intent(getActivity(), StartActivity.class);
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
            return "1";
        else if (foodCap >= priceRange2L && foodCap <= priceRange2H)
            return "2";
        else if(foodCap >= priceRange3L && foodCap <= priceRange3H)
            return "3";
        else
            return "4";
    }

    public String BuildUri(StringBuilder foodPar) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(SEARCH_API_URL);
        builder.addParameter("term", "restaurant");
        builder.addParameter("location", String.valueOf(cityAns.getText()));
        builder.addParameter("categories", foodPar.toString());
        builder.addParameter("limit", "50");
        builder.addParameter("offset", "0");
        builder.addParameter("price", priceRange);
        String url = builder.build().toString();
        return url;
    }

    public static CreateThirdFragment newInstance() {
        CreateThirdFragment frag = new CreateThirdFragment();
        return frag;
    }
}