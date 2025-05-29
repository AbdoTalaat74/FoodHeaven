package com.mohamed.foodorder.domain.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String mealId;
    private String name;
    private double price;
    private int quantity;
    private String imageBase64;
    private String description;
    private double rating;
    private int deliveryTime;
    private String category;

    public CartItem() {
    }

    public CartItem(Meal meal, int quantity) {
        this.mealId = meal.getId();
        this.name = meal.getName();
        this.price = meal.getPrice();
        this.quantity = quantity;
        this.imageBase64 = meal.getImageBase64();
        this.description = meal.getDescription();
        this.rating = meal.getRating();
        this.deliveryTime = meal.getDeliveryTime();
        this.category = meal.getCategory();
    }

    // Getters and Setters
    public String getMealId() {
        return mealId;
    }

    public void setMealId(String mealId) {
        this.mealId = mealId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}