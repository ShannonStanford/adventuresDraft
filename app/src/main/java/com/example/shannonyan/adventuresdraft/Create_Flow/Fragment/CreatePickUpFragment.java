package com.example.shannonyan.adventuresdraft.Create_Flow.Fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.shannonyan.adventuresdraft.Constants;
import com.example.shannonyan.adventuresdraft.R;
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

public class CreatePickUpFragment extends Fragment implements OnMapReadyCallback {

    public SupportPlaceAutocompleteFragment placeAutoComplete;

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private double startLat;
    private double startLong;
    private ImageView arrow_l;
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
        mMap.setMinZoomPreference(ZOOM_PREF);
        LatLng ny = new LatLng(HARD_LAT, HARD_LNG);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    public CreatePickUpFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_pick_up, container, false);
        arrow_l = (ImageView) view.findViewById(R.id.arrow_l);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.TRIPS).child(Constants.TEST_TRIPS).child(Constants.UBER);
        placeAutoComplete = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_one);
        setUpPlacesAutoComp();
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addMarker(place);
                startLat = place.getLatLng().latitude;
                startLong = place.getLatLng().longitude;
                mDatabase.child(Constants.PICKUP).setValue(place.getName());
                mDatabase.child(Constants.START_LOC).child(Constants.LAT).setValue(startLat);
                mDatabase.child(Constants.START_LOC).child(Constants.LONG).setValue(startLong);
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_one);
        mapFragment.getMapAsync(this);

        arrow_l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnButtonClickListener.onButtonClicked(v);
            }
        });
        return view;
    }

    public static CreatePickUpFragment newInstance() {
        CreatePickUpFragment frag = new CreatePickUpFragment();
        return frag;
    }

    public void setUpPlacesAutoComp() {
        final String HINT = "Set your pick up location";
        placeAutoComplete.getView().setBackgroundColor(Color.WHITE);
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
