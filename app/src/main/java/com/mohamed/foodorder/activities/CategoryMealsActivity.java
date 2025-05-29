package com.mohamed.foodorder.activities;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed.foodorder.adapters.MealAdapter;
import com.mohamed.foodorder.databinding.ActivityCategoryMealsBinding;
import com.mohamed.foodorder.domain.models.Meal;
import java.util.ArrayList;
import java.util.List;

public class CategoryMealsActivity extends AppCompatActivity {
    private ActivityCategoryMealsBinding binding;
    private DatabaseReference db;
    private MealAdapter mealAdapter;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityCategoryMealsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseDatabase.getInstance().getReference();
        mealAdapter = new MealAdapter(MealAdapter.VIEW_TYPE_CATEGORY);
        binding.categoryMealsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.categoryMealsList.setAdapter(mealAdapter);

        category = getIntent().getStringExtra("category");
        if (category != null) {
            binding.tvCategoryTitle.setText(category + " Meals");
            loadCategoryMeals(category);
        } else {
            Toast.makeText(this, "Category not specified", Toast.LENGTH_SHORT).show();
            finish();
        }

        binding.ivBackArrow.setOnClickListener(v -> finish());
    }

    private void loadCategoryMeals(String category) {
        db.child("restaurants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Meal> meals = new ArrayList<>();
                for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {
                    DataSnapshot mealsSnapshot = restaurantSnapshot.child("meals");
                    for (DataSnapshot mealSnapshot : mealsSnapshot.getChildren()) {
                        Meal meal = mealSnapshot.getValue(Meal.class);
                        if (meal != null && category.equals(meal.getCategory())) {
                            meal.setId(mealSnapshot.getKey());
                            meals.add(meal);
                        }
                    }
                }
                mealAdapter.setMeals(meals);
                if (meals.isEmpty()) {
                    Toast.makeText(CategoryMealsActivity.this, "No meals found for " + category, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryMealsActivity.this, "Error loading meals: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}