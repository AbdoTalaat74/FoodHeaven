package com.mohamed.foodorder.domain;

public class FoodDomain {
    private String title;
    private String description;
    private String picUrl; // Holds Base64 image string
    private double price;
    private int time;
    private double star;
    private int calories;
    private int numberInCart;

    public FoodDomain(String title, String description, String picUrl, double price, int time, double star, int calories) {
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.price = price;
        this.time = time;
        this.star = star;
        this.calories = calories;
        this.numberInCart = 1; // Default quantity
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPicUrl() { return picUrl; }
    public void setPicUrl(String picUrl) { this.picUrl = picUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public double getStar() { return star; }
    public void setStar(double star) { this.star = star; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public int getNumberInCart() { return numberInCart; }
    public void setNumberInCart(int numberInCart) { this.numberInCart = numberInCart; }
}