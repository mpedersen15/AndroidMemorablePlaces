package com.example.matt3865.memorableplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    LocationManager locationManager;
//    LocationListener locationListener;
    Marker currentLocationMarker;

    LatLng selectedCoords;
    Marker selectedMarker;
    String selectedName;


    @Override
    public void onBackPressed() {

        if (selectedCoords != null) {
            Intent data = new Intent();
            Log.i("Last Marker on Back", selectedCoords.toString() + " - " + selectedName);

            Bundle bundle = new Bundle();
            bundle.putParcelable("lastMarker", selectedCoords);
            bundle.putString("lastMarkerName", selectedName);

            data.putExtras(bundle);
            setResult(Activity.RESULT_OK, data);
        }

        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Log.i("Last Known Location", location.toString());
                LatLng coords = new LatLng(location.getLatitude(), location.getLongitude());
                currentLocationMarker.setPosition(coords);
                currentLocationMarker.setTitle("Current Location");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 12));

            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.i("MapsActivity", "In onCreate!");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i("In onMapReady", "testing...");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.i("Long Click!", latLng.toString());
                if (selectedMarker != null) {
                    selectedMarker.remove();
                }
                selectedMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("My new place"));
                selectedCoords = latLng;

                selectedName = getAddressFromCoords(latLng);

                Toast.makeText(MapsActivity.this, "Location Added! Press BACK to view!", Toast.LENGTH_LONG).show();
            }
        });


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        /*locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());
                LatLng coords = new LatLng(location.getLatitude(),location.getLongitude());
                marker.setPosition(coords);
                marker.setTitle("Current Location");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 12 ));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };*/

        LatLng selectedLocation = getIntent().getParcelableExtra("selectedLocation");

        if (selectedLocation != null) {
            Log.i("Selected Location", selectedLocation.toString());

            currentLocationMarker.setPosition(selectedLocation);
            currentLocationMarker.setTitle("Current Location");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 12));


        } else {
            Log.i("No selected location", "must've gotten here from the add button");


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Log.i("Location", location.toString());
                LatLng coords = new LatLng(location.getLatitude(), location.getLongitude());
                currentLocationMarker.setPosition(coords);
                currentLocationMarker.setTitle("Current Location");
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 12));

//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    private String getAddressFromCoords(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            return addressList.get(0).getAddressLine(0);


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
