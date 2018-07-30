package com.example.shannonyan.adventuresdraft;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.rides.client.error.ApiError;
import com.uber.sdk.rides.client.error.ErrorParser;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.ProductsResponse;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindActivity extends AppCompatActivity {

    //Location vars (pickup and drop off)
    public float startLat;
    public float startLong;
    public float endLat;
    public float endLong;
    public String rideId;
    public Boolean check = true;
    //UBER vars
    public RidesService service;
    public UberClient uberClient;
    public String returnTrip;
    public Timer timer;
    private DatabaseReference mDatabase;
    private static final String TRIPS = "trips";
    private static final String TEST_TRIPS = "testTrip";
    private static final String UBER = "uber";
    private static final String RIDE_ID = "rideId";
    private static final String ACCEPT = "accepted";
    private static final String ARRIVE = "arriving";
    private static final String PROGRESS = "in_progress";
    private static final String START_LOC = "startLoc";
    private static final String END_LOC = "endLoc";
    private static final String LAT = "lat";
    private static final String LONG = "long";
    private static final int UBER_X = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //UBER instanstiation
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;
        mDatabase = FirebaseDatabase.getInstance().getReference().child(TRIPS).child(TEST_TRIPS).child(UBER);
        // check if they are starting their journey, or going back?

        if (getIntent().getExtras() != null) {
            Intent intent = getIntent();
            if (intent.getStringExtra("returnTrip").equals("true")) {
                setGoingBack();
                returnTrip = "true";
            }
        }
        else {
            //populate location variables
            setStartEnd();
            returnTrip = "false";
        }

        //start required API calls for UBER process
        findDriver();
    }

    public void findDriver(){
        //get list of products offered in corresponding city
        service.getProducts(startLat, startLong).enqueue(new Callback<ProductsResponse>() {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
                if (response.isSuccessful()) {
                    //extract productId for product of choice, original uber should be #2 according to test response
                    List<Product> products = response.body().getProducts();
                    String productId = products.get(UBER_X).getProductId();
                    getFareId(productId);
                } else {
                    //Api Failure
                    ApiError error = ErrorParser.parseError(response);
                }
            }
            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t) {
                //Network Failure
            }
        });
    }

    //USe product ID to get Fare ID which gurantees a fare for 2 minutes
    public void getFareId(final String productId) {
        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setPickupCoordinates(startLat, startLong)
                .setProductId(productId)
                .setDropoffCoordinates(endLat, endLong)
                .build();

        service.estimateRide(rideRequestParameters).enqueue(new Callback<RideEstimate>() {
            @Override
            public void onResponse(Call<RideEstimate> call, Response<RideEstimate> response) {
                if (response.isSuccessful()) {
                    //not sure why response is returning an estimate with a null fareID however sandbox environment allows us to pass a null one and still make a request, however that would not be allowed in production environment
                    RideEstimate rideEstimate = response.body();
                    String fareId = rideEstimate.getPrice().getFareId();
                    requestRide(productId, fareId);
                } else {
                    //Api Failure
                    ApiError error = ErrorParser.parseError(response);
                }
            }
            @Override
            public void onFailure(Call<RideEstimate> call, Throwable t) {

            }
        });
    }

    //Use product and fare ID to send a request and get a Ride ID
    public void requestRide(final String productId, final String fareId){
        service.getCurrentRide().enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                if(response.isSuccessful()){
                    Ride ride = response.body();
                    rideId = ride.getRideId();
                    mDatabase.child(RIDE_ID).setValue(rideId);
                    Log.v("FindActivity",  RIDE_ID + ": " + rideId);
                    asynchronousTaskDemo(rideId);

                }
                else{
                    RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setPickupCoordinates(startLat, startLong)
                            .setProductId(productId)
                            .setFareId(fareId)
                            .setDropoffCoordinates(endLat, endLong)
                            .build();
                    service.requestRide(rideRequestParameters).enqueue(new Callback<Ride>() {
                        @Override
                        public void onResponse(Call<Ride> call, Response<Ride> response) {
                            if (response.isSuccessful()) {
                                Ride ride = response.body();
                                rideId = ride.getRideId();
                                mDatabase.child(RIDE_ID).setValue(rideId);
                                asynchronousTaskDemo(rideId);
                            } else {
                                //Api Failure
                                ApiError error = ErrorParser.parseError(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Ride> call, Throwable t) {
                            Log.d("tag3", "Callback fails");
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<Ride> call, Throwable t) {

            }
        });
        Log.d("SMART", "ride details callback ended: " + rideId);
    }

    //Use Ride ID to change driver status in SANDBOX every X amount of time for DEMO purposes
    public void asynchronousTaskDemo(final String rideId){
        // timer thing implement:
        timer = new Timer();
        // creating timer task, timer
        TimerTask tasknew = new TimerTask() {
            @Override
            public void run() {
                // execute the background task
                new ApiOperation().execute("");
            }
        };
        // add a buffer of 5 seconds
        timer.schedule(tasknew, 0, 5000);
    }

    // private class for timer
    private class ApiOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String stat = "norun";
            try {
                stat = service.getRideDetails(rideId).execute().body().getStatus();
                Log.d("STAT", "ride status: "+ stat);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stat;
        }

        // run on the main UI thread after the background task is executed
        @Override
        protected void onPostExecute(String stat) {
            Log.d("SMART", "ride id is: " + rideId);
            if (stat.equals(ACCEPT) || stat.equals(ARRIVE) || stat.equals(PROGRESS)) {
                // cancel all scheduled timer tasks and get rid of the cancelled tasks queued to the end
                Log.d("TAG4", "status in progress or accepted");
                timer.cancel();
                timer.purge();

                Log.d("TIMER", "timer cancel successful");
                Intent intent = new Intent(FindActivity.this, EtaActivity.class);
                intent.putExtra(RIDE_ID, rideId);
                intent.putExtra("returnTrip", returnTrip);
                startActivity(intent);
            } else {}
        }
    }

    public void setStartEnd() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                startLat = (float) dataSnapshot.child(START_LOC).child(LAT).getValue(float.class);
                startLong = (float) dataSnapshot.child(START_LOC).child(LONG).getValue(float.class);
                endLat = (float) dataSnapshot.child(END_LOC).child(LAT).getValue(float.class);
                endLong = (float) dataSnapshot.child(END_LOC).child(LONG).getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FindActivity", "Firebase cancelled");
            }
        });
    }

    public void setGoingBack() {
        float tempLat;
        float tempLong;
        tempLat = startLat;
        startLat = endLat;
        endLat = tempLat;

        tempLong = startLong;
        startLong = endLong;
        endLong = tempLong;
    }
}