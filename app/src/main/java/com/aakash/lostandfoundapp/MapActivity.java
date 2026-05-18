package com.aakash.lostandfoundapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseHelper dbHelper;
    private FusedLocationProviderClient fusedLocationClient;
    private double userLat = 0, userLng = 0;
    private SeekBar seekBarRadius;
    private TextView tvRadius;
    private static final int LOCATION_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dbHelper           = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        seekBarRadius      = findViewById(R.id.seekBarRadius);
        tvRadius           = findViewById(R.id.tvRadius);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int radius = progress + 1;
                tvRadius.setText("Radius: " + radius + " km");
                if (mMap != null) loadMarkers(radius);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        getUserLocation();
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLat = location.getLatitude();
                userLng = location.getLongitude();
                LatLng userLatLng = new LatLng(userLat, userLng);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(-25.2744, 133.7751), 4));
                Toast.makeText(this, "Could not get current location",
                        Toast.LENGTH_SHORT).show();
            }
            loadMarkers(seekBarRadius.getProgress() + 1);
        });
    }

    private void loadMarkers(int radiusKm) {
        mMap.clear();
        List<LostFoundItem> items = dbHelper.getAllItems();
        int count = 0;

        for (LostFoundItem item : items) {
            double itemLat = item.getLat();
            double itemLng = item.getLng();
            if (itemLat == 0.0 && itemLng == 0.0) continue;
            if (userLat != 0 && userLng != 0) {
                float[] results = new float[1];
                Location.distanceBetween(userLat, userLng,
                        itemLat, itemLng, results);
                float distanceKm = results[0] / 1000;
                if (distanceKm > radiusKm) continue;
            }

            LatLng position = new LatLng(itemLat, itemLng);

            // Here the Red is for the Lost items, and the Green dot is for the Found items.
            float markerColor = item.getType().equalsIgnoreCase("Lost")
                    ? BitmapDescriptorFactory.HUE_RED
                    : BitmapDescriptorFactory.HUE_GREEN;

            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(item.getType() + ": " + item.getDescription())
                    .snippet("📍 " + item.getLocation() + " | 📂 " + item.getCategory())
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

            count++;
        }

        Toast.makeText(this, count + " items shown on map", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        }
    }
}