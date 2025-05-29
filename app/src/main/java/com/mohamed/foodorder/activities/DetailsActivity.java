package com.mohamed.foodorder.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.mohamed.foodorder.R;
import com.mohamed.foodorder.databinding.ActivityDetailsBinding;
import com.mohamed.foodorder.domain.models.Meal;
import com.mohamed.foodorder.helper.ManagementCart;

public class DetailsActivity extends AppCompatActivity {
    private ActivityDetailsBinding binding;
    private Meal object;
    private int numberOrder = 1;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityDetailsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        managementCart = new ManagementCart(DetailsActivity.this);
        getBundle();
        binding.ivBackArrow.setOnClickListener(v -> {
            finish();
        });
    }

    private void getBundle() {
        object = (Meal) getIntent().getSerializableExtra("object");

        if (object.getImageBase64() != null && !object.getImageBase64().isEmpty()) {
            byte[] decodedBytes = Base64.decode(object.getImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            binding.ivItem.setImageBitmap(bitmap);
        } else {
            binding.ivItem.setImageResource(R.drawable.fast_1); // Fallback image
        }

        binding.itemName.setText(object.getName());
        binding.itemPrice.setText("$" + object.getPrice());
        binding.description.setText(object.getDescription());
        binding.quantity.setText(String.valueOf(numberOrder));
        binding.tvTime.setText(object.getDeliveryTime() + " min");
        binding.addToCart.setText("Add to cart - $" + Math.round(numberOrder * object.getPrice()));

        binding.plusQuantity.setOnClickListener(v -> {
            numberOrder = numberOrder + 1;
            binding.quantity.setText(String.valueOf(numberOrder));
            binding.addToCart.setText("Add to cart - $" + Math.round(numberOrder * object.getPrice()));
        });

        binding.minusQuantity.setOnClickListener(v -> {
            if (numberOrder > 1) {
                numberOrder = numberOrder - 1;
                binding.quantity.setText(String.valueOf(numberOrder));
                binding.addToCart.setText("Add to cart - $" + Math.round(numberOrder * object.getPrice()));
            }
        });

        binding.addToCart.setOnClickListener(v -> {
            object.setNumberInCart(numberOrder);
            managementCart.insertFood(object);
        });
    }
}