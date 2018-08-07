package com.example.shannonyan.adventuresdraft.Ongoing_Flow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.shannonyan.adventuresdraft.Constants;
import com.example.shannonyan.adventuresdraft.Create_Flow.CreateFlowActivity;
import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.Uber_Helper.UberClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.rides.client.services.RidesService;

public class ReturnHomeActivity extends AppCompatActivity {

    public UberClient uberClient;
    public RidesService service;
    public String rideId;
    public String status;
    public DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_home);

        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;
        Intent intent = getIntent();
        rideId = intent.getStringExtra(Constants.RIDE_ID);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.status).child(Constants.status);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(String.class);
                if (status != null) {
                    if (status.equals("completed")) {
                        Intent i = new Intent(ReturnHomeActivity.this, CreateFlowActivity.class);
                        i.putExtra(Constants.RIDE_ID, rideId);
                        mDatabase.removeEventListener(this);
                        startActivity(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("DATABASE", "Value event listener request cancelled.");
            }
        });
    }
}
