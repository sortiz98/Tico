package com.example.tico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.squareup.picasso.Picasso;
// import com.google.cloud.translate.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class DetailsActivity extends AppCompatActivity {

    // Details of a restaurant
    private String detailURL;
    private String photoURL;
    private String language;
    private Restaurant restaurant;

    private JSONObject results;
    private String address;
    private String website;
    private String openNow;
    private String name;
    private double rating;

    private TextView restaurantName;
    private TextView restaurantAddress;
    private TextView restaurantOpenNow;
    private TextView restaurantWebsite;
    private TextView restaurantRating;
    private TextView languageTextView;

    private ImageView restaurantPhoto;

    private Translator translator;

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

        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        restaurantName = findViewById(R.id.name);
        restaurantAddress = findViewById(R.id.address);
        restaurantOpenNow = findViewById(R.id.openNow);
        restaurantWebsite = findViewById(R.id.website);
        restaurantRating = findViewById(R.id.rating);
        restaurantPhoto = findViewById(R.id.imageView);
        languageTextView = findViewById(R.id.language);

        name = restaurant.getName();
        photoURL = restaurant.getPhotoURL();
        detailURL = restaurant.getDetailURL();
        language = restaurant.getLanguage();
        languageTextView.setText(language); // Can be deleted

        Picasso.get().load(photoURL).into(restaurantPhoto);

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
                    address = results.getString("formatted_address");
                    website = results.getString("website");
                    openNow = results.getJSONObject("opening_hours").getBoolean("open_now") ? "open" : "closed";
                    rating = results.getDouble("rating");
                    // Can add more information to the map below
                    final Map<String, TextView> infoMap = new HashMap<String, TextView>() {{
//                        put(address, restaurantAddress);
                        put(openNow, restaurantOpenNow);
//                        put(name, restaurantName);
                    }};

                    for (final String info: infoMap.keySet()) {
                        translator.translate(info).addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                infoMap.get(info).setText(s);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    }
                    restaurantName.setText(name);
                    restaurantAddress.setText(address);
                    restaurantWebsite.setText(website);
                    restaurantRating.setText(String.valueOf(rating));
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