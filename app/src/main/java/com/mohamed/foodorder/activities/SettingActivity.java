package com.mohamed.foodorder.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.google.firebase.auth.FirebaseAuth;
import com.mohamed.foodorder.databinding.ActivitySettingBinding;
import com.mohamed.foodorder.helper.SharedPreferenceHelper;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    private SharedPreferenceHelper helper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        helper = new SharedPreferenceHelper(SettingActivity.this);

        helper.loadProfileImage(binding.imgUser);

        binding.linearLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, LanguageActivity.class);
            startActivity(intent);
        });
        binding.linearHelp.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, HelpActivity.class);
            startActivity(intent);
        });
        binding.linearLogOut.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mAuth.signOut();
                        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        binding.constraintProfile.setOnClickListener(c -> {
            Intent intent = new Intent(SettingActivity.this, UserActivity.class);
            startActivity(intent);
        });


    }
}