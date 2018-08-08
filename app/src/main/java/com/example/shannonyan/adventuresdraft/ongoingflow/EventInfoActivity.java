package com.example.shannonyan.adventuresdraft.ongoingflow;

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

import com.example.shannonyan.adventuresdraft.constants.Api;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.createflow.fragments.TripOverviewFragment;
import com.example.shannonyan.adventuresdraft.modules.GlideApp;
import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.yelphelper.YelpClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uber.sdk.rides.client.model.PriceEstimate;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.client.utils.URIBuilder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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
        mDatabaseItinerary = FirebaseDatabase.getInstance().getReference().child(Database.ITINERARY_ARRAY_NAME);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        context = this;

        populateComponents();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseItinerary.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //store itinerary array from database into local var
                        itinerary = (ArrayList<String>) dataSnapshot.getValue();
                        if(itinerary != null){
                            createNextEvent(itinerary.get(0));
                            itinerary.remove(0);
                            mDatabaseItinerary.setValue(itinerary);
                        }else{
                            Intent intent = new Intent(EventInfoActivity.this, FindingDriverActivity.class);
                            intent.putExtra(Database.RETURN_TRIP, "true");
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
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

    //decides on whether to create restaurant or non-restaurant event
    public void createNextEvent(String eventType){
        if(eventType.equals(Database.EVENT_TYPE_NORM)){
            yelpClient = YelpClient.getYelpClientInstance(EventInfoActivity.this, Database.EVENT_TYPE_NORM, false);
            yelpClient.Create(Database.EVENT_TYPE_NORM, false);
            Intent intent = new Intent(EventInfoActivity.this, FindingDriverActivity.class);
            startActivity(intent);
        }else{
            yelpClient = YelpClient.getYelpClientInstance(EventInfoActivity.this, Database.EVENT_TYPE_FOOD, false);
            yelpClient.Create(Database.EVENT_TYPE_FOOD, false);
            Intent intent = new Intent(EventInfoActivity.this, FindingDriverActivity.class);
            startActivity(intent);
        }
    }

}
