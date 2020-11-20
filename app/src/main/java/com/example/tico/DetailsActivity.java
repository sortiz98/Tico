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

    HashMap<String, String> languageMap = new HashMap() {{
        put("English", TranslateLanguage.ENGLISH);
        put("Chinese", TranslateLanguage.CHINESE);
        put("German", TranslateLanguage.GERMAN);
        put("French", TranslateLanguage.FRENCH);
        put("Spanish", TranslateLanguage.SPANISH);
        put("Japanese", TranslateLanguage.JAPANESE);
        put("Korean", TranslateLanguage.KOREAN);
    }};
    // test detailURL: https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJaRrJa2Nx44kRPmjdbYFv-Ow&fields=name,rating,formatted_phone_number&key=AIzaSyDugNQO9vZxbi68BQnReZCd_CeM-cg-WW0

//    Translate translate = TranslateOptions.getDefaultInstance().getService();

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
        languageTextView.setText(language);
        Picasso.get().load(photoURL).into(restaurantPhoto);


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
                    String translateLanguage = languageMap.get(language);
                    TranslatorOptions options = new TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.ENGLISH)
                            .setTargetLanguage(translateLanguage)
                            .build();
                    Translator translator = Translation.getClient(options);
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
                    translator.translate(address)
                            .addOnSuccessListener(
                                    new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            restaurantAddress.setText(s);;
                                        }
                                    }
                            ).addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            }
                    );
                    translator.translate(openNow)
                            .addOnSuccessListener(
                                    new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            restaurantOpenNow.setText(s);;
                                        }
                                    }
                            ).addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            }
                    );

                    translator.translate(name)
                            .addOnSuccessListener(
                                    new OnSuccessListener<String>() {
                                        @Override
                                        public void onSuccess(String s) {
                                            restaurantName.setText(s);;
                                        }
                                    }
                            ).addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            }
                    );

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