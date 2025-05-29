package com.mohamed.foodorder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed.foodorder.R;
import com.mohamed.foodorder.databinding.ActivityAddMealBinding;
import com.mohamed.foodorder.domain.models.Meal;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.util.Base64;

public class AddMealActivity extends AppCompatActivity {
    private ActivityAddMealBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private Uri imageUri;
    private final String[] categories = {"Pizza", "Burger", "Chicken", "HotDog", "Sushi", "Meat", "Drink", "More"};
    private final String[] imageDrawables = {"btn_1", "btn_2", "btn_3", "btn_4", "btn_5", "btn_6", "btn_7", "btn_8"};

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchImagePicker();
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    binding.mealImagePreview.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityAddMealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.meal_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);

        binding.selectImageButton.setOnClickListener(v -> checkPermissionAndPickImage());

        binding.addMealButton.setOnClickListener(v -> addMeal());
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void addMeal() {
        String name = binding.mealName.getText().toString().trim();
        String price = binding.mealPrice.getText().toString().trim();
        String description = binding.mealDescription.getText().toString().trim();
        String rating = binding.mealRating.getText().toString().trim();
        String deliveryTime = binding.mealDeliveryTime.getText().toString().trim();
        String category = binding.categorySpinner.getSelectedItem().toString();
        String imageDrawable = imageDrawables[binding.categorySpinner.getSelectedItemPosition()];
        String restaurantId = mAuth.getCurrentUser().getUid();

        if (name.isEmpty() || price.isEmpty() || description.isEmpty() || rating.isEmpty() || deliveryTime.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        double ratingValue;
        int deliveryTimeValue;
        double priceValue;
        try {
            ratingValue = Double.parseDouble(rating);
            if (ratingValue < 1.0 || ratingValue > 5.0) {
                Toast.makeText(this, "Rating must be between 1.0 and 5.0", Toast.LENGTH_SHORT).show();
                return;
            }
            deliveryTimeValue = Integer.parseInt(deliveryTime);
            if (deliveryTimeValue <= 0) {
                Toast.makeText(this, "Delivery time must be positive", Toast.LENGTH_SHORT).show();
                return;
            }
            priceValue = Double.parseDouble(price);
            if (priceValue <= 0) {
                Toast.makeText(this, "Price must be positive", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageBase64;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            if (imageBase64.length() > 10 * 1024 * 1024) {
                Toast.makeText(this, "Image too large", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
            return;
        }

        String mealId = db.child("restaurants").child(restaurantId).child("meals").push().getKey();
        Meal meal = new Meal(mealId, name, priceValue, description, imageBase64, imageDrawable, category, ratingValue, deliveryTimeValue,100);
        db.child("restaurants").child(restaurantId).child("meals").child(mealId).setValue(meal)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Meal added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}