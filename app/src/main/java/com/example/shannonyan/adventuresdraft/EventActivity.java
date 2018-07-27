package com.example.shannonyan.adventuresdraft;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

public class EventActivity extends AppCompatActivity {
    //component vars
    public ImageView ivEvent;
    public TextView tvEventName;
    public RatingBar eventRating;
    public Button continueButton;
    //required instance of Database for firebase read/write and image Download
    private DatabaseReference mDatabase;
    public String tripId;
    public StorageReference storageRef;
    public FirebaseStorage storage;
    public Context context;

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

        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        context = this;

        setTripId();
        populateComponents();
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventActivity.this, FindActivity.class);
                intent.putExtra("returnTrip", "true");
                startActivity(intent);
            }
        });

    }

    public void setTripId(){
        //TODO: set tripId based on corresponsing user that's logged in
        tripId = "mXdgfyxOyh1zaolkJQPU";
    }

    public void populateComponents(){
        mDatabase.child("trips").child("testTrip").child("event").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                tvEventName.setText(event.name);
                //storageRef.child(event.storageUrl);
                String storagePath = "https://firebasestorage.googleapis.com/v0/b/adventureawaits-198ee.appspot.com/o/Screen%20Shot%202018-07-23%20at%2010.32.36%20AM.png?alt=media&token=e27f5e19-6cb7-4bed-b790-fefd240fd32b";
                eventRating.setNumStars(event.rating);
                GlideApp.with(context)
                        .load(storagePath)
                        .into(ivEvent);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
