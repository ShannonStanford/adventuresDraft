package com.example.shannonyan.adventuresdraft.createflow;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.ongoingflow.DriverInfoActivity;
import com.example.shannonyan.adventuresdraft.ongoingflow.ReturnHomeActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MultEventSelector extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.Adapter adapter;
    public ArrayList<String> allEvents;
    public DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mult_event_selector);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.ITINERARY_ARRAY_NAME);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        allEvents = new ArrayList<>();
        allEvents.add(Database.EVENT_TYPE_NORM);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(allEvents);
        recyclerView.setAdapter(adapter);
        updateItinerary(allEvents);
    }

    public void addEventNorm(View view){
        allEvents.add(Database.EVENT_TYPE_NORM);
        adapter.notifyItemInserted(adapter.getItemCount());
        updateItinerary(allEvents);
    }

    public void addEventFood(View view){
        allEvents.add(Database.EVENT_TYPE_FOOD);
        adapter.notifyItemInserted(adapter.getItemCount());
        updateItinerary(allEvents);
    }

    public void doneAdding(View view){
        Intent i = new Intent(MultEventSelector.this, CreateFlowActivity.class);
        startActivity(i);
    }

    public void updateItinerary(ArrayList<String> itinerary){
        mDatabase.setValue(itinerary);
    }

}
