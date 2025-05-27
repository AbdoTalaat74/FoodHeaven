package com.mohamed.foodorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed.foodorder.adapters.FoodListAdapter;
import com.mohamed.foodorder.databinding.ActivityMainBinding;
import com.mohamed.foodorder.domain.FoodDomain;
import com.mohamed.foodorder.domain.models.Meal;
import com.mohamed.foodorder.helper.SharedPreferenceHelper;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SharedPreferenceHelper helper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        helper = new SharedPreferenceHelper(this);

        // Check if user is authenticated
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        helper.loadProfileImage(binding.userPic);
        initRecyclerViews();

        binding.homeMenu.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));
        binding.cartMenu.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        binding.supportMenu.setOnClickListener(v -> startActivity(new Intent(this, HelpActivity.class)));
        binding.settingMenu.setOnClickListener(v -> startActivity(new Intent(this, SettingActivity.class)));
    }

    private void initRecyclerViews() {
        ArrayList<FoodDomain> items = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("restaurants")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Toast.makeText(MainActivity.this, "No meals available", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DataSnapshot restaurantSnapshot : snapshot.getChildren()) {
                            DataSnapshot mealsSnapshot = restaurantSnapshot.child("meals");
                            for (DataSnapshot mealSnapshot : mealsSnapshot.getChildren()) {
                                Meal meal = mealSnapshot.getValue(Meal.class);
                                if (meal != null) {
                                    FoodDomain food = new FoodDomain(
                                            meal.getName(),
                                            meal.getDescription(),
                                            meal.getImageBase64(),
                                            meal.getPrice(),
                                            30,
                                            4.5,
                                            100
                                    );
                                    items.add(food);
                                }
                            }
                        }
                        if (items.isEmpty()) {
                            Toast.makeText(MainActivity.this, "No meals found", Toast.LENGTH_SHORT).show();
                        } else {
                            binding.foodList.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            FoodListAdapter adapter = new FoodListAdapter(items);
                            binding.foodList.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Error loading meals: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}