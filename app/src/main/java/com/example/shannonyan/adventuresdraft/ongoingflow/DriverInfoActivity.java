package com.example.shannonyan.adventuresdraft.ongoingflow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.constants.TextViewStrings;
import com.example.shannonyan.adventuresdraft.createflow.CreateFlowActivity;
import com.example.shannonyan.adventuresdraft.uberhelper.UberClient;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
    public TextView driverRating;
    public TextView rideText;
    public ImageView driverPic;
    public ImageView car;
    public Button btDriverMap;
    public Context context;
    public String returnTrip;
    public ImageView ivCar;
    public DatabaseReference mDatabase;
    FloatingActionMenu fabMenu;
    FloatingActionButton cancel, call, map;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

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
        driverRating = (TextView) findViewById(R.id.driverrating);
        ivCar = (ImageView) findViewById(R.id.ivCar);
        rideText = (TextView) findViewById(R.id.rideText);
        ivCar = (ImageView) findViewById(R.id.ivCar);
        fabMenu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
        call = (FloatingActionButton) findViewById(R.id.fabCall);
        cancel = (FloatingActionButton) findViewById(R.id.fabCancel);
        map = (FloatingActionButton) findViewById(R.id.fabMap);


        //UBER instantiations
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.status).child(Database.status);

        Intent intent = getIntent();
        rideId = intent.getStringExtra(Database.RIDE_ID);
        returnTrip = intent.getStringExtra(Database.RETURN_TRIP);
        context = this;
        service.getRideDetails(rideId).enqueue(new Callback<Ride>() {
            @Override

            public void onResponse(Call<Ride> call, Response<Ride> response) {
                Ride ride = response.body();
                driverName.setText(ride.getDriver().getName());
                carModel.setText(" " + ride.getVehicle().getModel());
                carMake.setText(ride.getVehicle().getMake());
                carLicense.setText(ride.getVehicle().getLicensePlate());
                driverPhoneNumber = ride.getDriver().getPhoneNumber();
                driverRating.setText(String.valueOf(ride.getDriver().getRating())+" stars");
                onCancelButtonClick();
                com.example.shannonyan.adventuresdraft.modules.GlideApp.with(context)
                        .load(ride.getDriver().getPictureUrl()).circleCrop()
                        .into(driverPic);
                tvEta.setText(String.valueOf(ride.getEta()) + " min");
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Log.d("DriverInfoActivity", "getRideDetails failed");
            }
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.getValue(String.class);
                if (status != null) {
                    if (status.equals(Database.ARRIVE)) {
                        tvEta.setText("Your ride is arriving");
                        rideText.setText("");
                    }
                    else if (status.equals(Database.PROGRESS)) {
                        if (returnTrip.equals("true")) {
                            Intent i = new Intent(DriverInfoActivity.this, ReturnHomeActivity.class);
                            i.putExtra(Database.RIDE_ID, rideId);
                            mDatabase.removeEventListener(this);
                            startActivity(i);
                        }
                        else {
                            Intent i = new Intent(DriverInfoActivity.this, com.example.shannonyan.adventuresdraft.ongoingflow.RideInProgressActivity.class);
                            mDatabase.removeEventListener(this);
                            startActivity(i);
                        }
                    }
                    else if (status.equals(Database.DRIVER_CANCEL) || status.equals(Database.RIDER_CANCEL)) {
                        driverCancelDialog();
                        mDatabase.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DATABASE", "Value event listener request cancelled.");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                cancelConfirmationDialog();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), UberMapActivity.class);
                startActivity(intent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.v("onBackPressed", "pressed");
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

    public void onCancelButtonClick(){
    }

    public void cancelConfirmationDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(TextViewStrings.CANCEL_TITLE);
        alert.setMessage(TextViewStrings.CANCEL_MESSAGE);
        alert.setPositiveButton(TextViewStrings.DIALOG_POSITIVE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                service.cancelRide(rideId);
                Intent intent = new Intent(DriverInfoActivity.this, CreateFlowActivity.class);
                startActivity(intent);
            }
        });
        alert.setNegativeButton(TextViewStrings.DIALOG_NEGATIVE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void driverCancelDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(TextViewStrings.DRIVER_CANCEL_TITLE);
        alert.setMessage(TextViewStrings.DRIVER_CANCEL_MESSAGE);
        alert.setPositiveButton(TextViewStrings.DIALOG_POSITIVE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getBaseContext(), FindingDriverActivity.class);
                startActivity(intent);
            }
        });
        alert.setNegativeButton(TextViewStrings.DIALOG_NEGATIVE, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}