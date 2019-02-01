package com.example.matt3865.memorableplaces;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView locationListView;
    TextView listPlaceholerTextView;

    ArrayList<LocationItem> locations;

    ArrayAdapter<LocationItem> arrayAdapter;


    public void goToMap(View view) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();

                if (extras != null) {
                    Log.i("Extras", extras.toString());
                    LatLng latestMarker = extras.getParcelable("lastMarker");
                    String latestMarkerAddress = extras.getString("lastMarkerName");

                    Log.i("Address received: ", "" + latestMarkerAddress);

                    if (latestMarker != null && latestMarkerAddress != null) {

                        listPlaceholerTextView.setVisibility(View.INVISIBLE);
                        Log.i("Location from map", latestMarker.toString());


                        locations.add(new LocationItem(latestMarker, latestMarkerAddress));

                        Log.i("Locations Updated", locations.toString());

                        arrayAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.i("Extras", "Extras are null!");
                }
            }
        }
    }


    public class LocationItem {
        LatLng latLng;
        String address;

        LocationItem(LatLng location, String address) {
            this.latLng = location;
            this.address = address;
        }

        @Override
        public String toString() {
            return address;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationListView = findViewById(R.id.locationListView);
        listPlaceholerTextView = findViewById(R.id.listPlaceholderTextView);

        locations = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locations);

        locationListView.setAdapter(arrayAdapter);


        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

                intent.putExtra("selectedLocation", locations.get(position).latLng);

                startActivityForResult(intent, 1);
            }
        });

    }
}
