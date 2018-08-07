package com.example.shannonyan.adventuresdraft.Ongoing_Flow;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shannonyan.adventuresdraft.Constants;
import com.example.shannonyan.adventuresdraft.Modules.GlideApp;
import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.Uber_Helper.UberClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideMap;
import com.uber.sdk.rides.client.services.RidesService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtaActivity extends AppCompatActivity {

    private static final String CANCELLED = "Driver Cancelled, calling another Uber.";
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
    public String rideId2;
    public String returnTrip;
    public DatabaseReference mDatabase;

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
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.status).child(Constants.status);

        Intent intent = getIntent();
        rideId = intent.getStringExtra(Constants.RIDE_ID);
        returnTrip = intent.getStringExtra(Constants.RETURN_TRIP);
        context = this;
        service.getRideDetails(rideId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                Ride ride = response.body();
                driverName.setText(ride.getDriver().getName());
                carModel.setText(ride.getVehicle().getModel());
                carMake.setText(ride.getVehicle().getMake());
                carLicense.setText(ride.getVehicle().getLicensePlate());
                driverPhoneNumber = ride.getDriver().getPhoneNumber();
                onMapButtonClick();
                onCallButtonClick();
                onCancelButtonClick();
                GlideApp.with(context)
                        .load(ride.getDriver().getPictureUrl())
                        .into(driverPic);
                tvEta.setText(String.valueOf(ride.getEta()));
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {

            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if (status != null) {
                    if (status.equals(Constants.ARRIVE)) {
                        tvEta.setText("Arriving");
                    }
                    else if (status.equals(Constants.PROGRESS)) {
                        if (returnTrip.equals("true")) {
                            Intent i = new Intent(EtaActivity.this, ReturnHomeActivity.class);
                            i.putExtra(Constants.RIDE_ID, rideId);
                            mDatabase.child(Constants.status).child(Constants.status).removeEventListener(this);
                            startActivity(i);
                        }
                        else {
                            Intent i = new Intent(EtaActivity.this, com.example.shannonyan.adventuresdraft.Ongoing_Flow.RideInProgressActivity.class);
                            mDatabase.child(Constants.status).child(Constants.status).removeEventListener(this);
                            startActivity(i);
                        }
                    }
                    else if (status.equals(Constants.DRIVER_CANCEL) || status.equals(Constants.RIDER_CANCEL)) {
                        Toast.makeText(EtaActivity.this, CANCELLED, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getBaseContext(), FindActivity.class);
                        mDatabase.child(Constants.status).child(Constants.status).removeEventListener(this);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DATABASE", "Value event listener request cancelled.");
            }
        });
    }

    public void onMapButtonClick() {
        btDriverMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MapActivity.class);
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
                Intent intent = new Intent(getBaseContext(), MapActivity.class);
                intent.putExtra(Constants.MAP_URL, mapURL);
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
                Intent intent = new Intent(getBaseContext(), RiderCancelActivity.class);
                intent.putExtra(Constants.RIDE_ID, rideId2);
                intent.putExtra(Constants.RETURN_TRIP, returnTrip);
                startActivity(intent);
            }
        });
    }
}
