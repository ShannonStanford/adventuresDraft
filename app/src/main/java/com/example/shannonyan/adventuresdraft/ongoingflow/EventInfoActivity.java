package com.example.shannonyan.adventuresdraft.ongoingflow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.modules.GlideApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EventInfoActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    public ImageView ivEvent;
    public TextView tvEventName;
    public RatingBar eventRating;
    public Button continueButton;
    public StorageReference storageRef;
    public FirebaseStorage storage;
    public Context context;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //bind components
        ivEvent = findViewById(R.id.ivEvent);
        tvEventName = findViewById(R.id.tvLocationName);
        eventRating = findViewById(R.id.locationRating);
        continueButton = findViewById(R.id.continueAdventure);

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        context = this;

        populateComponents();
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventInfoActivity.this, FindingDriverActivity.class);
                intent.putExtra(Database.RETURN_TRIP, "true");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.v("onBackPressed", "pressed");
    }

    public void populateComponents(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                EventDataModel eventDataModel = dataSnapshot.getValue(EventDataModel.class);
                tvEventName.setText(eventDataModel.name);
                eventRating.setNumStars(eventDataModel.rating);
                GlideApp.with(context)
                        .load(eventDataModel.downloadUrl)
                        .into(ivEvent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
