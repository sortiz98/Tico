package com.example.tico;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    // Details of a restaurant
    private String detail_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private Restaurant restaurant;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        textView = findViewById(R.id.textView);
        textView.setText(restaurant.name);
    }
}