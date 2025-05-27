package com.mohamed.foodorder.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mohamed.foodorder.R;
import com.mohamed.foodorder.domain.models.Meal;

import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private List<Meal> mealList = new ArrayList<>();

    public void setMeals(List<Meal> meals) {
        this.mealList = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_food_list, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.title.setText(meal.getName());
        holder.price.setText(String.format("$%.2f", meal.getPrice()));
        holder.score.setText(String.format("%.1f", meal.getRating()));
        holder.time.setText(meal.getDeliveryTime() + " min");

        if (meal.getImageBase64() != null && !meal.getImageBase64().isEmpty()) {
            byte[] decodedBytes = Base64.decode(meal.getImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.image.setImageBitmap(bitmap);
        } else {
            holder.image.setImageResource(R.drawable.fast_1); // Fallback from XML
        }
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, time, score, price;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_pic);
            title = itemView.findViewById(R.id.item_title);
            time = itemView.findViewById(R.id.tv_time);
            score = itemView.findViewById(R.id.score);
            price = itemView.findViewById(R.id.price);
        }
    }
}