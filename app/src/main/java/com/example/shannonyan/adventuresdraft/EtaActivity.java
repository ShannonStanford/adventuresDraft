package com.example.shannonyan.adventuresdraft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.uber.sdk.rides.client.error.ApiError;
import com.uber.sdk.rides.client.error.ErrorParser;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.services.RidesService;

import org.parceler.Parcels;

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

        Intent intent = getIntent();
        service = (RidesService) Parcels.unwrap(intent.getParcelableExtra("service"));
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
