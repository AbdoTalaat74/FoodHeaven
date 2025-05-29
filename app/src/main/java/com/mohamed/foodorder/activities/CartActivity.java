package com.mohamed.foodorder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mohamed.foodorder.adapters.CartListAdapter;
import com.mohamed.foodorder.databinding.ActivityCartBinding;
import com.mohamed.foodorder.domain.models.CartItem;
import com.mohamed.foodorder.helper.ChangeNumberItemsListener;
import com.mohamed.foodorder.helper.ManagementCart;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartListAdapter cartListAdapter;
    private ManagementCart managementCart;
    private double tax;
    private double deliveryFee = 10.0;
    private double percentTax = 0.02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityCartBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        managementCart = new ManagementCart(this);
        setVariable();
        initList();
    }

    private void setVariable() {
        binding.back.setOnClickListener(v -> finish());
        binding.order.setOnClickListener(v -> {
            managementCart.placeOrder(tax, deliveryFee, (success, message) -> {
                if (success) {
                    initList();
                }
            });
        });
    }

    private void initList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.cartList.setLayoutManager(layoutManager);

        managementCart.getListCart(cartItems -> {
            cartListAdapter = new CartListAdapter(cartItems, this, new ChangeNumberItemsListener() {
                @Override
                public void onChanged() {
                    calculateCart();
                }
            });
            binding.cartList.setAdapter(cartListAdapter);

            if (cartItems.isEmpty()) {
                binding.emptyCart.setVisibility(View.VISIBLE);
                binding.scrollView.setVisibility(View.GONE);
            } else {
                binding.emptyCart.setVisibility(View.GONE);
                binding.scrollView.setVisibility(View.VISIBLE);
            }

            calculateCart();
        });
    }

    private void calculateCart() {
        managementCart.getListCart(cartItems -> {
            double itemTotal = 0.0;
            for (CartItem item : cartItems) {
                itemTotal += item.getPrice() * item.getQuantity();
            }
            tax = Math.round(itemTotal * percentTax * 100.0) / 100.0;
            double total = Math.round((itemTotal + tax + deliveryFee) * 100.0) / 100.0;
            binding.subTotal.setText(String.format("$%.2f", itemTotal));
            binding.totalTax.setText(String.format("$%.2f", tax));
            binding.delivery.setText(String.format("$%.2f", deliveryFee));
            binding.total.setText(String.format("$%.2f", total));
        });
    }
}