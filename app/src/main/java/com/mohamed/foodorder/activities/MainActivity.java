package com.mohamed.foodorder.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed.foodorder.adapters.MealAdapter;
import com.mohamed.foodorder.databinding.ActivityMainBinding;
import com.mohamed.foodorder.domain.models.Meal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private MealAdapter mealAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        mealAdapter = new MealAdapter();
        binding.foodList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.foodList.setAdapter(mealAdapter);

        loadUserData();
        loadMeals();

        binding.cartMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        binding.supportMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpActivity.class));
        });

        binding.settingMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingActivity.class));
        });

        // TODO: Implement search for binding.textView
        // TODO: Implement click listener for binding.tvOrder
    }

    private void loadUserData() {
        String userId = mAuth.getCurrentUser().getUid();
        db.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String location = snapshot.child("location").getValue(String.class);
                    String imageBase64 = snapshot.child("imageBase64").getValue(String.class);

                    if (location != null) {
                        binding.location.setText(location);
                    } else {
                        binding.location.setText("Set Location");
                    }
                    if (imageBase64 != null && !imageBase64.isEmpty()) {
                        byte[] decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                        binding.userPic.setImageBitmap(bitmap);
                    }
                } else {
                    binding.location.setText("Set Location");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error loading data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                binding.location.setText("Set Location");
            }
        });
    }

    private void loadMeals() {
        db.child("restaurants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Meal> meals = new ArrayList<>();
                for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {
                    DataSnapshot mealsSnapshot = restaurantSnapshot.child("meals");
                    for (DataSnapshot mealSnapshot : mealsSnapshot.getChildren()) {
                        Meal meal = mealSnapshot.getValue(Meal.class);
                        if (meal != null) {
                            meal.setId(mealSnapshot.getKey());
                            meals.add(meal);
                        }
                    }
                }
                mealAdapter.setMeals(meals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error loading meals: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}