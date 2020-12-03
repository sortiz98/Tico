package com.example.tico;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
// [START maps_marker_on_map_ready]
public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar3);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        queue = Volley.newRequestQueue(this);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent = getIntent();
        String location = intent.getStringExtra(DetailsActivity.EXTRA_MESSAGE);
        //String location= "2967 Vanport Dr, San Jose, CA, 95122";
        addLocation(location, googleMap);
    }

    public void addLocation(final String address, final GoogleMap googleMap) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + getResources().getString(R.string.Google_API_Key);
        JsonObjectRequest locationRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject results = response.getJSONArray("results").getJSONObject(0);
                            JSONObject location = results.getJSONObject("geometry").getJSONObject("location");

                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                            LatLng current = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                            googleMap.addMarker(new MarkerOptions()
                                    .position(current)
                                    .icon(bitmapDescriptor)
                                    .title("Selected Location")
                                    .snippet(address)).showInfoWindow();
                            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(current));
                            float zoomLevel = 11.5f; //This goes up to 21
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoomLevel));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("addCurrentLocation", error.toString());
            }
        });
        queue.add(locationRequest);
    }
}