package com.example.tico;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    int count = 0;
    Button locationButton;
    Button sortDistanceButton;
    Button sortAuthenticityButton;
    EditText locationEditText;
    String cuisine;
    String addressType;
    String language;
    Context context;
    double longitude, latitude;
    static String GEO_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static boolean distanceSort = true;
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
    private TextView dummyText;
    private TextView addressView;
    JSONArray results;
    List<Restaurant> restaurants;
    RestaurantAdapter adapter;
    static RecyclerView recyclerView;

    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;
    private Translator translator;
    private FirebaseFirestore db;


    Map<String, String> languageMap = new HashMap<String, String>() {{
        put("English", TranslateLanguage.ENGLISH);
        put("中文", TranslateLanguage.CHINESE);
        put("Deutsch", TranslateLanguage.GERMAN);
        put("Français", TranslateLanguage.FRENCH);
        put("Español", TranslateLanguage.SPANISH);
        put("日本語", TranslateLanguage.JAPANESE);
        put("한국어", TranslateLanguage.KOREAN);
        put("हिन्दी", TranslateLanguage.HINDI);
    }};

    Map<String, Integer> flagMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        recyclerView = findViewById(R.id.rvRestaurants);
        dummyText =  findViewById(R.id.dummyText);
        addressView = findViewById(R.id.addressView);
        this.db = FirebaseFirestore.getInstance();


        flagMap = new HashMap<String, Integer>() {{
            put("chinese", R.drawable.chinese_flag);
            put("japanese", R.drawable.japanese_flag);
            put("indian", R.drawable.indian_flag);
            put("mexican", R.drawable.mexican_flag);
        }};

            try {
                language = getIntent().getExtras().getString("language");
                cuisine = getIntent().getExtras().getString("cuisine");
                addressType = "currentLocation"; // Default to use current location
            } catch (Exception e) {
                language = mBundleRecyclerViewState.getString("lang");
                cuisine = mBundleRecyclerViewState.getString("cuisine");
                addressType = mBundleRecyclerViewState.getString("address");
                addressView.setText(addressType.split(",")[0]);
                dummyText.setText(addressType.split(",")[0]);
            }


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            locationButton = findViewById(R.id.locationButton);
            sortDistanceButton = findViewById(R.id.sortDistanceButton);
            sortAuthenticityButton = findViewById(R.id.sortAuthenticityButton);
            locationEditText = findViewById(R.id.locationEditText);
            viewSwitcher = findViewById(R.id.viewSwitcher);
            translate();
            restaurants = new ArrayList<>();
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    locationButton.setEnabled(false);
                    locationButton.setVisibility(View.INVISIBLE);
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
                        String simpleAddress = addressType.split(",")[0];
                        addressView.setText(simpleAddress);
                        dummyText.setText(simpleAddress);
                        viewSwitcher.showPrevious();
                        locationButton.setEnabled(true);
                        locationButton.setVisibility(View.VISIBLE);
                        //sortDistanceButton.setEnabled(false);
                        //sortAuthenticityButton.setEnabled(false);
                        processInputLocation(addressType);
                    }
                    return false;
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
        setSortListeners();
    }

    public void translate(){
        String translateLanguage = languageMap.get(language);
        TranslatorOptions options = new TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(translateLanguage)
                .build();
        translator = Translation.getClient(options);
        getLifecycle().addObserver(translator);
        DownloadConditions conditions = new DownloadConditions.Builder().requireWifi().build();
        translator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }
                ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

        translator.translate(String.format("%s meals near", cuisine)).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                ((TextView) findViewById(R.id.mealText)).setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        if (addressType == "currentLocation") {
            translator.translate("Current Location").addOnSuccessListener(new OnSuccessListener<String>() {
                @Override
                public void onSuccess(String s) {
                    addressView.setText(s);
                    dummyText.setText(s);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
        translator.translate("Authenticity").addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                sortAuthenticityButton.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        translator.translate("Distance").addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                sortDistanceButton.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setSortListeners() {
        final Button distanceButton = findViewById(R.id.sortDistanceButton);
        final Button authButton = findViewById(R.id.sortAuthenticityButton);
        final ImageView distSelect = findViewById(R.id.distance_underline);
        final ImageView authSelect = findViewById(R.id.auth_underline);

        distanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distanceButton.setTextColor(Color.parseColor("#06A3BB"));
                authButton.setTextColor(Color.parseColor("#8C8C8C"));
                distSelect.setImageResource(R.drawable.distance_underline);
                authSelect.setImageResource(R.color.colorAccent);
                sortByDistance();
                adapter.notifyDataSetChanged();
                distanceSort = true;
            }
        });
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distanceButton.setTextColor(Color.parseColor("#8C8C8C"));
                authButton.setTextColor(Color.parseColor("#06A3BB"));
                distSelect.setImageResource(R.color.colorAccent);
                authSelect.setImageResource(R.drawable.authenticity_underline);
                sortByAuthenticity();
                adapter.notifyDataSetChanged();
                distanceSort = false;
            }
        });

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
        final String full_URL = restaurant_URL + gps_URL + "&key=" + getResources().getString(R.string.Google_API_Key) + "&fields=name,place_id,photos,geometry";
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
                        String photoURL = "";
                        String detailURL = detail_URL + "place_id=" + placeID + "&key=" + getResources().getString(R.string.Google_API_Key);
                        String distanceURL = distance_URL + latitude + "," + longitude + "&destinations=place_id:" + placeID + "&key=" + getResources().getString(R.string.Google_API_Key);

                        // photo
                        if (restaurantInfo.has("photos")) {
                            JSONArray photos = restaurantInfo.getJSONArray("photos");
                            String photoReference = photos.getJSONObject(0).getString("photo_reference");
                            photoURL = photo_URL + "photoreference=" + photoReference + "&key=" + getResources().getString(R.string.Google_API_Key);
                        }
                        double lat = restaurantInfo.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        double lon = restaurantInfo.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                        Restaurant restaurant = new Restaurant(name, language, placeID, photoURL, detailURL, distanceURL, flagMap.get(cuisine), lat, lon);
                        getRestaurantScore(restaurant, lat, lon, i);
                        new RestaurantHelper(restaurant).setDistance();
                        restaurants.add(restaurant);
                    }
                    adapter = new RestaurantAdapter(restaurants, context, translator, cuisine);
                    recyclerView.setAdapter(adapter);
                    //recyclerView.smoothScrollToPosition(8);

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

    public void getRestaurantScore(final Restaurant restaurant, final Double lat, final Double lon, final int position) {

        final CollectionReference restaurantRef = db.collection("restaurants");
        final String name = restaurant.getName();
        Query query = restaurantRef.whereEqualTo("name", name).whereEqualTo("latitude", lat.toString()).whereEqualTo("longitude", lon.toString());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        restaurant.setInfo(document.getId(), document.getData());
                        getReviews(restaurantRef.document(document.getId()).collection("reviews"), restaurant, position);
                    }
                } else {
                    Query query2 = restaurantRef.whereEqualTo("name", name);
                    query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                String bestMatchId = "";
                                double bestDistSquare = 100000000;
                                Map<String, Object> info = null;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    double latitude = Double.parseDouble((String) document.getData().get("latitude"));
                                    double longitude = Double.parseDouble((String) document.getData().get("longitude"));
                                    double squareDist = Math.abs(latitude - lat) * Math.abs(latitude - lat) + Math.abs(longitude - lon) * Math.abs(longitude - lon);
                                    if (squareDist < bestDistSquare) {
                                        bestDistSquare = squareDist;
                                        bestMatchId = document.getId();
                                        info = document.getData();
                                    }
                                }
                                restaurant.setInfo(bestMatchId, info);
                                getReviews(restaurantRef.document(bestMatchId).collection("reviews"), restaurant, position);
                            } else {
                                Log.d("TICO", "Error getting reviews: ", task.getException());

                            }
                        }
                    });
                }
            }
        });
    }

    public void getReviews(CollectionReference reviewRef, final Restaurant restaurant, final int position) {
        reviewRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                restaurant.addReview(Integer.parseInt((String) document.getData().get("Authenticity")), (String) document.getData().get("text"));
                            }
                        }
                        restaurant.refreshScore();
                        if (adapter != null) {
                            if (!distanceSort) {
                                sortByAuthenticity();
                            }
                            adapter.notifyDataSetChanged();;
                        }
                    }
                });
    }

    public void sortByDistance() {
        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant restaurantOne, Restaurant restaurantTwo) {
                if (restaurantOne.getDistance() > restaurantTwo.getDistance()) return 1;
                else if (restaurantOne.getDistance() < restaurantTwo.getDistance()) return -1;
                else return 0;
            }
        });
    }

    public void sortByAuthenticity() {
        Collections.sort(restaurants, new Comparator<Restaurant>() {
            @Override
            public int compare(Restaurant restaurantOne, Restaurant restaurantTwo) {
                if (restaurantOne.getScore() < restaurantTwo.getScore()) return 1;
                else if (restaurantOne.getScore() > restaurantTwo.getScore()) return -1;
                else return 0;
            }
        });
    }

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

    private class RestaurantHelper {
        private Restaurant restaurant;
        public RestaurantHelper(Restaurant restaurant) {
            this.restaurant = restaurant;
        }
        public void setDistance() {
            RequestQueue queue = Volley.newRequestQueue(context);
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, restaurant.getDistanceURL(), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject distanceInformation = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                        String distance = distanceInformation.getJSONObject("distance").getString("text");
                        String time = distanceInformation.getJSONObject("duration").getString("text");
                        restaurant.setDistance(Double.valueOf(distance.split("\\s")[0]));
                        restaurant.setTime(Double.valueOf(time.split("\\s")[0]));
                        if (adapter != null) {
                            if (distanceSort) {
                                sortByDistance();
                            } else {
                                sortByAuthenticity();
                            }
                            adapter.notifyDataSetChanged();
                        }
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
        }
    }
}