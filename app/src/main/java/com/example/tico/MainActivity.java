package com.example.tico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // Type conversion:     static String API_KEY = R.string.Google_API_Key;
    static String GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    String address = ""; // might not need
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Nearby Search results
    private String restaurant_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    // Text based search results
    private String SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/xml?query=restaurants+in+Sydney&key=YOUR_API_KEY";

    /** TODO */
    String type = "currentLocation";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (type.equals("currentLocation")) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    double lat = location.getLatitude(), lon = location.getLongitude();
                    findRestaurantsByCoordinate(lat, lon);
                }
            });
        }

    }

    private void findRestaurantsByCoordinate(double lat, double lon) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String gps_URL = "location=-" + lat + "," + lon + "&radius=1500&type=restaurant";
        String full_URL = restaurant_URL + gps_URL + "&key=" + R.string.Google_API_Key;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, full_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    address = ((JSONArray) response.get("results")).getJSONObject(0).getString("formatted_address");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String jsonError = new String(error.networkResponse.data);
                System.out.println(jsonError);
            }
        });
        queue.add(stringRequest);
    }
}