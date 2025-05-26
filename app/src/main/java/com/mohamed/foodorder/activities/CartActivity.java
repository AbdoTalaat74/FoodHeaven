package com.mohamed.foodorder.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.mohamed.foodorder.R;
import com.mohamed.foodorder.adapters.CartListAdapter;
import com.mohamed.foodorder.databinding.ActivityCartBinding;
import com.mohamed.foodorder.helper.ChangeNumberItemsListner;
import com.mohamed.foodorder.helper.ManagementCart;

public class CartActivity extends AppCompatActivity {

    private ActivityCartBinding binding;
    private CartListAdapter cartListAdapter;
    private ManagementCart managementCart;
    private double tax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        managementCart = new ManagementCart(this);
        initList();
        calculateCart();
        setVariable();

    }

    private void setVariable() {
        binding.back.setOnClickListener(v -> {
            finish();
        });
    }


    private void initList() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.cartList.setLayoutManager(linearLayoutManager);
        cartListAdapter = new CartListAdapter(managementCart.getListCart(), this, new ChangeNumberItemsListner() {
            @Override
            public void onChanged() {
                calculateCart();
            }
        });
        binding.cartList.setAdapter(cartListAdapter);
        if (managementCart.getListCart().isEmpty()) {
            binding.emptyCart.setVisibility(View.VISIBLE);
            binding.scrollView.setVisibility(View.GONE);
        }
        {
            binding.emptyCart.setVisibility(View.GONE);
            binding.emptyCart.setVisibility(View.VISIBLE);
        }
    }

    private void calculateCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round((managementCart.getFee() * percentTax * 100.0)) / 100;

        double total = Math.round((managementCart.getFee() + tax + delivery) * 100.0) / 100;
        double itemTotal = Math.round(managementCart.getFee() * 100.0) / 100.0;

        binding.subTotal.setText("$" + itemTotal);
        binding.totalTax.setText("$" + tax);
        binding.delivery.setText("$" + delivery);
        binding.total.setText("$" + total);
    }
}