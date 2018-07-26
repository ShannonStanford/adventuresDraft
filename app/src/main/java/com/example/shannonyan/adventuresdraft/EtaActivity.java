package com.example.shannonyan.adventuresdraft;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uber.sdk.rides.client.error.ApiError;
import com.uber.sdk.rides.client.error.ErrorParser;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideMap;
import com.uber.sdk.rides.client.services.RidesService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EtaActivity extends AppCompatActivity {

    public RidesService service;
    public UberClient uberClient;
    public String rideID;
    public String status;
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
        final TextView tvEta = (TextView) findViewById(R.id.tvEta);
        final ImageView driverPic = (ImageView) findViewById(R.id.ivDriverPic);
        final Button btDriverMap = (Button) findViewById(R.id.btDriverMap);
        final Button btCallDriver = (Button) findViewById(R.id.btCallDriver);
        final Button btRiderCandel = (Button) findViewById(R.id.btCancel);

        //UBER instantiations
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;

        Intent intent = getIntent();
        rideID = intent.getStringExtra("rideId");
        context = this;

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

                                    onMapButtonClick();
                                    onCallButtonClick();
                                    onCancelButtonClick();

                                    GlideApp.with(context)
                                            .load(ride.getDriver().getPictureUrl())
                                            .into(driverPic);

                                    if(status.equals("arriving")){
                                        tvEta.setText("Arriving");
                                    } else {
                                        tvEta.setText(String.valueOf(ride.getEta()));
                                    }
                                } else if(status.equals("in_progress") || status.equals("completed") ){
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
