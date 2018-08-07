package com.example.shannonyan.adventuresdraft.ongoingflow;

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

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.UberClient;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.modules.GlideApp;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideMap;
import com.uber.sdk.rides.client.services.RidesService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverInfoActivity extends AppCompatActivity {

    public RidesService service;
    public UberClient uberClient;
    public String rideId;
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
    public String rideId2;
    public String returnTrip;

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
        rideId = intent.getStringExtra(Database.RIDE_ID);
        returnTrip = intent.getStringExtra(Database.RETURN_TRIP);
        context = this;

        // setup and call the timer
        timer = new Timer();
        // creating timer task, timer
        TimerTask tasknew = new TimerTask() {
            @Override
            public void run() {
                // execute the background task
                new DriverInfoActivity.ApiOperation().execute("");

            }
        };
        // add a buffer of 5 seconds
        timer.schedule(tasknew, 0, 5000);
    }

    @Override
    public void onBackPressed() {
        Log.v("onBackPressed", "pressed");
    }

    // to implement the timer
    // private class for timer
    private class ApiOperation extends AsyncTask<String, Void, Ride> {
        Ride ride;
        public Handler hand;

        @Override
        protected Ride doInBackground(String... strings) {
            try {
                ride = service.getRideDetails(rideId).execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ride;
        }

        // run on the main UI thread after the background task is executed
        @Override
        protected void onPostExecute(Ride ride1) {
            final String CANCELLED = "Driver Cancelled, calling another Uber.";
            // populate the views based on the current status and situation
            // deals with accepted and arriving
            String stat = ride1.getStatus();
            rideId2 = ride1.getRideId();
            if (stat.equals(Database.ACCEPT) || stat.equals(Database.ARRIVE)) {
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

                if (stat.equals(Database.ARRIVE)) {
                    tvEta.setText(Database.ARRIVE);
                    Log.d("TAG4", "status arriving");
                } else {
                    tvEta.setText(String.valueOf(ride1.getEta()));
                }
            }
            // deals with driver canceled and rider canceled situations
            else if (stat.equals(Database.DRIVER_CANCEL) || stat.equals(Database.RIDER_CANCEL)) {
                // TODO resolve the logic behind this
                // TODO
                timer.cancel();
                timer.purge();
                Toast.makeText(DriverInfoActivity.this, CANCELLED, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getBaseContext(), FindingDriverActivity.class);
                startActivity(intent);
            } else if (stat.equals(Database.PROGRESS)) {
                Log.d("TAG4", "status in progress");
                timer.cancel();
                timer.purge();
                if (returnTrip.equals("true")) {
                    Intent i = new Intent(DriverInfoActivity.this,
                            ReturnHomeActivity.class);
                    i.putExtra(Database.RIDE_ID, rideId);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(DriverInfoActivity.this, com.example.shannonyan.adventuresdraft.ongoingflow.RideInProgressActivity.class);
                    startActivity(i);
                }
            }
        }
    }

    public void onMapButtonClick() {

        btDriverMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UberMapActivity.class);
                startActivity(intent);
            }
        });
    }

    //Use this method to get the Map Link in a real life ride, (non simulation).
    public void getMapLinkProduction() {
        btDriverMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.getRideMap(rideId).enqueue(new Callback<RideMap>() {
                    @Override
                    public void onResponse(Call<RideMap> call, Response<RideMap> response) {
                        if (response.isSuccessful()) {
                            RideMap ride = response.body();
                            mapURL = ride.getHref();
                        }
                    }

                    @Override
                    public void onFailure(Call<RideMap> call, Throwable t) {
                        Log.d("ETA Activity", "OnMapButtonClick failed");
                    }
                });
                // launch the map activity to show their progress
                Intent intent = new Intent(getBaseContext(), UberMapActivity.class);
                intent.putExtra(Database.MAP_URL, mapURL);
                startActivity(intent);
            }
        });
    }

    public void onCallButtonClick() {
        final String callingPermission = "Make sure you granted calling permissions";
        btCallDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+driverPhoneNumber));
                if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getBaseContext(), callingPermission, Toast.LENGTH_SHORT);
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
                timer.cancel();
                timer.purge();
                Intent intent = new Intent(getBaseContext(), RiderCancelActivity.class);
                intent.putExtra(Database.RIDE_ID, rideId2);
                intent.putExtra(Database.RETURN_TRIP, returnTrip);
                startActivity(intent);
            }
        });
    }
}
