package com.example.shannonyan.adventuresdraft;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.core.auth.AccessTokenStorage;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.AccessTokenSession;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
import com.uber.sdk.rides.client.error.ApiError;
import com.uber.sdk.rides.client.error.ErrorParser;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.ProductsResponse;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.model.RideUpdateParameters;
import com.uber.sdk.rides.client.model.SandboxProductRequestParameters;
import com.uber.sdk.rides.client.model.SandboxRideRequestParameters;
import com.uber.sdk.rides.client.services.RidesService;

import org.parceler.Parcels;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //UBER instanstiation
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;
        //populate location variables
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
                                asynchronousTaskDemo(rideId);

                            } else {
                                //Api Failure
                                ApiError error = ErrorParser.parseError(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Ride> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {

            }
        });
    }

    //Use Ride ID to constantly get a ride object and check for changes in driver status
    public void asynchronousTaskReal(String rideId){
        String[] statuses = {"processing", "accepted", "arriving", "driver_cancelled"};
        Random rand = new Random();
        int  n = rand.nextInt(50);
    }

    //Use Ride ID to change driver status in SANDBOX every X amount of time for DEMO purposes
    public void asynchronousTaskDemo(final String rideId){
        // add a buffer of 5 seconds
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // setting the status to accepted
                final SandboxRideRequestParameters.Builder sandboxRideRequestParameters = new SandboxRideRequestParameters.Builder().setStatus("accepted");
                service.updateSandboxRide(rideId, sandboxRideRequestParameters.build());
            }
        }, 0, 5000);
        timer.cancel(); // clean up the threads

        // launch the next activity when a driver accepts
        do {
            //check = false;
            service.getRideDetails(rideId).enqueue(new Callback<Ride>() {
                @Override
                public void onResponse(Call<Ride> call, Response<Ride> response) {
                    if (response.isSuccessful()) {
                        Ride ride = response.body();
                        String status = ride.getStatus();
                        if(status.equals("accepted") || status.equals("arriving") || status.equals("in_progress")){
                            Intent intent = new Intent(FindActivity.this, EtaActivity.class);
                            intent.putExtra("rideId", rideId);
                            startActivity(intent);
                        }
                        else{
                            //scope issue
                            check = true;
                        }
                    } else {
                        ApiError error = ErrorParser.parseError(response);
                    }
                }

                @Override
                public void onFailure(Call<Ride> call, Throwable t) {

                }
            });
            //check = true;
        }while(true);
    }

    public void setStartEnd() {
        //TODO: Set variables based on Database values
        startLat = (float) 37.4564126;
        startLong = (float) -122.18630009999998;
        endLat = (float) 37.4799006;
        endLong = (float) -122.15206649999999;
    }
}
