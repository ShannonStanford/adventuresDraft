package com.example.shannonyan.adventuresdraft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

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
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.services.RidesService;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtaActivity extends AppCompatActivity {

    public RidesService service;
    public String rideID;
    public String status;
    public TextView tvEta;
    public TextView driverName;
    public TextView carMake;
    public TextView carModel;
    public TextView carLicense;
    public String CLIENT_ID = "0toSWTHkZXJIa-llj9rh900hXrelnQeY";
    public String TOKEN = "c2hx0dzYfc5ptMEPWA0w3ODBWdUsaITDQ_UTWF4M"; //serverToken
    public String testAccessToken = "KA.eyJ2ZXJzaW9uIjoyLCJpZCI6InZ1YkREQit1U2UrdUxrS3l6UzNmTkE9PSIsImV4cGlyZXNfYXQiOjE1MzQ2NDQ3NTQsInBpcGVsaW5lX2tleV9pZCI6Ik1RPT0iLCJwaXBlbGluZV9pZCI6MX0.qXqHB-ZHIJ8XBmXopiwcFMo3_Yw0qzFGdg6fVBWhqxU";
    public SessionConfiguration config = new SessionConfiguration.Builder()
            // mandatory
            .setClientId(CLIENT_ID)
            // required for enhanced button features
            .setServerToken(TOKEN)
            .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST, Scope.HISTORY_LITE, Scope.PLACES, Scope.ALL_TRIPS, Scope.REQUEST_RECEIPT))
            // required for implicit grant authentication
            //.setRedirectUri("<REDIRECT_URI>")
            // optional: set sandbox as operating environment
            .setEnvironment(SessionConfiguration.Environment.SANDBOX)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView driverName = (TextView) findViewById(R.id.tvDriverName);
        final TextView carMake = (TextView) findViewById(R.id.tvCarMake);
        final TextView carModel = (TextView) findViewById(R.id.tvCarModel);
        final TextView carLicense = (TextView) findViewById(R.id.tvCarLicense);

        //UBER initializations
        UberSdk.initialize(config);
        AccessTokenManager accessTokenManager = new AccessTokenManager(this, testAccessToken);
        Long expirationTime = Long.valueOf(2592000);
        List<Scope> scopes = Arrays.asList(Scope.PROFILE, Scope.REQUEST, Scope.HISTORY_LITE, Scope.PLACES, Scope.ALL_TRIPS, Scope.REQUEST_RECEIPT);
        String refreshToken = "obtainedRefreshToken";
        String tokenType = "access_token";
        AccessToken accessToken = new AccessToken(expirationTime, scopes, testAccessToken, refreshToken, tokenType);
        AccessTokenStorage accessTokenStorage = new AccessTokenManager(this);
        accessTokenStorage.setAccessToken(accessToken);
        AccessTokenSession session = new AccessTokenSession(config, accessTokenStorage);
        service = UberRidesApi.with(session).build().createService();

        Intent intent = getIntent();
        rideID = intent.getStringExtra("rideId");

        do{
            //Adds a buffer of 5 seconds between updating status
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    service.getRideDetails(rideID).enqueue(new Callback<Ride>() {
                        @Override
                        public void onResponse(Call<Ride> call, Response<Ride> response) {
                            if (response.isSuccessful()) {
                                Ride ride = response.body();
                                status = ride.getStatus();
                                if(status.equals("driver_canceled") || status.equals("rider_canceled")) {
                                    Intent i = new Intent(getBaseContext(), RiderCancelActivity.class);
                                    startActivity(i);
                                } else if(status.equals("accepted") || status.equals("arriving")){
                                    driverName.setText(ride.getDriver().getName());
                                    carModel.setText(ride.getVehicle().getModel());
                                    carMake.setText(ride.getVehicle().getMake());
                                    carLicense.setText(ride.getVehicle().getLicensePlate());
                                    if(status.equals("arriving")){
                                        tvEta.setText("Arriving");
                                    } else {
                                        tvEta.setText(ride.getEta());
                                    }
                                } else if(status.equals("completed") ){
                                    Intent i = new Intent(getBaseContext(), RideInProgressActivity.class);
                                    startActivity(i);
                                }
                            } else {
                                ApiError error = ErrorParser.parseError(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Ride> call, Throwable t) {

                        }
                    });

                }
            }, 0, 5000);
        } while (true);

    }

}
