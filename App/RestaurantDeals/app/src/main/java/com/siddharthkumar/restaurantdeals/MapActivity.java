package com.siddharthkumar.restaurantdeals;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.maps.*;
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{
    MapView mv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle(getIntent().getStringExtra("caller"));
        mv=(MapView) findViewById(R.id.map);
        mv.onCreate(savedInstanceState);
        mv.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mv.onResume();
    }
}
