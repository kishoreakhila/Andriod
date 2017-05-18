package com.smartstreet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener {
    // The instance of the map object provided by google play services.
    private GoogleMap map;

    public static Intent createIntent(Context context) {
        final Intent intent = new Intent(context, MapsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();
        // enables the find my location feature on the map.
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);

        final EditText mapSearchQuery = (EditText) findViewById(R.id.search_box);
        final Button mapSearchButton = (Button) findViewById(R.id.search_button);
        mapSearchButton.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mapSearchQuery.getWindowToken(), 0);

                if (!TextUtils.isEmpty(mapSearchQuery.getText())) {
                    // Launch the async request to search on the map
                    new SearchClicked(mapSearchQuery.getText().toString()).execute();
                    // clear the search field
                    mapSearchQuery.setText("", TextView.BufferType.EDITABLE);
                }
            }
        });
    }

    // Animates the map camera to a particular location passed.
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        if (currentLocation == null) {
            return;
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Show the place details whenever a place marker is clicked.
        new AlertDialog.Builder(this)
                .setTitle(marker.getTitle())
                .setMessage(marker.getSnippet())
                .show();
        return true;
    }

    private class SearchClicked extends AsyncTask<Void, Void, Void> {
        private String toSearch;
        private Address address;

        public SearchClicked(String toSearch) {
            this.toSearch = toSearch;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
                List<Address> results = geocoder.getFromLocationName(toSearch, 1);

                if (results.size() == 0) {
                    return null;
                }

                // Store the address returned in the first result.
                address = results.get(0);

            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!address.hasLatitude() || !address.hasLongitude()) {
                Toast.makeText(MapsActivity.this, "Invalid location returned", Toast.LENGTH_SHORT);
            }

            LatLng result = new LatLng(address.getLatitude(), address.getLongitude());

            final StringBuilder addressString = new StringBuilder();
            addressString.append("Address: ").append("\n");
            for (int i = 0; i <= address.getMaxAddressLineIndex(); ++i) {
                addressString.append(address.getAddressLine(i)).append("\n");
            }
            addressString.append("\n").append("Phone Number: ").append(address.getPhone() != null ? address.getPhone() : "No contact info found").append("\n");

            // Put the resulting location as a marker on the map
            map.addMarker(new MarkerOptions()
                    .position(result).title(address.getFeatureName() != null ? address.getFeatureName() : "No name for the location returned")
                    .snippet(addressString.toString()));
            // Move the camera to the location of the marker
            moveToCurrentLocation(result);
        }
    }
}
