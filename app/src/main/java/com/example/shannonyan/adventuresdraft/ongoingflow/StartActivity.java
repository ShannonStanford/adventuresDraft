package com.example.shannonyan.adventuresdraft.ongoingflow;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shannonyan.adventuresdraft.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity implements OnMapReadyCallback{

    public Button btLaunch;
    public GoogleMap gMap;

    private MapView mapView;
    private float startLat;
    private float startLong;
    private DatabaseReference mDatabase;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int ZOOM_PREF = 14;
    private static final int STROKE_WIDTH = 6;
    private static final int CURR_LOCATION_CIRCLE_RADIUS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(com.example.shannonyan.adventuresdraft.constants.Database.TRIPS).child(com.example.shannonyan.adventuresdraft.constants.Database.TEST_TRIPS).child(com.example.shannonyan.adventuresdraft.constants.Database.UBER);
        btLaunch = (Button) findViewById(R.id.btLaunch);

        onClickToFindActivity();

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapViewStart);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    public void onClickToFindActivity(){
        btLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent i = new Intent(getBaseContext(), FindingDriverActivity.class);
                 startActivity(i);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMinZoomPreference(ZOOM_PREF);
        gMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        gMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();
        getStartLatLong();
        gMap.getUiSettings().setZoomControlsEnabled(false);
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (gMap != null) {
            gMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng hotel = new LatLng(startLat, startLong);
        gMap.moveCamera(CameraUpdateFactory.newLatLng(hotel));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }
        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    gMap.setMinZoomPreference(ZOOM_PREF);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
                new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    gMap.setMinZoomPreference(ZOOM_PREF);
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    circleOptions.radius(CURR_LOCATION_CIRCLE_RADIUS);
                    circleOptions.fillColor(Color.RED);
                    circleOptions.strokeWidth(STROKE_WIDTH);
                    gMap.addCircle(circleOptions);
                }
            };

    public void getStartLatLong(){
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                startLat = (float) dataSnapshot.child(com.example.shannonyan.adventuresdraft.constants.Database.START_LOC).child(com.example.shannonyan.adventuresdraft.constants.Database.LAT).getValue(float.class);
                startLong = (float) dataSnapshot.child(com.example.shannonyan.adventuresdraft.constants.Database.START_LOC).child(com.example.shannonyan.adventuresdraft.constants.Database.LONG).getValue(float.class);
                Log.d("start", String.valueOf(startLat));
                Log.d("start", String.valueOf(startLong));
                LatLng ny = new LatLng(startLat, startLong);
                gMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
                setPickUpMarker();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FindingDriverActivity", "Firebase cancelled");
            }
        });
    }

    public void setPickUpMarker(){
        gMap.addMarker(new MarkerOptions().position(new LatLng(startLat, startLong)).title("Pickup Location"));
    }
}
