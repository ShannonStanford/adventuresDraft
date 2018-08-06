package com.example.shannonyan.adventuresdraft.Ongoing_Flow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.shannonyan.adventuresdraft.Constants;
import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.UberClient;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.services.RidesService;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RideInProgressActivity extends AppCompatActivity {

    public RidesService service;
    public UberClient uberClient;
    public String rideID;
    public Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_in_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;

        Intent intent = getIntent();
        rideID = intent.getStringExtra(Constants.RIDE_ID);

        // setup and call the timer
        timer = new Timer();
        // creating timer task, timer
        TimerTask tasknew = new TimerTask() {
            @Override
            public void run() {
                // execute the background task
                checkProgress();
            }
        };
        // add a buffer of 5 seconds
        timer.schedule(tasknew, 0, 5000);

    }

    public void checkProgress() {

        service.getCurrentRide().enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                    if(response.isSuccessful()){
                        Log.d("RideInProgressActivity", "check progress was successful");
                    }
                    else{
                        // stop the timer and get rid of all the cancelled tasks in the queue before
                        // launching the activity
                        timer.cancel();
                        timer.purge();
                        Intent i = new Intent(RideInProgressActivity.this, EventActivity.class);
                        i.putExtra(Constants.RIDE_ID, rideID);
                        startActivity(i);
                    }
                }
            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Log.d("RideInProgressActivity", "check progress failed");
            }
        });
    }

}