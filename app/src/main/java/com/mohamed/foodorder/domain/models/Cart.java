package com.mohamed.foodorder.domain.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cart implements Serializable {
    private String userId;
    private List<CartItem> meals;
    private double totalPrice;

    public Cart() {
        this.meals = new ArrayList<>();
        this.totalPrice = 0.0;
    }

    public Cart(String userId, List<CartItem> meals, double totalPrice) {
        this.userId = userId;
        this.meals = meals != null ? meals : new ArrayList<>();
        this.totalPrice = totalPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getMeals() {
        return meals;
    }

    public void setMeals(List<CartItem> meals) {
        this.meals = meals;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}