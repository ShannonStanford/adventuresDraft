package com.example.shannonyan.adventuresdraft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    public Button btLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btLaunch = (Button) findViewById(R.id.btLaunch);

        onClickToFindActivity();
    }

    public void onClickToFindActivity(){
        btLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent i = new Intent(getBaseContext(), FindActivity.class);
                 startActivity(i);
            }
        });
    }


}
