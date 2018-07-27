package com.example.shannonyan.adventuresdraft;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class CreateThirdFragment extends Fragment {

    private TextView pickupAns;
    private TextView priceAns;
    private TextView cityAns;
    public Button create;
    private DatabaseReference mDatabase;

    public CreateThirdFragment() { }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_third, container, false);
        pickupAns = view.findViewById(R.id.pickup_ans);
        priceAns = view.findViewById(R.id.price_ans);
        cityAns = view.findViewById(R.id.city_ans);
        create = view.findViewById(R.id.create);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        create = (Button) view.findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: get priceCap and users food preferences
                //TODO: pass in above info into yelp API call and determine which restaurant/event from response
                //TODO: store relevant data about restaurant/event in database
                //launch start activity
                Intent intent = new Intent(getActivity(), StartActivity.class);
                startActivity(intent);
            }
        });
        mDatabase.child("trips").child("testTrip").child("uber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child("cityOfInterest").getValue(String.class));
                priceAns.setText(dataSnapshot.child("priceCap").getValue(String.class));
                pickupAns.setText(dataSnapshot.child("pickUpName").getValue(String.class));
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
        mDatabase.child("trips").child("testTrip").child("uber").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cityAns.setText(dataSnapshot.child("cityOfInterest").getValue(String.class));
                priceAns.setText(dataSnapshot.child("priceCap").getValue(String.class));
                pickupAns.setText(dataSnapshot.child("pickUpName").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static CreateThirdFragment newInstance() {

        CreateThirdFragment frag = new CreateThirdFragment();
        return frag;
    }
}