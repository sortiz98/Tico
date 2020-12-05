package com.example.tico;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends AppCompatActivity {
    private Restaurant restaurant;
    private static Bundle state;
    private String detailURL;
    private JSONObject results;
    private String photo_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&";
    private List<String> photoURLs;
    Context context;

    PhotoAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        context = this;
        recyclerView = findViewById(R.id.rvPhotos);
        try {
            Intent i = getIntent();
            restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
            System.out.print(restaurant);
        } catch (Exception e) {
            restaurant = (Restaurant) state.getSerializable("restaurant");
        }
        if (restaurant == null) {
            restaurant = (Restaurant) state.getSerializable("restaurant");
        }
        detailURL = restaurant.getDetailURL();
        photoURLs = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, detailURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    results = response.getJSONObject("result");
                    JSONArray photos = results.getJSONArray("photos");
                    for (int index = 0; index < photos.length(); index++) {
                        String photoReference = photos.getJSONObject(index).getString("photo_reference");
                        String photoURL = photo_URL + "photoreference=" + photoReference + "&key=" + getResources().getString(R.string.Google_API_Key);
                        photoURLs.add(photoURL);
                    }
                    adapter = new PhotoAdapter(photoURLs, context);
                    recyclerView.setAdapter(adapter);

                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                    recyclerView.smoothScrollToPosition(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("ERROR");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}