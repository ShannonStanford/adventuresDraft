package com.example.shannonyan.adventuresdraft.Profile_Flow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.shannonyan.adventuresdraft.Constants;
import com.example.shannonyan.adventuresdraft.Modules.GlideApp;
import com.example.shannonyan.adventuresdraft.Profile_Flow.Fragments.profileViewAdapter;
import com.example.shannonyan.adventuresdraft.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements com.example.shannonyan.adventuresdraft.Profile_Flow.Fragments.PrefFragment.onButtonClickedListener{

    private ViewPager viewPager;
    private profileViewAdapter adapter;
    private DatabaseReference mDatabase;
    private TabLayout tabLayout;
    private ImageView ivProfile;

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

        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USER).child(Constants.TEST_USER);
    }

    @Override
    public void onButtonClicked(String name) {
        if(name.equals(Constants.FOOD)){
            onPrefButtonClicked(Constants.FOOD);
        } else if (name.equals(Constants.CAR)){
            onPrefButtonClicked(Constants.CAR);
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
            selectedChoice = Constants.FOOD_PREF;

        } else {
            Title = "Select the car you prefer:";
            options = new String[] {"UberX", "UberXL", "UberSELECT","UberBLACK", "UberSUV", "UberLUX"};
            selectedChoice = Constants.CAR_PREF;
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