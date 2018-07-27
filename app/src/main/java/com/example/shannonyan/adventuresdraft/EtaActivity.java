package com.example.shannonyan.adventuresdraft;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideMap;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtaActivity extends AppCompatActivity {

    public RidesService service;
    public UberClient uberClient;
    public String rideID;
    public TextView tvEta;
    public String mapURL;
    public String driverPhoneNumber;
    public TextView driverName;
    public TextView carMake;
    public TextView carModel;
    public TextView carLicense;
    public ImageView driverPic;
    public Button btDriverMap;
    public Button btCallDriver;
    public Button btCancel;
    public Context context;
    public Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        driverName = (TextView) findViewById(R.id.tvDriverName);
        carMake = (TextView) findViewById(R.id.tvCarMake);
        carModel = (TextView) findViewById(R.id.tvCarModel);
        carLicense = (TextView) findViewById(R.id.tvCarLicense);
        tvEta = (TextView) findViewById(R.id.tvEta);
        driverPic = (ImageView) findViewById(R.id.ivDriverPic);
        btDriverMap = (Button) findViewById(R.id.btDriverMap);
        btCallDriver = (Button) findViewById(R.id.btCallDriver);
        btCancel = (Button) findViewById(R.id.btCancel);

        //UBER instantiations
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;

        Intent intent = getIntent();
        rideID = intent.getStringExtra("rideId");
        context = this;

        // setup and call the timer
        timer = new Timer();
        // creating timer task, timer
        TimerTask tasknew = new TimerTask() {
            @Override
            public void run() {
                // execute the background task
                new EtaActivity.ApiOperation().execute("");

            }
        };
        // add a buffer of 5 seconds
        timer.schedule(tasknew, 0, 5000);
    }

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
            if (stat.equals("accepted") || stat.equals("arriving")) {
                Log.d("TAG4", "status accepted");
                driverName.setText(ride1.getDriver().getName());
                carModel.setText(ride1.getVehicle().getModel());
                carMake.setText(ride1.getVehicle().getMake());
                carLicense.setText(ride1.getVehicle().getLicensePlate());
                driverPhoneNumber = ride1.getDriver().getPhoneNumber();

                onMapButtonClick();
                onCallButtonClick();
                onCancelButtonClick();

                GlideApp.with(context)
                        .load(ride1.getDriver().getPictureUrl())
                        .into(driverPic);

                if (stat.equals("arriving")) {
                    tvEta.setText("Arriving");
                    Log.d("TAG4", "status arriving");
                } else {
                    tvEta.setText(String.valueOf(ride1.getEta()));
                }

            }
            // deals with driver canceled and rider canceled situations
            else if (stat.equals("driver_canceled") || stat.equals("rider_canceled")) {
                timer.cancel();
                timer.purge();
                Intent i = new Intent(getBaseContext(), RiderCancelActivity.class);
                startActivity(i);
            } else if (stat.equals("in_progress")) {
                Log.d("TAG4", "status in progress");
                timer.cancel();
                timer.purge();
                Intent i = new Intent(EtaActivity.this, RideInProgressActivity.class);
                i.putExtra("rideId", rideID);
                startActivity(i);
            }
        }
    }

    public void onMapButtonClick() {
        btDriverMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.getRideMap(rideID).enqueue(new Callback<RideMap>() {
                    @Override
                    public void onResponse(Call<RideMap> call, Response<RideMap> response) {
                        if (response.isSuccessful()) {
                            RideMap ride = response.body();
                            mapURL = ride.getHref();
                        }
                    }

                    @Override
                    public void onFailure(Call<RideMap> call, Throwable t) {

                    }
                });
                Intent intent = new Intent(getBaseContext(), MapActivity.class);
                intent.putExtra("mapURL", mapURL);
                startActivity(intent);
            }
        });
    }

    public void onCallButtonClick() {
        btCallDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+driverPhoneNumber));
                if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getBaseContext(), "Make sure you granted calling permissions", Toast.LENGTH_SHORT);
                    return;
                }
                startActivity(callIntent);
            }
        });
    }

    public void onCancelButtonClick(){
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), RiderCancelActivity.class);
                startActivity(intent);
            }
        });
    }
}
