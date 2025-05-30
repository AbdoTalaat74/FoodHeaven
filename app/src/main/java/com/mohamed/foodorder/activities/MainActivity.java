package com.mohamed.foodorder.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private MealAdapter mealAdapter;
    private List<Meal> allMeals;

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

        allMeals = new ArrayList<>();
        mealAdapter = new MealAdapter(MealAdapter.VIEW_TYPE_MAIN);
        binding.foodList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.foodList.setAdapter(mealAdapter);
        binding.foodList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.right = 16; // Add spacing between cards
            }
        });

        setupSearch();
        loadUserData();
        loadMeals();

        binding.pizzaCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryMealsActivity.class);
            intent.putExtra("category", "Pizza");
            startActivity(intent);
        });

        binding.burgerCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryMealsActivity.class);
            intent.putExtra("category", "Burger");
            startActivity(intent);
        });

        binding.chickenCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryMealsActivity.class);
            intent.putExtra("category", "Chicken");
            startActivity(intent);
        });

        binding.hotDogCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryMealsActivity.class);
            intent.putExtra("category", "HotDog");
            startActivity(intent);
        });

        binding.sushiCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryMealsActivity.class);
            intent.putExtra("category", "Sushi");
            startActivity(intent);
        });

        binding.meatCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryMealsActivity.class);
            intent.putExtra("category", "Meat");
            startActivity(intent);
        });

        binding.drinkCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryMealsActivity.class);
            intent.putExtra("category", "Drink");
            startActivity(intent);
        });

        binding.moreCategory.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryMealsActivity.class);
            intent.putExtra("category", "More");
            startActivity(intent);
        });

        binding.cartMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        binding.supportMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpActivity.class));
        });

        binding.settingMenu.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingActivity.class));
        });

    }

    private void setupSearch() {
        binding.textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterMeals(s.toString());
            }
        });
    }

    private void filterMeals(String query) {
        List<Meal> filteredMeals = new ArrayList<>();
        if (query.isEmpty()) {
            filteredMeals.addAll(allMeals);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Meal meal : allMeals) {
                if (meal.getName().toLowerCase().contains(lowerQuery)) {
                    filteredMeals.add(meal);
                }
            }
        }
        mealAdapter.setMeals(filteredMeals);
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
                allMeals.clear();
                for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {
                    DataSnapshot mealsSnapshot = restaurantSnapshot.child("meals");
                    for (DataSnapshot mealSnapshot : mealsSnapshot.getChildren()) {
                        Meal meal = mealSnapshot.getValue(Meal.class);
                        if (meal != null) {
                            meal.setId(mealSnapshot.getKey());
                            allMeals.add(meal);
                        }
                    }
                }
                mealAdapter.setMeals(allMeals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error loading meals: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMeals();
        loadUserData();
    }
}