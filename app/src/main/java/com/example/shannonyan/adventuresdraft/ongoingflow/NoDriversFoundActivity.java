package com.example.shannonyan.adventuresdraft.ongoingflow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.shannonyan.adventuresdraft.Api;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.createflow.CreateFlowActivity;
import com.example.shannonyan.adventuresdraft.R;


public class NoDriversFoundActivity extends AppCompatActivity {

    public Button tryagain;
    public Button exit;
    public String rideId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drivers_found);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // bind buttons
        tryagain = (Button) findViewById(R.id.tryagain);
        exit = (Button) findViewById(R.id.exit);
        rideId = getIntent().getStringExtra(Database.RIDE_ID);

        // cancel ride
        tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoDriversFoundActivity.this, FindingDriverActivity.class);
                startActivity(intent);
            }
        });

        // create new adventure or start over? -- should be create a new adventure
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoDriversFoundActivity.this, CreateFlowActivity.class);
                startActivity(intent);
            }
        });
    }
}
