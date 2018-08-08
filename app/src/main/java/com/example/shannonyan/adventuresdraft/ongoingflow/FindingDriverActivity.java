package com.example.shannonyan.adventuresdraft.ongoingflow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.uberhelper.UberClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.rides.client.error.ApiError;
import com.uber.sdk.rides.client.error.ErrorParser;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.ProductsResponse;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.services.RidesService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindingDriverActivity extends AppCompatActivity {

    //Location vars (pickup and drop off)
    public float startLat;
    public float startLong;
    public float endLat;
    public float endLong;
    public String rideId;
    //UBER vars
    public RidesService service;
    public UberClient uberClient;
    public String returnTrip;
    public String status;

    private DatabaseReference mDatabase;
    private static final int UBER_X = 2;
    private ImageView ivBackgroundFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        ivBackgroundFind = findViewById(R.id.ivBackgroundFind);
        //UBER instanstiation
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // check if they are starting their journey, or going back?
        if (getIntent().getExtras() != null) {
            Intent intent = getIntent();
            if (intent.getStringExtra("returnTrip").equals("true")) {
                setGoingBack();
                returnTrip = "true";
            }
        }
        else {
            //populate location variables
            setStartEnd();
            returnTrip = "false";
        }
        mDatabase.child(Database.status).child(Database.status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(String.class);
                if (status != null) {
                    if (status.equals(Database.ACCEPT) || status.equals(Database.ARRIVE) || status.equals(Database.PROGRESS)) {
                        Intent intent = new Intent(FindingDriverActivity.this, DriverInfoActivity.class);
                        intent.putExtra(Database.RIDE_ID, rideId);
                        intent.putExtra("returnTrip", returnTrip);
                        mDatabase.child(Database.status).child(Database.status).removeEventListener(this);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DATABASE", "Value event listener request cancelled.");
            }
        });

        Glide.with(getBaseContext())
                .load(R.drawable.spaceship_dark)
                .into(ivBackgroundFind);
    }

    @Override
    public void onBackPressed() {
        Log.v("onBackPressed", "pressed");
    }

    public void findDriver(){
        //get list of products offered in corresponding city
        service.getProducts(startLat, startLong).enqueue(new Callback<ProductsResponse>() {
            @Override
            public void onResponse(Call<ProductsResponse> call, Response<ProductsResponse> response) {
                if (response.isSuccessful()) {
                    //extract productId for product of choice, original uber should be #2 according to test response
                    List<Product> products = response.body().getProducts();
                    String productId = products.get(UBER_X).getProductId();
                    getFareId(productId);
                } else {
                    //Api Failure
                    ApiError error = ErrorParser.parseError(response);
                }
            }
            @Override
            public void onFailure(Call<ProductsResponse> call, Throwable t) {
                //Network Failure
            }
        });
    }

    //Use product ID to get Fare ID which gurantees a fare for 2 minutes
    public void getFareId(final String productId) {
        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setPickupCoordinates(startLat, startLong)
                .setProductId(productId)
                .setDropoffCoordinates(endLat, endLong)
                .build();

        service.estimateRide(rideRequestParameters).enqueue(new Callback<RideEstimate>() {
            @Override
            public void onResponse(Call<RideEstimate> call, Response<RideEstimate> response) {
                if (response.isSuccessful()) {
                    //not sure why response is returning an estimate with a null fareID however sandbox environment allows us to pass a null one and still make a request, however that would not be allowed in production environment
                    RideEstimate rideEstimate = response.body();
                    String fareId = rideEstimate.getPrice().getFareId();
                    requestRide(productId, fareId);
                } else {
                    //Api Failure
                    ApiError error = ErrorParser.parseError(response);
                }
            }

            @Override
            public void onFailure(Call<RideEstimate> call, Throwable t) {
                Log.d("FindingDriverActivity", "estimate ride failed");
            }
        });
    }

    //Use product and fare ID to send a request and get a Ride ID
    public void requestRide(final String productId, final String fareId){
        service.getCurrentRide().enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                if(response.isSuccessful()){
                    useCurrentRide(response);
                }
                else{
                    requestNewRide(response, productId, fareId);
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Log.d("FindingDriverActivity", "request ride failed");
            }
        });
    }

    public void useCurrentRide(Response<Ride> response){
        Ride ride = response.body();
        rideId = ride.getRideId();
        mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.RIDE_ID).setValue(rideId);
        Log.v("tag3", "rideid: " + rideId);
    }

    public void requestNewRide(Response<Ride> response, String productId, String fareId){
        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder().setPickupCoordinates(startLat, startLong)
                .setProductId(productId)
                .setFareId(fareId)
                .setDropoffCoordinates(endLat, endLong)
                .build();

        service.requestRide(rideRequestParameters).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
                if (response.isSuccessful()) {
                    Ride ride = response.body();
                    rideId = ride.getRideId();
                    mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).child(Database.RIDE_ID).setValue(rideId);
                } else {
                    ApiError error = ErrorParser.parseError(response);
                }
            }

            @Override
            public void onFailure(Call<Ride> call, Throwable t) {
                Log.d("tag3", "Callback fails");
            }
        });
        Log.d("SMART", "ride details callback ended: " + rideId);
    }

    public void setStartEnd() {
        mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startLat = (float) dataSnapshot.child(Database.START_LOC).child(Database.LAT).getValue(float.class);
                startLong = (float) dataSnapshot.child(Database.START_LOC).child(Database.LONG).getValue(float.class);
                endLat = (float) dataSnapshot.child(Database.END_LOC).child(Database.LAT).getValue(float.class);
                endLong = (float) dataSnapshot.child(Database.END_LOC).child(Database.LONG).getValue(float.class);
                if (returnTrip.equals("true")){
                    setGoingBack();
                }else {
                    findDriver();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FindingDriverActivity", "Firebase cancelled");
            }
        });
        Log.d("DEBUGTAG", "going inside setStartEnd");
    }

    public void setGoingBack() {
        mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startLat = (float) dataSnapshot.child(Database.END_LOC).child(Database.LAT).getValue(float.class);
                startLong = (float) dataSnapshot.child(Database.END_LOC).child(Database.LONG).getValue(float.class);
                endLat = (float) dataSnapshot.child(Database.START_LOC).child(Database.LAT).getValue(float.class);
                endLong = (float) dataSnapshot.child(Database.START_LOC).child(Database.LONG).getValue(float.class);
                findDriver();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FindingDriverActivity", "Firebase cancelled on return trip");
            }
        });
    }
}