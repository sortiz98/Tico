package com.example.tico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView test;
    TextView restaurantName;
    Button currentLocation;
    String cuisine;

    // Type conversion:     static String API_KEY = R.string.Google_API_Key;
    static String GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    String address = ""; // might not need
    private FusedLocationProviderClient fusedLocationProviderClient;

    // Nearby Search results
    private String restaurant_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";

    // Photo of restaurant (max-width of photo can be changed)
    private String photo_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&";

    // Text based search results
    private String SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/xml?query=restaurants+in+Sydney&key=YOUR_API_KEY";

    // Details of a restaurant
    private String detail_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

    String type = "currentLocation";
    String name;
    JSONArray results;

    // "https://maps.googleapis.com/maps/api/place/textsearch/json?query=chinese+restaurants&location=100,200&radius=1500&type=restaurant&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0"

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantName = findViewById(R.id.restaurant);
        imageView = findViewById(R.id.imageView);
        test = findViewById(R.id.test);
        currentLocation = findViewById(R.id.currentLocation);
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

    private void findRestaurantsByCoordinate(double lat, double lon) {
        RequestQueue queue = Volley.newRequestQueue(this);
        cuisine = getIntent().getExtras().getString("cuisine");
        String gps_URL = "query=" + cuisine + "+restaurants&location=" + lat + "," + lon + "&radius=1500&type=restaurant";
        String full_URL = restaurant_URL + gps_URL + "&key=" + getResources().getString(R.string.Google_API_Key);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, full_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    results = (JSONArray) response.get("results");
                    name = results.getJSONObject(0).getString("name");
                    String placeID = results.getJSONObject(0).getString("place_id");
                    String detailUrl = detail_URL + "place_id=" + placeID + "&fields=name,rating,formatted_phone_number&key=" + getResources().getString(R.string.Google_API_Key);

                    // photo
                    JSONArray photos = results.getJSONObject(0).getJSONArray("photos");
                    String photoReference = photos.getJSONObject(0).getString("photo_reference");
                    String photoUrl = photo_URL + "photoreference=" + photoReference + "&key=" + getResources().getString(R.string.Google_API_Key);
                    Picasso.get().load(photoUrl).into(imageView);

                    restaurantName.setText(name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String jsonError = new String(error.networkResponse.data);
                test.setText(jsonError);
            }
        });
        queue.add(stringRequest);
    }
}