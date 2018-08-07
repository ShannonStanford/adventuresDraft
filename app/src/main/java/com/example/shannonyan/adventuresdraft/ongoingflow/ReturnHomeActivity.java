package com.example.shannonyan.adventuresdraft.ongoingflow;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    public ImageView ivEnd;

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
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.status).child(Database.status);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(String.class);
                if (status != null) {
                    if (status.equals("completed")) {
                        Intent i = new Intent(ReturnHomeActivity.this, CreateFlowActivity.class);
                        i.putExtra(Database.RIDE_ID, rideId);
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
