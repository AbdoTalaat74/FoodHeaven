package com.mohamed.foodorder.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;

import com.bumptech.glide.Glide;
import com.mohamed.foodorder.databinding.ActivityDetailsBinding;
import com.mohamed.foodorder.domain.FoodDomain;
import com.mohamed.foodorder.helper.ManagementCart;

public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    private FoodDomain object;
    private int numberOrder = 1;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        managementCart = new ManagementCart(DetailsActivity.this);
        getBundle();
        binding.ivBackArrow.setOnClickListener(v -> {
            finish();
        });

    }

    private void getBundle() {
        object = (FoodDomain) getIntent().getSerializableExtra("object");

        int drawableResourceId = this.getResources().getIdentifier(object.getPicUrl(), "drawable", this.getPackageName());
        Glide.with(this)
                .load(drawableResourceId)
                .into(binding.ivItem);

        binding.itemName.setText(object.getTitle());
        binding.itemPrice.setText("$" + object.getPrice());
        binding.description.setText(object.getDescription());
        binding.quantity.setText(String.valueOf(numberOrder));
        binding.tvTime.setText(object.getTime() + " min");
        binding.addToCart.setText("Add to cart - $" + Math.round(numberOrder * object.getPrice()));

        binding.plusQuantity.setOnClickListener(v -> {
            numberOrder = numberOrder + 1;
            binding.quantity.setText(String.valueOf(numberOrder));
            binding.addToCart.setText("Add to cart - $" + Math.round(numberOrder * object.getPrice()));
        });

        binding.minusQuantity.setOnClickListener(v -> {
            numberOrder = numberOrder - 1;
            binding.quantity.setText(String.valueOf(numberOrder));
            binding.addToCart.setText("Add to cart - $" + Math.round(numberOrder * object.getPrice()));
        });

        binding.addToCart.setOnClickListener(v -> {
            object.setNumberInCart(numberOrder);
            managementCart.insertFood(object);
        });

    }
}