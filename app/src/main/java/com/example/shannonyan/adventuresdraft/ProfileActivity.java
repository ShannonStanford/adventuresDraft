package com.example.shannonyan.adventuresdraft;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements PrefFragment.onButtonClickedListener{

    private ViewPager viewPager;
    private profileViewAdapter adapter;
    private DatabaseReference mDatabase;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        adapter = new profileViewAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("user").child("testUser");
    }

    @Override
    public void onButtonClicked(String name) {
        if(name.equals("food")){
            onFoodButtonClicked();
        } else if (name.equals("car")){
            onCarButtonClicked();
        }

    }

    public void onFoodButtonClicked(){
        AlertDialog dialog;
        final String[] foods = {"American","Barbeque","Beer Garden", "Brazilian", "Burgers", "Caribbean",
                "Chinese", "Fast Food", "French", "German", "Greek", "Hawaiian", "Indian",
                "Italian","Japanese", "Korean", "Mediterranean", "Portuguese", "Salad", "Sandwiches", "Seafood", "Southern", "Spanish"};
        final ArrayList selectedFoods = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the cuisines you prefer:");
        builder.setMultiChoiceItems(foods, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        String cuisine = foods[indexSelected];
                        if (isChecked) {
                            selectedFoods.add(cuisine);
                        } else if (selectedFoods.contains(cuisine)) {
                            selectedFoods.remove(cuisine);
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.child("foodPref").setValue(selectedFoods);
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

    public void onCarButtonClicked(){
        AlertDialog dialog;
        final String[] foods = {"UberX", "UberXL", "UberSELECT","UberBLACk", "UberSUV", "UberLUX"};
        final ArrayList selectedFoods = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select the ride you prefer:");
        builder.setMultiChoiceItems(foods, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        String cuisine = foods[indexSelected];
                        if (isChecked) {
                            selectedFoods.add(cuisine);
                        } else if (selectedFoods.contains(cuisine)) {
                            selectedFoods.remove(cuisine);
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mDatabase.child("carPref").setValue(selectedFoods);
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
