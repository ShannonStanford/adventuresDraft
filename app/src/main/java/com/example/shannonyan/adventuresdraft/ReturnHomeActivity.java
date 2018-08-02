package com.example.shannonyan.adventuresdraft;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.services.RidesService;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnHomeActivity extends AppCompatActivity {

    UberClient uberClient;
    RidesService service;
    String rideId;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_home);

        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;

        Intent intent = getIntent();
        rideId = intent.getStringExtra("rideId");

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
                }
                else{
                    // stop the timer and get rid of all the cancelled tasks in the queue before
                    // launching the activity
                    timer.cancel();
                    timer.purge();
                    Intent i = new Intent(ReturnHomeActivity.this, CreateFlowActivity.class);
                    i.putExtra("rideId", rideId);
                    startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
            }
        });
    }

}