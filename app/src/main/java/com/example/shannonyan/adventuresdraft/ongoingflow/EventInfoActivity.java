package com.example.shannonyan.adventuresdraft.ongoingflow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.shannonyan.adventuresdraft.yelphelper.YelpClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class EventInfoActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseItinerary;

    public ArrayList<String> itinerary;
    public YelpClient yelpClient;
    public ImageView ivEvent;
    public TextView tvEventName;
    public RatingBar eventRating;
    public Button continueButton;
    public StorageReference storageRef;
    public FirebaseStorage storage;
    public Context context;
    public Button btHome;
    public int itinerarySize;
    public KonfettiView viewKonfetti;

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
        btHome = findViewById(R.id.btHome);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.EVENT);
        mDatabaseItinerary = FirebaseDatabase.getInstance().getReference().child(Database.ITINERARY_ARRAY_NAME);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        context = this;

        populateComponents();

        viewKonfetti = findViewById(R.id.viewKonfetti);
        viewKonfetti.build()
                .addColors(Color.YELLOW, Color.BLUE, Color.MAGENTA)
                .setDirection(0.0, 90)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1300L)
                .addSizes(new Size(6, 5f))
                .addShapes(Shape.RECT, Shape.CIRCLE)
                //.setPosition(-50f, viewKonfetti.getWidth() + 50f, -50f, -50f)
                .setPosition(0, (float)viewKonfetti.getWidth(), -50f, -50f)
                .streamFor(300, 1300L);

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseItinerary.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        itinerary = (ArrayList<String>) dataSnapshot.getValue();
                        if(itinerary != null){
                            itinerarySize = itinerary.size();
                            createNextEvent(itinerary.get(0));
                            itinerary.remove(0);
                            mDatabaseItinerary.setValue(itinerary);
                            //DatabaseHelper.setItinerary(itinerary);
                        } else {
                            Intent intent = new Intent(EventInfoActivity.this, FindingDriverActivity.class);
                            intent.putExtra(Database.RETURN_TRIP, "true");
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("DATABASE", "Value event listener request cancelled.");
                    }
                });
            }
        });

        btHome.setOnClickListener(new View.OnClickListener(){
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
                Log.d("DATABASE", "Value event listener request cancelled.");
            }
        });
    }

    //decides on whether to create restaurant or non-restaurant event
    public void createNextEvent(String eventType){
        boolean last = false;
        if (itinerarySize == 1) last = true;
        if(eventType.equals(Database.EVENT_TYPE_NORM)){
            yelpClient = YelpClient.getYelpClientInstance(EventInfoActivity.this, Database.EVENT_TYPE_NORM, false, last);
            yelpClient.Create(Database.EVENT_TYPE_NORM, false);
            Intent intent = new Intent(EventInfoActivity.this, FindingDriverActivity.class);
            startActivity(intent);
        }else{
            yelpClient = YelpClient.getYelpClientInstance(EventInfoActivity.this, Database.EVENT_TYPE_FOOD, false, last);
            yelpClient.Create(Database.EVENT_TYPE_FOOD, false);
            Intent intent = new Intent(EventInfoActivity.this, FindingDriverActivity.class);
            startActivity(intent);
        }
    }

}
