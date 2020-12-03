package com.example.tico;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    int count = 0;
    Button locationButton;
    Button sortDistanceButton;
    EditText locationEditText;
    String cuisine;
    String addressType;
    String language;
    Context context;
    double longitude, latitude;
    static String GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String restaurant_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    private String place_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    // Photo of restaurant (max-width of photo can be changed)
    private String photo_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&";
    // Text based search results
    private String SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/xml?query=restaurants+in+Sydney&key=YOUR_API_KEY";
    // Details of a restaurant
    private String detail_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private String distance_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=";
    private ViewSwitcher viewSwitcher;
    JSONArray results;
    List<Restaurant> restaurants;
    RestaurantAdapter adapter;
    RecyclerView recyclerView;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        recyclerView = findViewById(R.id.rvRestaurants);


            try {
                language = getIntent().getExtras().getString("language");
                cuisine = getIntent().getExtras().getString("cuisine");
                addressType = "currentLocation"; // Default to use current location
            } catch (Exception e) {
                /*recyclerView.setAdapter(new RestaurantAdapter(new ArrayList<Restaurant>(), context));
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            lm.onRestoreInstanceState(listState);*/
                language = mBundleRecyclerViewState.getString("lang");
                cuisine = mBundleRecyclerViewState.getString("cuisine");
                addressType = mBundleRecyclerViewState.getString("address");
            }


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            locationButton = findViewById(R.id.locationButton);
            sortDistanceButton = findViewById(R.id.sortDistanceButton);
            locationEditText = findViewById(R.id.locationEditText);
            viewSwitcher = findViewById(R.id.viewSwitcher);
            restaurants = new ArrayList<>();
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewSwitcher.showNext();
                }
            });

            locationEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if ((keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(locationEditText.getWindowToken(), 0);
                        addressType = locationEditText.getText().toString();
                        processInputLocation(addressType);
                    }
                    return false;
                }
            });


            sortDistanceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount()-1);
                    sortByDistance();
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(0);
                }
            });
        if (addressType.equals("currentLocation")) {
            processCurrentLocation();
        } else {
            processInputLocation(addressType);
        }


        sortDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByDistance();
                adapter.notifyDataSetChanged();
            }
        });
    }

    // https://maps.googleapis.com/maps/api/place/textsearch/json?query=chinese+restaurants&location=100,200&radius=1500&type=restaurant&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0
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

    // https://maps.googleapis.com/maps/api/geocode/json?address=2301durantAve&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0
    private void processInputLocation(String address) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String place_Url = place_URL + address.replace("\\s+", "") + "&key=" + getResources().getString(R.string.Google_API_Key);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, place_Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    latitude = ((JSONArray) response.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    longitude = ((JSONArray) response.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                    findRestaurantsByCoordinate(latitude, longitude);
                } catch (JSONException e) {
                    locationEditText.setText("Invalid Location");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    private void findRestaurantsByCoordinate(double lat, double lon) {
        RequestQueue queue = Volley.newRequestQueue(this);
        restaurants = new ArrayList<>();
        String gps_URL = "query=" + cuisine + "+restaurants&location=" + lat + "," + lon + "&radius=1500&type=restaurant";
        String full_URL = restaurant_URL + gps_URL + "&key=" + getResources().getString(R.string.Google_API_Key);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, full_URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    results = (JSONArray) response.get("results");
                    // Outputs at most five restaurants
                    for (int i = 0; i < Math.min(results.length(), 9); i++) {
                        JSONObject restaurantInfo = results.getJSONObject(i);

                        String name = restaurantInfo.getString("name");
                        String placeID = restaurantInfo.getString("place_id");

                        String detailURL = detail_URL + "place_id=" + placeID + "&key=" + getResources().getString(R.string.Google_API_Key);
                        String distanceURL = distance_URL + latitude + "," + longitude + "&destinations=place_id:" + placeID + "&key=" + getResources().getString(R.string.Google_API_Key);

                        // photo
                        JSONArray photos = restaurantInfo.getJSONArray("photos");
                        String photoReference = photos.getJSONObject(0).getString("photo_reference");
                        String photoURL = photo_URL + "photoreference=" + photoReference + "&key=" + getResources().getString(R.string.Google_API_Key);

                        restaurants.add(new Restaurant(name, language, placeID, photoURL, detailURL, distanceURL));
                    }
                    adapter = new RestaurantAdapter(restaurants, context);
                    recyclerView.setAdapter(adapter);

                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    recyclerView.smoothScrollToPosition(0);
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
        queue.add(request);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void sortByDistance() {
        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant restaurantOne, Restaurant restaurantTwo) {
                if (restaurantOne.getDistance() > restaurantTwo.getDistance()) return 1;
                else if (restaurantOne.getDistance() < restaurantTwo.getDistance()) return -1;
                else return 0;
            }
        });
    }

   /* @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        mListState = mLayoutManager.onSaveInstanceState();
        state.putParcelable(LIST_STATE_KEY, mListState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            mListState = state.getParcelable(LIST_STATE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }*/

    @Override
    protected void onPause()
    {
        super.onPause();

        // save RecyclerView state
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);
        mBundleRecyclerViewState.putString("lang", language);
        mBundleRecyclerViewState.putString("cuisine", cuisine);
        mBundleRecyclerViewState.putString("address", addressType);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}