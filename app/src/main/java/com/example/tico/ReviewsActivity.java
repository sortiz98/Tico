package com.example.tico;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReviewsActivity extends AppCompatActivity {
    String language;
    Context context;
    RecyclerView recyclerView;
    private Restaurant restaurant;
    ReviewAdapter adapter;
    private Translator translator;
    private static Bundle state;


    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static Bundle mBundleRecyclerViewState;

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
        setContentView(R.layout.activity_reviews);

        try {
            Intent i = getIntent();
            restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
            System.out.print(restaurant);
        } catch (Exception e) {
            restaurant = (Restaurant) savedInstanceState.getSerializable("restaurant");
        }
        if (restaurant == null) {
            restaurant = (Restaurant) savedInstanceState.getSerializable("restaurant");
        }


        context = this;
        recyclerView = findViewById(R.id.rvReviews);
        language = restaurant.getLanguage();

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.reviewsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new ReviewAdapter(restaurant.reviews, context, translator);
        recyclerView.setAdapter(adapter);

        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        recyclerView.smoothScrollToPosition(0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

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