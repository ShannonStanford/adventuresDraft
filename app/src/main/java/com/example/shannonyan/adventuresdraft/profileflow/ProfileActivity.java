package com.example.shannonyan.adventuresdraft.profileflow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shannonyan.adventuresdraft.constants.Api;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.createflow.CreateFlowActivity;
import com.example.shannonyan.adventuresdraft.modules.GlideApp;
import com.example.shannonyan.adventuresdraft.profileflow.fragments.profileViewAdapter;
import com.example.shannonyan.adventuresdraft.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements com.example.shannonyan.adventuresdraft.profileflow.fragments.PrefFragment.onButtonClickedListener{

    private ViewPager viewPager;
    private profileViewAdapter adapter;
    private DatabaseReference mDatabase;
    private TabLayout tabLayout;
    private ImageView ivProfile;
    private Button btBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        ivProfile = findViewById(R.id.ivProfilePic);

        GlideApp.with(getBaseContext())
                .load(R.drawable.profile)
                .into(ivProfile);

        adapter = new profileViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        btBack = findViewById(R.id.btBack);
        onBackButtonClick();

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Database.USER).child(Database.TEST_USER);
    }

    public void onBackButtonClick(){
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), CreateFlowActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onButtonClicked(String name) {
        if(name.equals(Database.FOOD)){
            onPrefButtonClicked(Database.FOOD);
        } else if (name.equals(Database.CAR)){
            onPrefButtonClicked(Database.CAR);
        }

    }

    public void onPrefButtonClicked(String choice){
        AlertDialog dialog;
        String Title = "";
        String selectedChoice = "";
        final String[] options;
        final ArrayList selections = new ArrayList();


        if(choice.equals("food")){
            Title = "Select the cuisine you prefer:";
            options = new String[] {"American","Barbeque","Beer Garden", "Brazilian", "Burgers", "Caribbean",
                    "Chinese", "Fast Food", "French", "German", "Greek", "Hawaiian", "Indian",
                    "Italian","Japanese", "Korean", "Mediterranean", "Portuguese", "Salad", "Sandwiches", "Seafood", "Southern", "Spanish"};
            selectedChoice = Database.FOOD_PREF;

        } else {
            Title = "Select the car you prefer:";
            options = new String[] {"UberX", "UberXL", "UberSELECT","UberBLACK", "UberSUV", "UberLUX"};
            selectedChoice = Database.CAR_PREF;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Title);
        final String finalSelectedChoice = selectedChoice;
        builder.setMultiChoiceItems(options, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        String selected = options[indexSelected];
                        if (isChecked) {
                            selections.add(selected);
                        } else if (selections.contains(selected)) {
                            selections.remove(selected);
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.child(finalSelectedChoice).setValue(selections);
                        Toast.makeText(getBaseContext(),"Preferences updated", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getBaseContext(),"Preferences not updated", Toast.LENGTH_LONG).show();
                    }
                });

        dialog = builder.create();
        dialog.show();
    }
}
