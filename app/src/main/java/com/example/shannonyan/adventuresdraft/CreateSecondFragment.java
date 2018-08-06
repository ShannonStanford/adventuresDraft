package com.example.shannonyan.adventuresdraft;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateSecondFragment extends Fragment implements OnMapReadyCallback {

    public SupportPlaceAutocompleteFragment placeAutoComplete;

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private String cityInterest;
    private EditText etPrice;
    private NumberPicker numPicker;

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

    public CreateSecondFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_second, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.UBER);
        etPrice = view.findViewById(R.id.etPrice);
        numPicker = view.findViewById(R.id.num_picker);
        setUpNumPicker();
        placeAutoComplete = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setUpPlacesFrag();

        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addMarker(place);
                cityInterest = place.getAddress().toString();
                mDatabase.child(Constants.CITY_OF_INTEREST).setValue(cityInterest);
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
                    mDatabase.child(Constants.PRICECAP).setValue(priceCap);
                }
            }
        });
        //stores new number of people
        numPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mDatabase.child(Constants.NUM_PEEPS).setValue(numPicker.getValue());
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
        final String HINT = "Pick your City of Interest";
        placeAutoComplete.getView().setBackgroundColor(Color.WHITE);
        placeAutoComplete.setHint(HINT);
    }

    public static CreateSecondFragment newInstance() {
        CreateSecondFragment frag = new CreateSecondFragment();
        return frag;
    }

    public void addMarker(Place p){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(p.getLatLng());
        markerOptions.title((String)p.getName());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }
}
