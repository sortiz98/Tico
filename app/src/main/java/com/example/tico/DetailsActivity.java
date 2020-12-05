package com.example.tico;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
// import com.google.cloud.translate.*;


public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.tico.MESSAGE";

    // Details of a restaurant
    private String detailURL;
    private String photoURL;
    private String language;
    private Restaurant restaurant;

    private JSONObject results;
    private String address;
    private String miles;
    private String openNow;
    private String driveTime;
    private String name;
    private double distance;
    private int time;
    private double rating;

    private TextView restaurantName;
    private TextView driveTimeView;
    private TextView restaurantOpenNow;
    private TextView distanceView;
    private TextView statusView;
    private TextView languageTextView;
    private TextView whereText;
    private TextView photosText;
    private TextView reviewsText;
    private TextView rateItText;
    private TextView authText;
    private TextView authRateText;
    private TextView numRatingsText;
    private SeekBar bar;
    private ImageView restaurantPhoto;

    private static Bundle state;

    private Translator translator;

    private Button photoButton;


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


    // test detailURL: https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJaRrJa2Nx44kRPmjdbYFv-Ow&fields=name,rating,formatted_phone_number&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        try {
            restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        } catch (Exception e) {
            restaurant = (Restaurant) state.getSerializable("restaurant");
        }
        if (restaurant == null) {
            restaurant = (Restaurant) state.getSerializable("restaurant");
        }

        restaurantName = findViewById(R.id.name);
        statusView = findViewById(R.id.statusView);
        restaurantOpenNow = findViewById(R.id.openNow);
        distanceView = findViewById(R.id.distanceView);
        driveTimeView = findViewById(R.id.driveTimeView);
        restaurantPhoto = findViewById(R.id.imageView);
        languageTextView = findViewById(R.id.language);
        photosText = findViewById(R.id.photosText);
        reviewsText = findViewById(R.id.reviewsText);
        whereText = findViewById(R.id.whereText);
        authText = findViewById(R.id.authText);
        numRatingsText = findViewById(R.id.numRatingsText);
        rateItText  = findViewById(R.id.rateItText);
        authRateText = findViewById(R.id.authRateText);
        bar = findViewById(R.id.seekBarDetails);
        bar.setThumb(getResources().getDrawable(restaurant.getCuisineFlagRes(), null));

        photoButton = findViewById(R.id.photos);

        name = restaurant.getName();
        photoURL = restaurant.getPhotoURL();
        detailURL = restaurant.getDetailURL();
        language = restaurant.getLanguage();
        distance = restaurant.getDistance();
        time = (int) restaurant.getTime();
        //languageTextView.setText(language); // Can be deleted

        restaurantName.setText(name);
        Picasso.get().load(photoURL).into(restaurantPhoto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            }
        });


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

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, detailURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    results = response.getJSONObject("result");
                    //website = results.getString("website");
                    //restaurantWebsite.setText(website);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    results = response.getJSONObject("result");
                    address = results.getString("formatted_address");
                    openNow = results.getJSONObject("opening_hours").getBoolean("open_now") ? "Currently Open" : "Now Closed";
                    driveTime = String.format("%d minute drive", time);
                    miles  = String.format("%3.1f miles away",  distance);
                    rating = results.getDouble("rating");
                    // Can add more information to the map below
                    final Map<String, TextView> infoMap = new HashMap<String, TextView>() {{
                        put(openNow, statusView);
                        put(miles, distanceView);
                        put(driveTime, driveTimeView);
                        put("photos", photosText);
                        put("reviews", reviewsText);
                        put("where", whereText);
                        put((String) numRatingsText.getText(), numRatingsText);
                        put("authenticity", authText);
                        put("Rate it!", rateItText);
                        put("authentic?", authRateText);
                    }};
                    for (final String line: infoMap.keySet()) {
                        translator.translate(line).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                infoMap.get(line).setText(s);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.print("noo");
                            }
                        });
                    }
                    restaurantName.setText(name);
                    //restaurantAddress.setText(address);
                    //restaurantRating.setText(String.valueOf(rating));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    results = response.getJSONObject("result");
                    //website = results.getString("website");
                    //restaurantWebsite.setText(website);
                    openNow = results.getJSONObject("opening_hours").getBoolean("open_now") ? "open" : "closed";
                    translator.translate(openNow).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            //restaurantOpenNow.setText(s);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    results = response.getJSONObject("result");
                    rating = results.getDouble("rating");
                    //restaurantRating.setText(String.valueOf(rating));
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

    public void getMap(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.putExtra(EXTRA_MESSAGE, address);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle sstate) {
        super.onSaveInstanceState(sstate);

        // Save list state
        sstate.putSerializable("restaurant", restaurant);
        state.putSerializable("restaurant", restaurant);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            restaurant = (Restaurant) state.getSerializable("restaurant");
    }


    @Override
    protected void onPause()
    {
        super.onPause();

        state = new Bundle();
        state.putSerializable("restaurant", restaurant);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

}