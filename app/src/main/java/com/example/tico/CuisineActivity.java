package com.example.tico;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;

public class CuisineActivity extends AppCompatActivity {

    Button chineseCuisine;
    Button mexicanCuisine;
    Button japaneseCuisine;
    Button indianCuisine;
    Button koreanCuisine;
    PopupWindow popupWindow;
    Button languageSelector;
    String language = "English";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine);

        chineseCuisine = findViewById(R.id.chinese);
        mexicanCuisine = findViewById(R.id.mexican);
        japaneseCuisine = findViewById(R.id.japanese);
        indianCuisine = findViewById(R.id.indian);
        koreanCuisine = findViewById(R.id.korean);
        languageSelector = findViewById(R.id.languageSelector);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupWindow = new PopupWindow(this);
        popupWindow = new PopupWindow(inflater.inflate(R.layout.language_selector, null,false),300,350,true);

        languageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.language_selector, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, 300, 350, true);
                Spinner languageSpinner = (Spinner) popupView.findViewById(R.id.languageSpinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CuisineActivity.this, R.array.languages, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                languageSpinner.setAdapter(adapter);
                languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        language = (String) parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                popupWindow.showAtLocation(findViewById(R.id.languageSelector), Gravity.TOP, 0, 0);
            }
        });

        chineseCuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.putExtra("cuisine", "chinese");
                startIntent.putExtra("language", language);
                startActivity(startIntent);
            }
        });

        mexicanCuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.putExtra("cuisine", "mexican");
                startIntent.putExtra("language", language);
                startActivity(startIntent);
            }
        });

        japaneseCuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.putExtra("cuisine", "japanese");
                startIntent.putExtra("language", language);
                startActivity(startIntent);
            }
        });

        indianCuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.putExtra("cuisine", "indian");
                startIntent.putExtra("language", language);
                startActivity(startIntent);
            }
        });

        koreanCuisine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startIntent.putExtra("cuisine", "korean");
                startIntent.putExtra("language", language);
                startActivity(startIntent);
            }
        });

    }
}