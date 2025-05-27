package com.mohamed.foodorder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mohamed.foodorder.R;
import com.mohamed.foodorder.domain.models.Meal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RestaurantHomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private EditText mealName, mealPrice, mealDescription;
    private Spinner categorySpinner;
    private ImageView mealImagePreview;
    private MaterialButton selectImageButton, addMealButton;
    private Uri imageUri;
    private final String[] categories = {"Pizza", "Burger", "Chicken", "HotDog", "Sushi", "Meat", "Drink", "More"};
    private final String[] imageDrawables = {"btn_1", "btn_2", "btn_3", "btn_4", "btn_5", "btn_6", "btn_7", "btn_8"};

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    launchImagePicker();
                } else {
                    Toast.makeText(this, "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    mealImagePreview.setImageURI(imageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        mealName = findViewById(R.id.meal_name);
        mealPrice = findViewById(R.id.meal_price);
        mealDescription = findViewById(R.id.meal_description);
        categorySpinner = findViewById(R.id.category_spinner);
        mealImagePreview = findViewById(R.id.meal_image_preview);
        selectImageButton = findViewById(R.id.select_image_button);
        addMealButton = findViewById(R.id.add_meal_button);

        // Setup category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.meal_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        // Image selection
        selectImageButton.setOnClickListener(v -> checkPermissionAndPickImage());

        // Add meal
        addMealButton.setOnClickListener(v -> addMeal());
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+: Request READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            // Pre-Android 13: Request READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
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
        String name = mealName.getText().toString().trim();
        String price = mealPrice.getText().toString().trim();
        String description = mealDescription.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String imageDrawable = imageDrawables[categorySpinner.getSelectedItemPosition()];
        String restaurantId = mAuth.getCurrentUser().getUid();

        if (name.isEmpty() || price.isEmpty() || description.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert image to Base64
        String imageBase64;
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            // Compress image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // 50% quality
            byte[] imageBytes = baos.toByteArray();
            imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            // Check size (must be < 10 MB)
            if (imageBase64.length() > 10 * 1024 * 1024) {
                Toast.makeText(this, "Image too large. Please select a smaller image.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error processing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to database
        String mealId = db.child("restaurants").child(restaurantId).child("meals").push().getKey();
        Meal meal = new Meal(name, Double.parseDouble(price), description, imageDrawable, category, imageBase64);
        db.child("restaurants").child(restaurantId).child("meals").child(mealId).setValue(meal)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Meal added successfully", Toast.LENGTH_SHORT).show();
                    mealName.setText("");
                    mealPrice.setText("");
                    mealDescription.setText("");
                    categorySpinner.setSelection(0);
                    imageUri = null;
                    mealImagePreview.setImageResource(R.drawable.placeholder_image);
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error adding meal: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}