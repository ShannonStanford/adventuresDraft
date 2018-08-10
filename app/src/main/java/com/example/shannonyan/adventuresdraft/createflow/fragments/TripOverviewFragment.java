package com.example.shannonyan.adventuresdraft.createflow.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.example.shannonyan.adventuresdraft.ongoingflow.FindingDriverActivity;
import com.example.shannonyan.adventuresdraft.yelphelper.YelpClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TripOverviewFragment extends Fragment {

    private TextView pickupAns;
    private TextView priceAns;
    private TextView cityAns;
    private TextView numPeepAns;
    private ImageView arrow_l;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseItinerary;
    public Button create;
    public ArrayList<String> itinerary;
    public YelpClient yelpClient;
    private OnButtonClickListener mOnButtonClickListener;

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnButtonClickListener = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).getLocalClassName() + " must implement OnButtonClickListener");
        }
    }

    public TripOverviewFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_third, container, false);
        arrow_l = (ImageView) view.findViewById(R.id.arrow_l);
        pickupAns = view.findViewById(R.id.pickup_ans);
        priceAns = view.findViewById(R.id.price_ans);
        cityAns = view.findViewById(R.id.city_ans);
        create = view.findViewById(R.id.create);
        numPeepAns = view.findViewById(R.id.num_peeps_ans);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseItinerary = FirebaseDatabase.getInstance().getReference().child("itinerary");
        create = (Button) view.findViewById(R.id.create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressIndicator();
                mDatabaseItinerary.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //store itinerary array from database into local var
                        itinerary = (ArrayList<String>) dataSnapshot.getValue();
                        if(itinerary != null){
                            yelpClient = YelpClient.getYelpClientInstance(getContext(), itinerary.get(0), true);
                            itinerary.remove(0);
                            mDatabaseItinerary.setValue(itinerary);
                        }else{
                            Intent intent = new Intent(getContext(), FindingDriverActivity.class);
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


        mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numPeepAns.setText(String.valueOf(dataSnapshot.child(Database.NUM_PEEPS).getValue(Integer.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setValues();
    }

    public void setValues(){
        mDatabase.child(Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child(Database.CITY_OF_INTEREST).getValue(String.class));
                priceAns.setText(dataSnapshot.child(Database.PRICECAP).getValue(String.class));
                pickupAns.setText(dataSnapshot.child(Database.PICKUP).getValue(String.class));
                numPeepAns.setText(String.valueOf(dataSnapshot.child(Database.NUM_PEEPS).getValue(Integer.class)));

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static TripOverviewFragment newInstance() {
        TripOverviewFragment frag = new TripOverviewFragment();
        return frag;
    }

    public void showProgressIndicator(){
        ProgressDialog progress;
        progress = new ProgressDialog(getContext());
        progress.setTitle(Database.LOADING_TITLE);
        progress.setMessage(Database.LOADING_MESSAGE);
        progress.setCancelable(true);
        progress.setIcon(R.drawable.spaceship_dark);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
    }
}