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
import com.mohamed.foodorder.databinding.ActivityEditMealBinding;
import com.mohamed.foodorder.domain.models.Meal;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditMealActivity extends AppCompatActivity {
    private ActivityEditMealBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private Uri imageUri;
    private Meal meal;
    private String mealId;
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
        binding = ActivityEditMealBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        mealId = getIntent().getStringExtra("MEAL_ID");
        meal = (Meal) getIntent().getSerializableExtra("MEAL");

        if (meal == null || mealId == null) {
            Toast.makeText(this, "Invalid meal data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.mealName.setText(meal.getName());
        binding.mealPrice.setText(String.valueOf(meal.getPrice()));
        binding.mealDescription.setText(meal.getDescription());
        binding.mealRating.setText(String.valueOf(meal.getRating()));
        binding.mealDeliveryTime.setText(String.valueOf(meal.getDeliveryTime()));
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equals(meal.getCategory())) {
                binding.categorySpinner.setSelection(i);
                break;
            }
        }
        try {
            if (meal.getImageBase64() != null && !meal.getImageBase64().isEmpty()) {
                byte[] decodedBytes = Base64.decode(meal.getImageBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                binding.mealImagePreview.setImageBitmap(bitmap);
            } else {
                binding.mealImagePreview.setImageResource(R.drawable.placeholder_image);
            }
        } catch (Exception e) {
            binding.mealImagePreview.setImageResource(R.drawable.placeholder_image);
        }

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.meal_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.categorySpinner.setAdapter(categoryAdapter);

        binding.selectImageButton.setOnClickListener(v -> checkPermissionAndPickImage());

        binding.saveMealButton.setOnClickListener(v -> saveMeal());
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

    private void saveMeal() {
        String name = binding.mealName.getText().toString().trim();
        String price = binding.mealPrice.getText().toString().trim();
        String description = binding.mealDescription.getText().toString().trim();
        String rating = binding.mealRating.getText().toString().trim();
        String deliveryTime = binding.mealDeliveryTime.getText().toString().trim();
        String category = binding.categorySpinner.getSelectedItem().toString();
        String imageDrawable = imageDrawables[binding.categorySpinner.getSelectedItemPosition()];
        String restaurantId = mAuth.getCurrentUser().getUid();

        if (name.isEmpty() || price.isEmpty() || description.isEmpty() || rating.isEmpty() || deliveryTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
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

        String imageBase64 = meal.getImageBase64();
        if (imageUri != null) {
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
        }

        Meal updatedMeal = new Meal(mealId, name, priceValue, description, imageBase64, imageDrawable, category, ratingValue, deliveryTimeValue,100);
        db.child("restaurants").child(restaurantId).child("mealsList").child(mealId).setValue(updatedMeal)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Meal updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}