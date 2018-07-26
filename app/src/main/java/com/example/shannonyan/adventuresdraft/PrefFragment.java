package com.example.shannonyan.adventuresdraft;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PrefFragment extends Fragment {
    public Button btFood;
    public Button btCar;
    public onButtonClickedListener callback;

    public PrefFragment() {

    }

    public interface onButtonClickedListener {
        public void onButtonClicked(String name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (onButtonClickedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public static PrefFragment newInstance(String text) {
        PrefFragment frag = new PrefFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pref, container, false);
        btFood = (Button) view.findViewById(R.id.btFood);
        btCar = (Button) view.findViewById(R.id.btCar);

        onFoodButtonClick();
        onCarButtonClick();
        return view;
    }

    public void onFoodButtonClick(){
        btFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onButtonClicked("food");
            }
        });
    }

    public void onCarButtonClick() {
        btCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onButtonClicked("car");
            }
        });
    }
}
