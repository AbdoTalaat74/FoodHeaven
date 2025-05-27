package com.mohamed.foodorder.domain.models;

import java.io.Serializable;

public class Meal implements Serializable {
    private String id;
    private String name;
    private double price;
    private String description;
    private String imageBase64;
    private String imageDrawable;
    private String category;
    private double rating;
    private int deliveryTime;

    public Meal() {}

    public Meal(String id, String name, double price, String description, String imageBase64, String imageDrawable, String category, double rating, int deliveryTime) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageBase64 = imageBase64;
        this.imageDrawable = imageDrawable;
        this.category = category;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
    public String getImageDrawable() { return imageDrawable; }
    public void setImageDrawable(String imageDrawable) { this.imageDrawable = imageDrawable; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }
}