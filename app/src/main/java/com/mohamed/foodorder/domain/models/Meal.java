package com.mohamed.foodorder.domain.models;

public class Meal {
    private String name;
    private double price;
    private String description;
    private String imageDrawable;
    private String category;
    private String imageBase64;

    public Meal() {}

    public Meal(String name, double price, String description, String imageDrawable, String category, String imageBase64) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageDrawable = imageDrawable;
        this.category = category;
        this.imageBase64 = imageBase64;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageDrawable() { return imageDrawable; }
    public void setImageDrawable(String imageDrawable) { this.imageDrawable = imageDrawable; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}