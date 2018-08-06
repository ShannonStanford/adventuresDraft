package com.example.shannonyan.adventuresdraft;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;

public class RiderCancelActivity extends AppCompatActivity {
    Button orderNewDriver;
    Button cancelTrip;
    UberClient uberClient;
    RidesService service;
    String rideId;
    String returnTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_cancel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        orderNewDriver = (Button) findViewById(R.id.orderNewDriver);
        cancelTrip = (Button) findViewById(R.id.cancelTrip);

        rideId = getIntent().getStringExtra(Constants.RIDE_ID);
        returnTrip = getIntent().getStringExtra(Constants.RETURN_TRIP);

        //UBER instantiations
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;

        // what is the logic behind this? IN what scenario will they ditch their current ride and just
        // suddenly call a new driver? they will have to cancel their ride first].
        orderNewDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cancel current ride first
                // has to be done in background thread - otherwise - networkonmainthread exception thrown
                new RiderCancelActivity.ApiOperation().execute(rideId);
                // launch the findActivity and it'll call an uber in its onCreate
                Intent intent = new Intent(RiderCancelActivity.this, FindActivity.class);
                intent.putExtra(Constants.RETURN_TRIP, returnTrip);
                startActivity(intent);

                boolean check = false;
                if (check) {
                    Log.d("TAG", "goes inside the if statement");
                }
                else {
                    Log.d("TAG", "goes inside the else statement");
                }
            }
        });

        cancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make the cancel ride API call -- need the rideId for that
                new RiderCancelActivity.ApiOperation().execute(rideId);
                // launch the createactivity
                Intent intent = new Intent(RiderCancelActivity.this, CreateFlowActivity.class);
                startActivity(intent);
            }
        });
    }

    private class ApiOperation extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                service.cancelRide(strings[0]).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rideId;
        }
    }
}
