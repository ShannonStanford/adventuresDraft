package com.example.shannonyan.adventuresdraft;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.uber.sdk.rides.client.error.ApiError;
import com.uber.sdk.rides.client.error.ErrorParser;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideInProgressActivity extends AppCompatActivity {

    public RidesService service;
    public UberClient uberClient;
    public String rideID;
    public String status;
    public Timer timer;

    // to implement the timer
    // private class for timer
    private class ApiOperation extends AsyncTask<String, Void, Ride> {
        Ride ride;
        public Handler hand;

        @Override
        protected Ride doInBackground(String... strings) {
            try {
                ride = service.getRideDetails(rideID).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ride;
        }
        // run on the main UI thread after the background task is executed
        @Override
        protected void onPostExecute(Ride ride1) {
            // populate the views based on the current status and situation
            // deals with accepted and arriving
            String stat = ride1.getStatus();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_in_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;

        Intent intent = getIntent();
        rideID = intent.getStringExtra("rideId");
        checkProgress();

        // setup and call the timer
        timer = new Timer();
        // creating timer task, timer
        TimerTask tasknew = new TimerTask() {
            @Override
            public void run() {
                // execute the background task
                new RideInProgressActivity.ApiOperation().execute("");

            }
        };
        // add a buffer of 5 seconds
        timer.schedule(tasknew, 0, 5000);

    }

    public void checkProgress() {
        service.getRideDetails(rideID).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                if (response.isSuccessful()) {
                    Ride ride = response.body();
                    status = ride.getStatus();
                } else {
                    ApiError error = ErrorParser.parseError(response);
                }
            }
            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Intent i = new Intent(getBaseContext(), EventActivity.class);
                startActivity(i);
            }
        });

    }

}
