package com.example.shannonyan.adventuresdraft.createflow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MultEventSelector extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.Adapter adapter;
    public ArrayList<String> allEvents;
    public DatabaseReference mDatabase;
    FloatingActionMenu fabMenu;
    FloatingActionButton done, food, event;
    Button btDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mult_event_selector);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.ITINERARY_ARRAY_NAME);
        allEvents = new ArrayList<>();
        allEvents.add(Database.EVENT_TYPE_NORM);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(allEvents);
        fabMenu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
        //done = (FloatingActionButton) findViewById(R.id.fabDone);
        food = (FloatingActionButton) findViewById(R.id.fabFood);
        event = (FloatingActionButton) findViewById(R.id.fabEvent);
        btDone = (Button) findViewById(R.id.btDone);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.logo2);
        getSupportActionBar().setTitle(null);

        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        updateItinerary(allEvents);

        btDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allEvents.size() == 0) {
                    Toast.makeText(getBaseContext(),"Must add at least one event", Toast.LENGTH_SHORT);
                } else {
                    Intent i = new Intent(MultEventSelector.this, CreateFlowActivity.class);
                    startActivity(i);
                }
            }
        });

//        done.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                if(allEvents.size() == 0) {
//
//                } else {
//                    Intent i = new Intent(MultEventSelector.this, CreateFlowActivity.class);
//                    startActivity(i);
//                }
//
//            }
//        });

        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allEvents.add(Database.EVENT_TYPE_NORM);
                adapter.notifyItemInserted(adapter.getItemCount());
                updateItinerary(allEvents);
            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allEvents.add(Database.EVENT_TYPE_FOOD);
                adapter.notifyItemInserted(adapter.getItemCount());
                updateItinerary(allEvents);
            }
        });
    }

    public void updateItinerary(ArrayList<String> itinerary){
        mDatabase.setValue(itinerary);
    }

}
