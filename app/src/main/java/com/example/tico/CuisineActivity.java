package com.example.tico;

import androidx.annotation.NonNull;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.HashMap;
import java.util.Map;

public class CuisineActivity extends AppCompatActivity {

    Button chineseCuisine;
    Button mexicanCuisine;
    Button japaneseCuisine;
    Button indianCuisine;
    PopupWindow popupWindow;
    Button languageSelector;
    static String language;
    TextView japaneseLabel;
    TextView chineseLabel;
    TextView mexicanLabel;
    TextView indianLabel;
    TextView slogan;
    TextView cuisineLabel;
    static Translator translator;

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
        setContentView(R.layout.activity_cuisine);

        chineseCuisine = findViewById(R.id.chinese);
        mexicanCuisine = findViewById(R.id.mexican);
        japaneseCuisine = findViewById(R.id.japanese);
        indianCuisine = findViewById(R.id.indian);
        languageSelector = findViewById(R.id.languageSelector);
        japaneseLabel = findViewById(R.id.japaneseLabel);
        chineseLabel = findViewById(R.id.chineseLabel);
        indianLabel = findViewById(R.id.indianLabel);
        mexicanLabel = findViewById(R.id.mexicanLabel);
        cuisineLabel = findViewById(R.id.cuisineLabel);
        slogan = findViewById(R.id.slogan);

        if (language == null || translator == null) {
            language = "English";
        }

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //popupWindow = new PopupWindow(this);
        //popupWindow = new PopupWindow(inflater.inflate(R.layout.language_selector, null,false),600,350,true);

        languageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.language_selector, null);
                popupWindow = new PopupWindow(popupView, 1100, 2200, true);
                Spinner languageSpinner = (Spinner) popupView.findViewById(R.id.languageSpinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(CuisineActivity.this, R.array.languages, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                languageSpinner.setAdapter(adapter);
                languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        language = (String) parent.getItemAtPosition(position);
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
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                popupWindow.showAtLocation(findViewById(R.id.languageSelector), Gravity.TOP, 0, 0);
                Button close = (Button) popupView.findViewById(R.id.continueButton);
                close.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View popupView) {
                        translate("authentic meals that taste like home", slogan);
                        translate("Cuisines", cuisineLabel);
                        translate("Japanese", japaneseLabel);
                        translate("Mexican", mexicanLabel);
                        translate("Indian", indianLabel);
                        translate("Chinese", chineseLabel);
                        popupWindow.dismiss();
                    }
                });
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
}