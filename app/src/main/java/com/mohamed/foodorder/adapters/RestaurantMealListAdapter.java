package com.mohamed.foodorder.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.mohamed.foodorder.R;
import com.mohamed.foodorder.databinding.ViewHolderMealListBinding;
import com.mohamed.foodorder.domain.models.Meal;

import java.util.ArrayList;

public class RestaurantMealListAdapter extends RecyclerView.Adapter<RestaurantMealListAdapter.ViewHolder> {
    private final ArrayList<Meal> meals;
    private final OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public RestaurantMealListAdapter(ArrayList<Meal> meals, OnMealClickListener listener) {
        this.meals = meals;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_meal_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = meals.get(position);
        holder.binding.mealName.setText(meal.getName());
        holder.binding.mealPrice.setText(String.format("$%.2f", meal.getPrice()));
        holder.binding.mealRating.setText(String.format("%.1f stars", meal.getRating()));
        holder.binding.mealDeliveryTime.setText(String.format("%d min", meal.getDeliveryTime()));

        try {
            byte[] decodedBytes = Base64.decode(meal.getImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            Glide.with(holder.binding.getRoot().getContext()).load(bitmap).transform(new GranularRoundedCorners(30, 30, 30, 30)).into(holder.binding.mealImage);
        } catch (Exception e) {
            Glide.with(holder.binding.getRoot().getContext()).load(R.drawable.placeholder_image).transform(new GranularRoundedCorners(30, 30, 30, 30)).into(holder.binding.mealImage);
        }

        holder.itemView.setOnClickListener(v -> listener.onMealClick(meal));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolderMealListBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewHolderMealListBinding.bind(itemView);
        }
    }
}