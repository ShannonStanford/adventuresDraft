package com.example.shannonyan.adventuresdraft;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements PrefFragment.onButtonClickedListener{

    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onButtonClicked(String name) {
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

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        dialog = builder.create();
        dialog.show();
    }

}
