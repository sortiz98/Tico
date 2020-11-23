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
//                        switch (position) {
////                            case 0: language = "English";
////                            case 1: language = "Chinese";
////                            case 2: language = "German";
////                            case 3: language = "French";
////                            case 4: language = "Spanish";
////                            case 5: language = "Japanese";
////                            case 6: language = "Korean";
////                            case 7: language = "Hindi";
//                        }
                        language = (String) parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Auto-generated method stub
                    }
                });

                popupWindow.showAtLocation(findViewById(R.id.languageSelector), Gravity.TOP, 0, 0);
            }
        });


//        class languageSelectorListener implements View.OnClickListener {
//            @Override
//            public void onClick(View v) {
//                languageSpinner = findViewById(R.id.spinner);
//
//                popupWindow.showAtLocation(findViewById(R.id.textView), Gravity.CENTER, 0, 0);
//                // Code to undo the user's last action
//            }
//        }


//        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), R.string.select_language, Snackbar.LENGTH_SHORT);
//        snackbar.setAction("Select Your Language", new languageSelectorListener());
//        snackbar.show();


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