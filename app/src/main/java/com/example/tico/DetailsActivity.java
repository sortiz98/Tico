package com.example.tico;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.google.firebase.firestore.FirebaseFirestore;
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
    private FirebaseFirestore db;



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
    private TextView percentageText;
    private SeekBar bar;
    private ImageView restaurantPhoto;
    private Button rateButton;
    private PopupWindow popupWindow;

    private static Bundle state;

    private Translator translator;

    private Button photoButton;
    private Button reviewButton;

    private Button yesButton;
    private Button noButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        this.db = FirebaseFirestore.getInstance();


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
        rateButton = findViewById(R.id.rate);
        percentageText = findViewById(R.id.percentage);
        bar = findViewById(R.id.seekBarDetails);
        bar.setThumb(getResources().getDrawable(restaurant.getCuisineFlagRes(), null));

        bar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        updateRatings();

        photoButton = findViewById(R.id.photos);
        reviewButton = findViewById(R.id.reviews);

        yesButton = findViewById(R.id.yesButton);
        noButton = findViewById(R.id.noButton);

        name = restaurant.getName();
        photoURL = restaurant.getPhotoURL();
        detailURL = restaurant.getDetailURL();
        language = restaurant.getLanguage();
        distance = restaurant.getDistance();
        time = (int) restaurant.getTime();

        restaurantName.setText(name);
        if (photoURL.length() != 0)
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

        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReviewsActivity.class);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            }
        });

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yesButton.setBackground(getDrawable(R.drawable.yes_btn_pressed));
                noButton.setBackground(getDrawable(R.drawable.no_btn));
                restaurant.rateAuthentic();
                updateAuthenticity();
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noButton.setBackground(getDrawable(R.drawable.no_btn_pressed));
                yesButton.setBackground(getDrawable(R.drawable.yes_btn));
                restaurant.rateInauthentic();
                updateAuthenticity();
            }
        });

        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.rating_layout, null);
                popupWindow = new PopupWindow(popupView, 1100, 2200, true);
                TextView rateItTv = (TextView) popupView.findViewById(R.id.rateIt);
                TextView authenticTv = (TextView) popupView.findViewById(R.id.authentic);
                final Button yesPopupButton = (Button) popupView.findViewById(R.id.popupYesButton);
                final Button noPopupButton = (Button) popupView.findViewById(R.id.popupNoButton);
                Button closeButton = (Button) popupView.findViewById(R.id.closeButton);
                translate("Rate it!", rateItTv);
                translate("authentic?", authenticTv);
                yesPopupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restaurant.rateAuthentic();
                        updateAuthenticity();
                        yesPopupButton.setBackground(getDrawable(R.drawable.yes_btn_pressed));
                        noPopupButton.setBackground(getDrawable(R.drawable.no_btn));
                        yesButton.performClick();
                        //popupWindow.dismiss();
                    }
                });

                noPopupButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restaurant.rateInauthentic();
                        updateAuthenticity();
                        noPopupButton.setBackground(getDrawable(R.drawable.no_btn_pressed));
                        yesPopupButton.setBackground(getDrawable(R.drawable.yes_btn));
                        noButton.performClick();

                        //popupWindow.dismiss();
                    }
                });
                closeButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View popupView) {
                        popupWindow.dismiss();
                        rateItText.setVisibility(View.INVISIBLE);
                        authRateText.setVisibility(View.INVISIBLE);
                        yesButton.setVisibility(View.VISIBLE);
                        noButton.setVisibility(View.VISIBLE);
                        yesButton.setEnabled(true);
                        noButton.setEnabled(true);
                        rateButton.setEnabled(false);
                    }
                });
                popupWindow.showAtLocation(findViewById(R.id.rate), Gravity.TOP, 0, 0);
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
                    //rating = results.getDouble("rating");
                    // Can add more information to the map below
                    final Map<String, TextView> infoMap = new HashMap<String, TextView>() {{
                        put(openNow, statusView);
                        put(miles, distanceView);
                        put(driveTime, driveTimeView);
                        put("photos", photosText);
                        put("reviews", reviewsText);
                        put("where", whereText);
                        put(Integer.toString(restaurant.getTotalScore()) + " ratings", numRatingsText);
                        put(Integer.toString(restaurant.getScore()) + "%", percentageText);
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
                    //rating = results.getDouble("rating");
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

    public void updateAuthenticity() {
        if (restaurant.restaurantId == null) {
            restaurant.restaurantId = "google" + restaurant.id;
            Map<String, Object> storeMe = new HashMap<>();
            storeMe.put("name", restaurant.getName());
            storeMe.put("latitude", Double.toString(restaurant.lat));
            storeMe.put("longitude", Double.toString(restaurant.lng));
            storeMe.put("authScore", restaurant.getAuthScoreString());
            storeMe.put("totalScore", restaurant.getTotalScoreString());

            db.collection("restaurants").document(restaurant.getRestaurantId()).set(storeMe)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Details", "AuthScore successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Details", "Error updating document", e);
                        }
                    });
        } else {
            db.collection("restaurants").document(restaurant.getRestaurantId()).update("authScore", restaurant.getAuthScoreString(), "totalScore", restaurant.getTotalScoreString())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Details", "AuthScore successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Details", "Error updating document", e);
                        }
                    });
            updateRatings();
        }
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

    public void translate(final String line, final TextView view) {
        translator.translate(line).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                view.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.print("noo");
            }
        });
    }

    public void updateRatings() {
        numRatingsText.setText(Integer.toString(restaurant.getTotalScore()) + " ratings");
        percentageText.setText(Integer.toString(restaurant.getScore()) + "%");
        int rating = restaurant.getScore();
        bar.setProgress(rating);

        // Change color of seekbar progress according to rating
        if (rating >= 80) {
            //barColor = Color.parseColor("#72D74F");
            bar.setProgressDrawable(getDrawable(R.drawable.green_bar));
        } else if (rating >= 50) {
            bar.setProgressDrawable(getDrawable(R.drawable.yellow_bar));
            //barColor = Color.parseColor("#F5E135");
        }  else if (rating > 25)  {
            bar.setProgressDrawable(getDrawable(R.drawable.orange_bar));
            //barColor = Color.parseColor("#F6B831");
        } else {
            bar.setProgressDrawable(getDrawable(R.drawable.red_bar));
            //barColor = Color.parseColor("#FC1204");
        }
    }

}