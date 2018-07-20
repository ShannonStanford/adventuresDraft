package com.example.shannonyan.adventuresdraft;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.LoginManager;
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.UberRidesApi;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindActivity extends AppCompatActivity {

    //Location vars (pickup and drop off)
    public float startLat;
    public float startLong;
    public float endLat;
    public float endLong;
    //UBER API vars
    public String CLIENT_ID = "0toSWTHkZXJIa-llj9rh900hXrelnQeY";
    public String TOKEN = "c2hx0dzYfc5ptMEPWA0w3ODBWdUsaITDQ_UTWF4M"; //serverToken
    public String testAccessToken = "KA.eyJ2ZXJzaW9uIjoyLCJpZCI6InZ1YkREQit1U2UrdUxrS3l6UzNmTkE9PSIsImV4cGlyZXNfYXQiOjE1MzQ2NDQ3NTQsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.qXqHB-ZHIJ8XBmXopiwcFMo3_Yw0qzFGdg6fVBWhqxU";
    public Session session;
    public RidesService service;
    public LoginManager loginManager;
    public SessionConfiguration config = new SessionConfiguration.Builder()
            // mandatory
            .setClientId(CLIENT_ID)
            // required for enhanced button features
            .setServerToken(TOKEN)
            // required for implicit grant authentication
            //.setRedirectUri("<REDIRECT_URI>")
            // optional: set sandbox as operating environment
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //UBER initializations
        UberSdk.initialize(config);
        session = loginManager.getSession();
        service = UberRidesApi.with(session).build().createService();
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
                    //extract productId for product of choice, in this case just the first type returned
                    List<Product> products = response.body().getProducts();
                    String productId = products.get(0).getProductId();
                    //added try/catch for Exception from getFareId(productId)
                    try {
                        getFareId(productId);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
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
    //added throws IOException to fix .execute() error, good choice? not sure what that may change.
    public void getFareId(String productId) throws IOException {
        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setPickupCoordinates(startLat, startLong)
                .setProductId(productId)
                .setDropoffCoordinates(endLat, endLong)
                .build();
        RideEstimate rideEstimate = service.estimateRide(rideRequestParameters).execute().body();
        //String fareId = rideEstimate.getFareId();
        //requestRide(productId, fareId);
    }
    //or could solve execute() error by surrounding with try/catch block
    public void requestRide(String productId, String fareId){
        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setPickupCoordinates(startLat, startLong)
                .setProductId(productId)
                .setFareId(fareId)
                .setDropoffCoordinates(endLat, endLong)
                .build();
        Ride ride = null;
        try {
            ride = service.requestRide(rideRequestParameters).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String rideId = ride.getRideId();
        callAsynchronousTask(rideId);
    }

    public void callAsynchronousTask(String rideId){

    }

    public void setStartEnd(){
        //TODO: Set variables based on Database values
        startLat = (float) 37.4564126;
        startLong = (float) -122.18630009999998;
        endLat = (float) 37.4799006;
        endLong = (float) -122.15206649999999;
    }


}
