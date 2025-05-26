package com.mohamed.foodorder.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.mohamed.foodorder.R;
import com.mohamed.foodorder.databinding.ActivityUserBinding;
import com.mohamed.foodorder.helper.SharedPreferenceHelper;

import java.io.IOException;

public class UserActivity extends AppCompatActivity {
    private ActivityUserBinding binding;
    private SharedPreferenceHelper helper;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleImageSelection(result.getData());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        helper = new SharedPreferenceHelper(this);


        binding.imageUser.setOnClickListener(v -> {
            openImageChooser();
        });

        helper.loadProfileImage(binding.imageUser);


        binding.buttonSave.setOnClickListener(v -> {
            if (!binding.edFirstName.getText().toString().isEmpty() && !binding.edEmail.getText().toString().isEmpty()) {
                helper.saveUserDataWithoutPassword(binding.edFirstName.getText().toString() + " " + binding.edLastName.getText().toString(), binding.edEmail.getText().toString());
                Toast.makeText(this, "User Data Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Fields Can't be empty", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("IntentReset")
    private void openImageChooser() {
        @SuppressLint("IntentReset") Intent imageChooser = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imageChooser.setType("image/*");
        imagePickerLauncher.launch(imageChooser);
    }

    private void handleImageSelection(Intent data) {
        Uri imageUri = data.getData();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);
            helper.saveImageToPreferences(bitmap);
            binding.imageUser.setImageBitmap(bitmap);
        } catch (IOException e) {
            Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) openImageChooser();
            });

    private void checkPermissionsAndOpenPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            openImageChooser();
        }
    }


}