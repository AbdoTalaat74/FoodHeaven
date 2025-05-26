package com.mohamed.foodorder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;


import com.mohamed.foodorder.adapters.FoodListAdapter;
import com.mohamed.foodorder.databinding.ActivityMainBinding;
import com.mohamed.foodorder.domain.FoodDomain;
import com.mohamed.foodorder.helper.SharedPreferenceHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SharedPreferenceHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        initRecyclerViews();

        helper = new SharedPreferenceHelper(this);
        helper.loadProfileImage(binding.userPic);


        binding.homeMenu.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        });

        binding.cartMenu.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });

        binding.supportMenu.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
        });

        binding.settingMenu.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        });
    }

    private void initRecyclerViews() {
        ArrayList<FoodDomain> items = new ArrayList<>();
        items.add(new FoodDomain("Cheese Burger", "Satisfy your cravings with our juicy Cheese Burger. " + "Made with 100% Angus beef patty and topped with melted cheddar cheese. fresh lettuce, tomato, and" + "our secret sauce, this classic burger will leave you waiting more. Served with crispy fries and a drink," + "it's a perfect meal for any occasion.", "fast_1", 15, 20, 4, 120));
        items.add(new FoodDomain("Pizza Peperoni", "Get a taste of Italy with our delicious Peperoni Pizza. Made with freshly rolled dough, zesty tomato and " + "spicy pepperoni, this pizza is sure to be a crowd pleasure. Perfectly baked in a wood-fired oven, it's the perfect choice for a quick launch or a family dinner", "fast_2", 20, 30, 5, 200));
        items.add(new FoodDomain("Vegetables Pizza", "Looking for a healthier option? Try our Vegetables Pizza made with a variety  of fresh vegetables. " + "Perfectly baked in a wood-fired oven, it's the perfect choice for a quick launch or a family dinner    ", "fast_3", 16, 30, 4.5, 100));

        binding.foodList.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        FoodListAdapter adapter = new FoodListAdapter(items);
        binding.foodList.setAdapter(adapter);
    }
}