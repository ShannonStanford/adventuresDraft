package com.example.shannonyan.adventuresdraft;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
    private EditText etPeeps;
    private EditText etPrice;
    private final double HARD_LAT = 37.669695;
    private final double HARD_LNG = -122.260088;
    private final int ZOOM_PREF = 9;

    @Override
    public void onMapReady(GoogleMap googleMap) {
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        etPeeps = view.findViewById(R.id.etNumPeeps);
        etPrice = view.findViewById(R.id.etPrice);
        placeAutoComplete = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.getView().setBackgroundColor(Color.DKGRAY);
        placeAutoComplete.setHint("Pick your City of Interest");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //stores city of interest
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Maps", "Place selected: " + place.getName());
                addMarker(place);
                //store in database
                cityInterest = place.getAddress().toString();
                mDatabase.child("trips").child("testTrip").child("uber").child("cityOfInterest").setValue(cityInterest);
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
                    mDatabase.child("trips").child("testTrip").child("uber").child("priceCap").setValue(priceCap);
                }
            }
        });
        //stores new number of people
        etPeeps.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    int priceCap = Integer.parseInt(etPeeps.getText().toString());
                    mDatabase.child("trips").child("testTrip").child("uber").child("numPeeps").setValue(priceCap);
                }
            }
        });

        return view;
    }

    public static CreateSecondFragment newInstance() {

        CreateSecondFragment frag = new CreateSecondFragment();
        return frag;
    }

    public void addMarker(Place p){

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(p.getLatLng());
        markerOptions.title(p.getName()+"");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(p.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

    }
}
