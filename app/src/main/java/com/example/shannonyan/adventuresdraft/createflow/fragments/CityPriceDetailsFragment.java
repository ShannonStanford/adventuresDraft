package com.example.shannonyan.adventuresdraft.createflow.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.databasehelper.DatabaseHelper;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CityPriceDetailsFragment extends Fragment implements OnMapReadyCallback {

    public SupportPlaceAutocompleteFragment placeAutoComplete;

    private GoogleMap mMap;
    private String cityInterest;
    private EditText etPrice;
    private NumberPicker numPicker;
    private ImageView arrow_r;
    private OnButtonClickListener mOnButtonClickListener;
    private Button btNext;

    public interface OnButtonClickListener{
        void onButtonClicked(View view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnButtonClickListener = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).getLocalClassName()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final double HARD_LAT = 37.669695;
        final double HARD_LNG = -122.260088;
        final int ZOOM_PREF = 9;

        mMap = googleMap;
        mMap.setMinZoomPreference(ZOOM_PREF);
        LatLng ny = new LatLng(HARD_LAT, HARD_LNG);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    public CityPriceDetailsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_second, container, false);
        arrow_r = (ImageView) view.findViewById(R.id.arrow_r);
        etPrice = view.findViewById(R.id.etPrice);
        numPicker = view.findViewById(R.id.num_picker);
        setUpNumPicker();
        placeAutoComplete = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpPlacesFrag();

        btNext = view.findViewById(R.id.btNext);

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("here", "clicked");
                mOnButtonClickListener.onButtonClicked(v);
            }
        });
        btNext.setEnabled(false);

        arrow_r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });

        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addMarker(place);
                cityInterest = place.getAddress().toString();
                DatabaseHelper.setCity(cityInterest);
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });
        //stores new price cap
        etPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    String priceCap = etPrice.getText().toString();
                    DatabaseHelper.setPriceCap(priceCap);
                }
            }
        });
        //stores new number of people
        numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                DatabaseHelper.setNumPeople(numPicker.getValue());
                btNext.setEnabled(true);
            }
        });

        return view;
    }

    public void setUpNumPicker(){
        final int MAX_PEOPLE = 6;
        final int MIN_PEOPLE = 1;
        numPicker.setWrapSelectorWheel(false);
        numPicker.setMaxValue(MAX_PEOPLE);
        numPicker.setMinValue(MIN_PEOPLE);
    }

    public void setUpPlacesFrag(){
        final String HINT = "City of Interest";
        placeAutoComplete.getView().setBackgroundColor(getResources().getColor(R.color.trans_white));
        placeAutoComplete.setHint(HINT);
    }

    public static CityPriceDetailsFragment newInstance() {
        CityPriceDetailsFragment frag = new CityPriceDetailsFragment();
        return frag;
    }

    public void addMarker(Place p){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(p.getLatLng());
        markerOptions.title((String)p.getName());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(markerOptions);
        mMap.setPadding(0, 0, 0, 0);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p.getLatLng()));
        mMap.setPadding(0, 50, 0, 0);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }
}
