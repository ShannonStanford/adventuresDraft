package com.example.shannonyan.adventuresdraft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class PrefFragment extends Fragment {

    public PrefFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pref, container, false);
        return view;
    }

    public static PrefFragment newInstance(String text) {

        PrefFragment frag = new PrefFragment();
        return frag;
    }


}
