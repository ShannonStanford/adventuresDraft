package com.example.shannonyan.adventuresdraft;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class CreatePickUpFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    public SupportPlaceAutocompleteFragment placeAutoComplete;
    private DatabaseReference mDatabase;
    private double startLat;
    private double startLong;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    public CreatePickUpFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_pick_up, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        placeAutoComplete = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_one);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d("Maps", "Place selected: " + place.getName());
                addMarker(place);
                //store in database
                startLat = place.getLatLng().latitude;
                startLong = place.getLatLng().longitude;
                mDatabase.child("trips").child("testTrip").child("uber").child("startLoc").child("lat").setValue(startLat);
                mDatabase.child("trips").child("testTrip").child("uber").child("startLoc").child("long").setValue(startLong);
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_one);
        mapFragment.getMapAsync(this);

        return view;
    }

    public static CreatePickUpFragment newInstance() {
        CreatePickUpFragment frag = new CreatePickUpFragment();
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
