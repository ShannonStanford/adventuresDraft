package com.example.shannonyan.adventuresdraft.ongoingflow;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.uberhelper.UberClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uber.sdk.rides.client.services.RidesService;

public class RideInProgressActivity extends AppCompatActivity {

    public RidesService service;
    public UberClient uberClient;
    public String rideID;
    public DatabaseReference mDatabase;
    public String status;

    private ImageView ivBackgroundFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_in_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uberClient = UberClient.getUberClientInstance(this);
        service = uberClient.service;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        TextView prepare = (TextView) findViewById(R.id.tvPrepare);
        TextView your = (TextView) findViewById(R.id.tvYour);
        TextView forTakeoff = (TextView) findViewById(R.id.tvPrepare2);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/round.otf");
        prepare.setTypeface(typeface);
        your.setTypeface(typeface);
        forTakeoff.setTypeface(typeface);

        ivBackgroundFind = findViewById(R.id.ivBackgroundFind);
        Glide.with(getBaseContext())
                .load(R.drawable.rocket_telescope)
                .into(ivBackgroundFind);

        Intent intent = getIntent();
        rideID = intent.getStringExtra(Database.RIDE_ID);

        mDatabase.child(Database.status).child(Database.status).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status = dataSnapshot.getValue(String.class);
                if (status != null) {
                    if (status.equals("completed")) {
                        Intent i = new Intent(RideInProgressActivity.this, EventInfoActivity.class);
                        i.putExtra(Database.RIDE_ID, rideID);
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
