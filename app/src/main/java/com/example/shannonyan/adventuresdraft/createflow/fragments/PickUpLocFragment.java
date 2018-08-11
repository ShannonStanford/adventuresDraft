package com.example.shannonyan.adventuresdraft.createflow.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.shannonyan.adventuresdraft.R;
import com.example.shannonyan.adventuresdraft.constants.Database;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
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

public class PickUpLocFragment extends Fragment implements OnMapReadyCallback {

    public SupportPlaceAutocompleteFragment placeAutoComplete;

    private GoogleMap mMap;
    private double startLat;
    private double startLong;
    private DatabaseReference mDatabase;
    private Button btPrev;
    private Button btNext;
    private FragmentChangeInterface fragmentChangeInterface;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            fragmentChangeInterface = (FragmentChangeInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(((Activity) context).getLocalClassName()
                    + " must implement OnButtonClickListener");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final int ZOOM_PREF = 14;
        final double HARD_LAT = 37.479222;
        final double HARD_LNG = -122.152279;

        mMap = googleMap;
//        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.greyscale));
        mMap.setMinZoomPreference(ZOOM_PREF);
        LatLng ny = new LatLng(HARD_LAT, HARD_LNG);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        mMap.getUiSettings().setZoomControlsEnabled(false);
    }

    public PickUpLocFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_pick_up, container, false);
        btNext = view.findViewById(R.id.btNext);
        btPrev = view.findViewById(R.id.btPrev);
        btNext.setEnabled(false);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(com.example.shannonyan.adventuresdraft.constants.Database.TRIPS).child(Database.TEST_TRIPS).child(Database.UBER);
        placeAutoComplete = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_one);
        setUpPlacesAutoComp();

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("US")
                .build();

        placeAutoComplete.setFilter(autocompleteFilter);

        placeAutoComplete.getView().setBackgroundColor(getResources().getColor(R.color.trans_white));
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addMarker(place);
                startLat = place.getLatLng().latitude;
                startLong = place.getLatLng().longitude;
                mDatabase.child(com.example.shannonyan.adventuresdraft.constants.Database.PICKUP).setValue(place.getName());
                mDatabase.child(Database.START_LOC).child(Database.LAT).setValue(startLat);
                mDatabase.child(Database.START_LOC).child(Database.LONG).setValue(startLong);
                mDatabase.child(Database.HOME_LOC).child(Database.LAT).setValue(startLat);
                mDatabase.child(Database.HOME_LOC).child(Database.LONG).setValue(startLong);
                btNext.setEnabled(true);
                btNext.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.bluearrow, null));
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_one);
        mapFragment.getMapAsync(this);

        btPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentChangeInterface.onButtonClicked(v);
            }
        });
        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentChangeInterface.onButtonClicked(v);
            }
        });
        return view;
    }

    public static PickUpLocFragment newInstance() {
        PickUpLocFragment frag = new PickUpLocFragment();
        return frag;
    }

    public void setUpPlacesAutoComp() {
        final String HINT = "Pick up location";
        placeAutoComplete.getView().setBackgroundColor(getResources().getColor(R.color.background_material_light));
        placeAutoComplete.setHint(HINT);
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
