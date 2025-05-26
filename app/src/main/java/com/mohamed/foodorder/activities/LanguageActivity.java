package com.mohamed.foodorder.activities;

import static androidx.core.app.PendingIntentCompat.getActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.mohamed.foodorder.R;
import com.mohamed.foodorder.databinding.ActivityLanguageBinding;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity {
    private ActivityLanguageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        String currentLanguage = Locale.getDefault().getLanguage();
        binding.imgCloseLanguage.setOnClickListener(v -> {
            finish();
        });
        if ("en".equals(currentLanguage)) {
            changeToEnglish();
        } else if ("ar".equals(currentLanguage)) {
            changeToArabic();
        }

        binding.linearArabic.setOnClickListener(v -> {
            changeToArabic();
        });

        binding.linearEnglish.setOnClickListener(v -> {
            changeToEnglish();
        });
    }

    private void changeToArabic() {
        binding.imgArabic.setVisibility(View.VISIBLE);
        binding.imgEnglish.setVisibility(View.INVISIBLE);
    }

    private void changeToEnglish() {
        binding.imgArabic.setVisibility(View.INVISIBLE);
        binding.imgEnglish.setVisibility(View.VISIBLE);
    }

    private void changeLanguage(String code) {
        Intent intent = new Intent(LanguageActivity.this, SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        SharedPreferences sharedPref = LanguageActivity.this.getSharedPreferences("Language", Context.MODE_PRIVATE);

        if (code.equals("en")) {
            setLocal(LanguageActivity.this, "en");
            changeToEnglish();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("language", "en");
            editor.apply();
            startActivity(intent);
        } else if (code.equals("ar")) {
            setLocal(LanguageActivity.this, "ar");
            changeToArabic();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("language", "ar");
            editor.apply();
            startActivity(intent);
        }

    }


    private void setLocal(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);

        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;

        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }


}