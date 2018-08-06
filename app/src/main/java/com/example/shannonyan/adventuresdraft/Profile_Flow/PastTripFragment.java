package com.example.shannonyan.adventuresdraft.Profile_Flow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shannonyan.adventuresdraft.R;

public class PastTripFragment extends Fragment {

    public PastTripFragment() {

    }

    public static PastTripFragment newInstance() {
        PastTripFragment frag = new PastTripFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_trip, container, false);
        return view;
    }
}
