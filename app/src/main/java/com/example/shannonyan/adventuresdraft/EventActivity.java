package com.example.shannonyan.adventuresdraft;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
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

    }

    public void setTripId(){
        //TODO: set tripId based on corresponsing user that's logged in
        tripId = "mXdgfyxOyh1zaolkJQPU";
    }

    public void populateComponents(){
        mDatabase.child("trip").child(tripId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                JSONObject object = (JSONObject) dataSnapshot.getValue();
                Event event = null;
                try {
                    event = Event.fromJSON(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tvEventName.setText(event.title);
                eventRating.setNumStars(event.numStars);
                GlideApp.with(context)
                        .load(storageRef.child(event.storageUrl))
                        .into(ivEvent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
