package com.mohamed.foodorder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import com.mohamed.foodorder.databinding.ActivityHelpBinding;

public class HelpActivity extends AppCompatActivity {
    ActivityHelpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityHelpBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());


        binding.imgPhone.setOnClickListener(v -> phoneSupport());

        binding.imgEmail.setOnClickListener(v -> emailSupport());

        binding.imgCloseHelp.setOnClickListener(v -> finish());

    }

    private void phoneSupport() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+972 58-599-9683"));
        startActivity(intent);
    }

    private void emailSupport() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"));

        startActivity(intent);
    }


}