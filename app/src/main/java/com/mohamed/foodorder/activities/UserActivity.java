package com.mohamed.foodorder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed.foodorder.databinding.ActivityUserBinding;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserActivity extends AppCompatActivity {
    private ActivityUserBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private Uri imageUri;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openImageChooser();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        binding.imageUser.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setSupportActionBar(binding.toolbarUserAccount);
        binding.imageCloseUserAccount.setOnClickListener(v -> finish());

        loadUserData();

        binding.imageEdit.setOnClickListener(v -> checkPermissionsAndOpenPicker());

        binding.buttonSave.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        binding.progressbarAccount.setVisibility(View.VISIBLE);
        db.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                binding.progressbarAccount.setVisibility(View.GONE);
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String location = snapshot.child("location").getValue(String.class);
                    String imageBase64 = snapshot.child("imageBase64").getValue(String.class);

                    if (name != null) {
                        String[] names = name.split(" ", 2);
                        binding.edFirstName.setText(names[0]);
                        if (names.length > 1) {
                            binding.edLastName.setText(names[1]);
                        }
                    }
                    if (email != null) {
                        binding.edEmail.setText(email);
                    }
                    if (location != null) {
                        binding.edLocation.setText(location);
                    }
                    if (imageBase64 != null && !imageBase64.isEmpty()) {
                        byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        binding.imageUser.setImageBitmap(bitmap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressbarAccount.setVisibility(View.GONE);
                Toast.makeText(UserActivity.this, "Error loading data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionsAndOpenPicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveUserData() {
        String firstName = binding.edFirstName.getText().toString().trim();
        String lastName = binding.edLastName.getText().toString().trim();
        String email = binding.edEmail.getText().toString().trim();
        String location = binding.edLocation.getText().toString().trim();
        String fullName = firstName + (lastName.isEmpty() ? "" : " " + lastName);

        if (firstName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "First Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference userRef = db.child("users").child(userId);
        binding.progressbarAccount.setVisibility(View.VISIBLE);

        // Handle image
        String imageBase64 = null;
        if (imageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] imageBytes = baos.toByteArray();
                imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                if (imageBase64.length() > 10 * 1024 * 1024) {
                    binding.progressbarAccount.setVisibility(View.GONE);
                    Toast.makeText(this, "Image too large", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (IOException e) {
                binding.progressbarAccount.setVisibility(View.GONE);
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        userRef.child("name").setValue(fullName);
        userRef.child("email").setValue(email);
        userRef.child("location").setValue(location.isEmpty() ? null : location);
        if (imageBase64 != null) {
            userRef.child("imageBase64").setValue(imageBase64);
        }

        binding.progressbarAccount.setVisibility(View.GONE);
        Toast.makeText(this, "User Data Updated Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}