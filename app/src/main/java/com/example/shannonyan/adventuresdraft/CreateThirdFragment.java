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

import static com.example.shannonyan.adventuresdraft.Constants.LONG;

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
                Intent intent = new Intent(getActivity(), StartActivity.class);
                startActivity(intent);
                //TODO: get priceCap and users food preferences
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // test size
                        food = (ArrayList<String>) dataSnapshot.child(Constants.USER).child(Constants.TEST_USER).child(Constants.FOOD_PREF).getValue();
                        numPeeps = dataSnapshot.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.UBER).child(Constants.NUM_PEEPS).getValue(float.class);
                        startLat = dataSnapshot.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.UBER).child(Constants.START_LOC).child(Constants.LAT).getValue(long.class);
                        startLong = dataSnapshot.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.UBER).child(Constants.START_LOC).child(LONG).getValue(long.class);
                        StringBuilder foodParam = new StringBuilder();
//                        for (int i = 0; i < food.size(); i++) {
//                            foodParam.append(food.get(i));
//                            if (i != food.size() - 1) {
//                                foodParam.append(",");
//                            }
//                        }
//                        CreateEvent(foodParam);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        mDatabase.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child(Constants.CITY_OF_INTEREST).getValue(String.class));
                priceAns.setText("$" + dataSnapshot.child(Constants.PRICECAP).getValue(String.class));
                pickupAns.setText(dataSnapshot.child(Constants.PICKUP).getValue(String.class));
                numPeepAns.setText(String.valueOf(dataSnapshot.child(Constants.NUM_PEEPS).getValue(Integer.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        setValues();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setValues();
    }

    public void setValues(){
        mDatabase.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child(Constants.CITY_OF_INTEREST).getValue(String.class));
                priceAns.setText(dataSnapshot.child(Constants.PRICECAP).getValue(String.class));
                pickupAns.setText(dataSnapshot.child(Constants.PICKUP).getValue(String.class));
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
                    .addHeader(Constants.AUTHORIZATION, YELP_KEY)
                    .addHeader(Constants.CACHE_CONTROL, Constants.NO_CACHE)
                    .addHeader(Constants.POSTMAN, Constants.POSTMAN_TOKEN)
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
                            double endLat = item.getJSONObject(Constants.COORDINATES).getLong(Constants.LATITUDE);
                            double endLon = item.getJSONObject(Constants.COORDINATES).getLong(Constants.LONGITUDE);
                            List<PriceEstimate> priceEstimates = service.getPriceEstimates((float) startLat, (float) startLong, (float) endLat, (float) endLon).execute().body().getPrices();
                            highEstimate = -1;
                            for (int i = 0; i < priceEstimates.size(); i++) {
                                if (priceEstimates.get(i).getDisplayName().equals(Constants.UBERX)) {
                                    highEstimate = priceEstimates.get(i).getHighEstimate();
                                }
                            }
                            if (highEstimate <= uberCap) {
                                found = true;
                                mDatabase.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.UBER).child(Constants.END_LOC).child(Constants.LAT).setValue(endLat);
                                mDatabase.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.UBER).child(Constants.END_LOC).child(Constants.LONG).setValue(endLon);
                                mDatabase.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.EVENT).child(Constants.DOWNLOAD_URL).setValue(item.get(Constants.IMAGE_URL));
                                mDatabase.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.EVENT).child(Constants.NAME).setValue(item.get(Constants.NAME));
                                mDatabase.child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.EVENT).child(Constants.RATING).setValue(item.get(Constants.RATING));
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

    // TODO add a random 

    public String BuildUri(StringBuilder foodPar) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(SEARCH_API_URL);
        builder.addParameter(Constants.TERM, Constants.RESTAURANT);
        builder.addParameter(Constants.LOCATION, String.valueOf(cityAns.getText()));
        builder.addParameter(Constants.CATEGORIES, foodPar.toString());
        builder.addParameter(Constants.LIMIT, "50");
        builder.addParameter(Constants.OFFSET, "0");
        builder.addParameter(Constants.PRICE, priceRange);
        String url = builder.build().toString();
        return url;
    }

    public static CreateThirdFragment newInstance() {
        CreateThirdFragment frag = new CreateThirdFragment();
        return frag;
    }
}