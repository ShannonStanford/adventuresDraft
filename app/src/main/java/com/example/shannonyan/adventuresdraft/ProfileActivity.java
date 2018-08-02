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
        if(name.equals(Constants.FOOD)){
            onFoodButtonClicked();
        } else if (name.equals(Constants.CAR)){
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
                        mDatabase.child(Constants.FOOD_PREF).setValue(selectedFoods);
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
        final String Title = "Select the ride you prefer:";
        AlertDialog dialog;
        final String[] foods = {"UberX", "UberXL", "UberSELECT","UberBLACk", "UberSUV", "UberLUX"};
        final ArrayList selectedFoods = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Title);
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
                        mDatabase.child(Constants.CAR_PREF).setValue(selectedFoods);
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
            selectedChoice = "food";

        } else {
            Title = "Select the car you prefer:";
            options = new String[] {"UberX", "UberXL", "UberSELECT","UberBLACK", "UberSUV", "UberLUX"};
            selectedChoice = "car";
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
