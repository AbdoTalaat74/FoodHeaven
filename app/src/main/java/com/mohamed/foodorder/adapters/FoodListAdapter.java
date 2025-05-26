package com.mohamed.foodorder.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.mohamed.foodorder.R;
import com.mohamed.foodorder.activities.DetailsActivity;
import com.mohamed.foodorder.databinding.ViewholderFoodListBinding;
import com.mohamed.foodorder.domain.FoodDomain;

import java.util.ArrayList;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.FoodViewHolder> {

    private ArrayList<FoodDomain> foodList;
    private Context context;

    public FoodListAdapter(ArrayList<FoodDomain> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_food_list, parent, false);
        context = inflater.getContext();
        return new FoodViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        holder.binding.title.setText(foodList.get(position).getTitle());
        holder.binding.tvTime.setText(foodList.get(position).getTime() + " min");
        holder.binding.score.setText(String.valueOf(foodList.get(position).getScore()));
        @SuppressLint("DiscouragedApi") int drawableResourceId = holder.itemView.getResources().getIdentifier(foodList.get(position).getPicUrl(), "drawable", holder.binding.getRoot().getContext().getPackageName());
        Glide.with(holder.binding.getRoot().getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30, 30, 0, 0))
                .into(holder.binding.itemPic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.binding.getRoot().getContext(), DetailsActivity.class);
            intent.putExtra("object", foodList.get(position));
            holder.binding.getRoot().getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {
        private ViewholderFoodListBinding binding;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderFoodListBinding.bind(itemView);

        }
    }
}
