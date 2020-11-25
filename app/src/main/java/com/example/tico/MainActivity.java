package com.example.tico;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button locationButton;
    EditText locationEditText;
    String cuisine;
    String addressType;
    String language;
    double longitude, latitude;
    static String GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json";
//    String address = ""; // only needed if user manually inputs address
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String restaurant_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    private String place_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    // Photo of restaurant (max-width of photo can be changed)
    private String photo_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&";
    // Text based search results
    private String SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/xml?query=restaurants+in+Sydney&key=YOUR_API_KEY";
    // Details of a restaurant
    private String detail_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private ViewSwitcher viewSwitcher;
    JSONArray results;
    List<Restaurant> restaurants;
    RecyclerView recyclerView;

    // "https://maps.googleapis.com/maps/api/place/textsearch/json?query=chinese+restaurants&location=100,200&radius=1500&type=restaurant&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        language = getIntent().getExtras().getString("language");
        locationButton = findViewById(R.id.locationButton);
        locationEditText = findViewById(R.id.locationEditText);
        viewSwitcher = findViewById(R.id.viewSwitcher);
        restaurants = new ArrayList<>();
        addressType = "currentLocation"; // Default to use current location
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSwitcher.showNext();
            }
        });

        locationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(locationEditText.getWindowToken(), 0);
                    String address = locationEditText.getText().toString();
                    processInputLocation(address);
                    locationEditText.setText("clicked");
                }
                return false;
            }
        });

        if (addressType.equals("currentLocation")) processCurrentLocation();


        recyclerView = findViewById(R.id.rvRestaurants);
    }

    private void processCurrentLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                findRestaurantsByCoordinate(latitude, longitude);
            }
        });
    }

    private void processInputLocation(String address) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String place_Url = place_URL + address.replace("\\s+", "") + "&key=" + getResources().getString(R.string.Google_API_Key);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, place_Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    latitude = ((JSONArray) response.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    longitude = ((JSONArray) response.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lon");
                    findRestaurantsByCoordinate(latitude, longitude);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                locationEditText.setText("Invalid Location");
            }
        });
        queue.add(stringRequest);
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
                    // Outputs at most five restaurants
                    for (int i = 0; i < Math.min(results.length(), 5); i++) {
                        JSONObject restaurantInfo = results.getJSONObject(i);
                        String name = restaurantInfo.getString("name");
                        String placeID = restaurantInfo.getString("place_id");
                        String detailURL = detail_URL + "place_id=" + placeID + "&key=" + getResources().getString(R.string.Google_API_Key);
                        // photo
                        JSONArray photos = restaurantInfo.getJSONArray("photos");
                        String photoReference = photos.getJSONObject(0).getString("photo_reference");
                        String photoURL = photo_URL + "photoreference=" + photoReference + "&key=" + getResources().getString(R.string.Google_API_Key);
                        restaurants.add(new Restaurant(name, language, placeID, photoURL, detailURL));
                    }
                    RestaurantAdapter adapter = new RestaurantAdapter(restaurants);

                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String jsonError = new String(error.networkResponse.data);
            }
        });
        queue.add(stringRequest);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}