package com.example.shannonyan.adventuresdraft.ongoingflow;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shannonyan.adventuresdraft.constants.Api;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.createflow.CreateFlowActivity;
import com.bumptech.glide.Glide;
import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.UberClient;
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
    ImageView ivEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_home);

        ivEnd = findViewById(R.id.ivEnd);
        Glide.with(getBaseContext())
                .load(R.drawable.rocket_ending)
                .into(ivEnd);

        TextView prepare = (TextView) findViewById(R.id.tvLater);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/round.otf");
        prepare.setTypeface(typeface);

        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;

        Intent intent = getIntent();
        rideId = intent.getStringExtra(Database.RIDE_ID);

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
                    Log.d("ReturnHomeActivity", "check progress was successful");
                }
                else{
                    // stop the timer and get rid of all the cancelled tasks in the queue before
                    // launching the activity
                    timer.cancel();
                    timer.purge();
                    Intent i = new Intent(ReturnHomeActivity.this, CreateFlowActivity.class);
                    i.putExtra(Database.RIDE_ID, rideId);
                    startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Log.d("ReturnHomeActivity", "get current ride failed");
            }
        });
    }

}
