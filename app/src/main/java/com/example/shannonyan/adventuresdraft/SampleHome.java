package com.example.shannonyan.adventuresdraft;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.android.core.auth.LoginManager;
import com.uber.sdk.android.core.auth.AccessTokenManager;
import com.uber.sdk.core.auth.AccessToken;
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

public class SampleHome extends AppCompatActivity {

    public String CLIENT_ID = "0toSWTHkZXJIa-llj9rh900hXrelnQeY";
    public String TOKEN = "c2hx0dzYfc5ptMEPWA0w3ODBWdUsaITDQ_UTWF4M";
    //pass this in as a query parameter when making api calls tag is access_token
    public String testingAccessToken = "KA.eyJ2ZXJzaW9uIjoyLCJpZCI6InZ1YkREQit1U2UrdUxrS3l6UzNmTkE9PSIsImV4cGlyZXNfYXQiOjE1MzQ2NDQ3NTQsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.qXqHB-ZHIJ8XBmXopiwcFMo3_Yw0qzFGdg6fVBWhqxU";
    //need a session to use Uber API
    public Session session;
    RidesService service;
    public LoginManager loginManager;

    SessionConfiguration config = new SessionConfiguration.Builder()
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
        setContentView(R.layout.activity_sample_home);
        //initialize SDK
        UberSdk.initialize(config);
        session = loginManager.getSession();
        service = UberRidesApi.with(session).build().createService();
    }

    public void getProducts(float lat, float lon){
        service.getProducts(lat, lon).enqueue(new Callback<ProductsResponse>() {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
                if (response.isSuccessful()) {
                    //Success
                    List<Product> products = response.body().getProducts();
                    String productId = products.get(0).getProductId();
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

    public void getFareInfo(String productId) throws IOException {
        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setPickupCoordinates(37.77f, -122.41f)
                .setProductId(productId)
                .setDropoffCoordinates(37.49f, -122.41f)
                .build();
        RideEstimate rideEstimate = service.estimateRide(rideRequestParameters).execute().body();
        String fareId = rideEstimate.getFareId();
    }

    public void requestRide(String productId, String fareId) throws IOException {
        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setPickupCoordinates(37.77f, -122.41f)
                .setProductId(productId)
                .setFareId(fareId)
                .setDropoffCoordinates(37.49f, -122.41f)
                .build();
        Ride ride = service.requestRide(rideRequestParameters).execute().body();
        String rideId = ride.getRideId();
    }

    public void getRideDetails(String rideId) throws IOException {
        Response<Void> response = service.cancelRide(rideId).execute();
    }

    public void cancelRide(String rideId) throws IOException {
        Response<Void> response = service.cancelRide(rideId).execute();
    }


}
//NOTES
//Sync API call
//        Response<List<Product>> response = service.getProducts(37.79f, -122.39f).execute();
//        List<Product> products = response.body();
//        String productId = products.get(0).getProductId();
//Async API call
//        service.getProducts(lat,lon).enqueue(new Callback<ProductsResponse>() {
//            @Override
//            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
//                if (response.isSuccessful()) {
//                    //Success
//
//                } else {
//                    //Api Failure
//                    ApiError error = ErrorParser.parseError(response);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ProductsResponse> call, Throwable t) {
//                //Network Failure
//            }
//        });
