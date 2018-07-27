package com.example.shannonyan.adventuresdraft;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    public String status = "1";
    public String transportTo;
    public Timer timer;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //UBER instanstiation
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // check if they are starting their journey, or going back?
//        Intent intent = getIntent();
//        if (intent != null && intent.getStringExtra("transportTo").equals("false")) {
//            setGoingBack();
//            transportTo = "false";
//        }
//        else {
//            //populate location variables
//            setStartEnd();
//            transportTo = "true";
//        }
        setStartEnd();
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
                    String productId = products.get(2).getProductId();
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
                    mDatabase.child("trips").child("testTrip").child("uber").child("rideId").setValue(rideId);
                    Log.v("tag3", "rideid: " + rideId);
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
                                mDatabase.child("trips").child("testTrip").child("uber").child("rideId").setValue(rideId);
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
        timer.schedule(tasknew, 0, 10000);
    }

    // private class for timer
    private class ApiOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String stat = "norun";
            try {
                stat = service.getRideDetails(rideId).execute().body().getStatus();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            service.getRideDetails(rideId).enqueue(new Callback<Ride>() {
//                @Override
//                public void onResponse(Call<Ride> call, Response<Ride> response) {
//                    Log.d("tag1", "Going inside the callback");
//                    if (response.isSuccessful()) {
//                        Log.d("tag1", "Getting a successful response");
//                        Ride ride = response.body();
//                        status = ride.getStatus();
//                        Log.d("tag2", "updating the status: " + status);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Ride> call, Throwable t) {
//                    Log.d("tag", t.getMessage());
//                }
//            });
            return stat;
        }
        // run on the main UI thread after the background task is executed
        @Override
        protected void onPostExecute(String stat) {
            if (stat.equals("accepted") || stat.equals("arriving") || stat.equals("in_progress")) {
                // cancel all scheduled timer tasks and get rid of the cancelled tasks queued to the end
                Log.d("TAG4", "status in progress or accepted");
                timer.cancel();
                timer.purge();

                Log.d("TIMER", "timer cancel successful");
//                timer.purge();
                Intent intent = new Intent(FindActivity.this, EtaActivity.class);
                intent.putExtra("rideId", rideId);
//                if (transportTo.equals("false")) {
//                    intent.putExtra("transportTo", transportTo);
//                }
                startActivity(intent);
            } else {}
        }

    }

    public void setStartEnd() {
        //TODO: Set variables based on Database values
        startLat = (float) 37.4564126;
        startLong = (float) -122.18630009999998;
        endLat = (float) 37.4799006;
        endLong = (float) -122.15206649999999;
    }

    public void setGoingBack() {
        //TODO: Set variables based on Database values
        endLat = (float) 37.4564126;
        endLong = (float) -122.18630009999998;
        startLat = (float) 37.4799006;
        startLong = (float) -122.15206649999999;
    }
}