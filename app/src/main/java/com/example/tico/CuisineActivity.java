package com.example.tico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CuisineActivity extends AppCompatActivity {

    Button chineseCuisine;
    Button mexicanCuisine;
    Button japaneseCuisine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine);

        chineseCuisine = findViewById(R.id.chinese);
        mexicanCuisine = findViewById(R.id.mexican);
        japaneseCuisine = findViewById(R.id.japanese);

        chineseCuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.putExtra("cuisine", "chinese");
                startActivity(startIntent);
            }
        });

        mexicanCuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.putExtra("cuisine", "mexican");
                startActivity(startIntent);
            }
        });

        japaneseCuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.putExtra("cuisine", "japanese");
                startActivity(startIntent);
            }
        });

    }
}