package com.mohamed.foodorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.mohamed.foodorder.adapters.RestaurantMealListAdapter;
import com.mohamed.foodorder.databinding.ActivityRestaurantHomeBinding;
import com.mohamed.foodorder.domain.models.Meal;
import java.util.ArrayList;

public class RestaurantHomeActivity extends AppCompatActivity {
    private ActivityRestaurantHomeBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityRestaurantHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setSupportActionBar(binding.toolbar);

        setupStatsCards();
        setupButtons();
        initRecyclerView();

        binding.homeMenu.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        binding.supportMenu.setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));
        binding.settingMenu.setOnClickListener(v -> startActivity(new Intent(this, SettingActivity.class)));
    }

    private void setupStatsCards() {
        String restaurantId = mAuth.getCurrentUser().getUid();
        db.child("restaurants").child(restaurantId).child("meals")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long mealCount = snapshot.getChildrenCount();
                        binding.totalMeals.setText(String.valueOf(mealCount));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(RestaurantHomeActivity.this,
                                "Failed to load stats: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupButtons() {
        binding.addMealButton.setOnClickListener(v -> {
            Intent intent = new Intent(RestaurantHomeActivity.this, AddMealActivity.class);
            startActivity(intent);
        });
    }

    private void initRecyclerView() {
        String restaurantId = mAuth.getCurrentUser().getUid();
        db.child("restaurants").child(restaurantId).child("meals")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Meal> meals = new ArrayList<>();
                        for (DataSnapshot mealSnapshot : snapshot.getChildren()) {
                            Meal meal = mealSnapshot.getValue(Meal.class);
                            if (meal != null) {
                                meal.setId(mealSnapshot.getKey());
                                meals.add(meal);
                            }
                        }

                        if (meals.isEmpty()) {
                            binding.mealsRecyclerView.setVisibility(View.GONE);
                        } else {
                            binding.mealsRecyclerView.setVisibility(View.VISIBLE);
                            binding.mealsRecyclerView.setLayoutManager(new LinearLayoutManager(RestaurantHomeActivity.this));
                            RestaurantMealListAdapter adapter = new RestaurantMealListAdapter(meals, meal -> {
                                Intent intent = new Intent(RestaurantHomeActivity.this, EditMealActivity.class);
                                intent.putExtra("MEAL_ID", meal.getId());
                                intent.putExtra("MEAL", meal);
                                startActivity(intent);
                            });
                            binding.mealsRecyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(RestaurantHomeActivity.this,
                                "Error loading meals: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initRecyclerView();
        setupStatsCards();
    }
}