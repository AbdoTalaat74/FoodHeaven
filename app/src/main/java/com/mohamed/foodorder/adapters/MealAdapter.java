package com.mohamed.foodorder.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mohamed.foodorder.R;
import com.mohamed.foodorder.activities.DetailsActivity;
import com.mohamed.foodorder.domain.models.Meal;
import java.util.ArrayList;
import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private List<Meal> mealList = new ArrayList<>();
    private final int viewType;

    public static final int VIEW_TYPE_MAIN = 0;
    public static final int VIEW_TYPE_CATEGORY = 1;

    public MealAdapter(int viewType) {
        this.viewType = viewType;
    }

    public void setMeals(List<Meal> meals) {
        this.mealList = meals;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = viewType == VIEW_TYPE_MAIN ? R.layout.viewholder_food_list : R.layout.view_holder_meal_list;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new MealViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);

        if (holder.title != null) {
            holder.title.setText(meal.getName());
        } else {
            Log.e("MealAdapter", "title TextView is null for viewType: " + viewType);
        }
        if (holder.price != null) {
            holder.price.setText(String.format("$%.2f", meal.getPrice()));
        } else {
            Log.e("MealAdapter", "price TextView is null for viewType: " + viewType);
        }
        if (holder.score != null) {
            holder.score.setText(String.format("%.1f", meal.getRating()));
        } else {
            Log.e("MealAdapter", "score TextView is null for viewType: " + viewType);
        }
        if (holder.time != null) {
            holder.time.setText(meal.getDeliveryTime() + " min");
        } else {
            Log.e("MealAdapter", "time TextView is null for viewType: " + viewType);
        }
        if (holder.description != null && meal.getDescription() != null) {
            holder.description.setText(meal.getDescription());
        } else if (holder.description != null) {
            holder.description.setText("No description available");
        }

        if (holder.image != null) {
            if (meal.getImageBase64() != null && !meal.getImageBase64().isEmpty()) {
                byte[] decodedBytes = Base64.decode(meal.getImageBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                holder.image.setImageBitmap(bitmap);
            } else {
                holder.image.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            Log.e("MealAdapter", "image ImageView is null for viewType: " + viewType);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailsActivity.class);
            intent.putExtra("object", meal);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, time, score, price, description;

        public MealViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_MAIN) {
                image = itemView.findViewById(R.id.item_image_id);
                title = itemView.findViewById(R.id.item_name);
                time = itemView.findViewById(R.id.time_label);
                score = itemView.findViewById(R.id.rating_label);
                price = itemView.findViewById(R.id.price_label);
                description = null;
            } else {
                image = itemView.findViewById(R.id.meal_image);
                title = itemView.findViewById(R.id.meal_name);
                time = itemView.findViewById(R.id.meal_delivery_time);
                score = itemView.findViewById(R.id.meal_rating);
                price = itemView.findViewById(R.id.meal_price);
                description = itemView.findViewById(R.id.meal_description);
            }

            if (image == null) Log.e("MealViewHolder", "image not found for viewType: " + viewType);
            if (title == null) Log.e("MealViewHolder", "title not found for viewType: " + viewType);
            if (time == null) Log.e("MealViewHolder", "time not found for viewType: " + viewType);
            if (score == null) Log.e("MealViewHolder", "score not found for viewType: " + viewType);
            if (price == null) Log.e("MealViewHolder", "price not found for viewType: " + viewType);
            if (viewType == VIEW_TYPE_CATEGORY && description == null) {
                Log.e("MealViewHolder", "description not found for viewType: " + viewType);
            }
        }
    }
}