package com.mohamed.foodorder.adapters;

import android.content.Context;
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
import com.mohamed.foodorder.databinding.ViewholderCartBinding;
import com.mohamed.foodorder.domain.FoodDomain;
import com.mohamed.foodorder.helper.ChangeNumberItemsListner;
import com.mohamed.foodorder.helper.ManagementCart;
import java.util.ArrayList;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartListViewHolder> {

    private ArrayList<FoodDomain> listFoodSelected;
    private ManagementCart managementCart;
    private ChangeNumberItemsListner changeNumberItemsListner;

    public CartListAdapter(ArrayList<FoodDomain> listFoodSelected, Context context, ChangeNumberItemsListner changeNumberItemsListner) {
        this.listFoodSelected = listFoodSelected;
        managementCart = new ManagementCart(context);
        this.changeNumberItemsListner = changeNumberItemsListner;
    }

    @NonNull
    @Override
    public CartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new CartListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListViewHolder holder, int position) {
        FoodDomain food = listFoodSelected.get(position);
        holder.binding.itemName.setText(food.getTitle());
        holder.binding.fee.setText(String.format("$%.2f", food.getPrice()));
        holder.binding.total.setText(String.format("$%.2f", food.getNumberInCart() * food.getPrice()));
        holder.binding.quantity.setText(String.valueOf(food.getNumberInCart()));

        // Decode Base64 image
        try {
            byte[] decodedBytes = Base64.decode(food.getPicUrl(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            Glide.with(holder.binding.getRoot().getContext())
                    .load(bitmap)
                    .transform(new GranularRoundedCorners(30, 30, 30, 30))
                    .into(holder.binding.foodItem);
        } catch (Exception e) {
            Glide.with(holder.binding.getRoot().getContext())
                    .load(R.drawable.placeholder_image)
                    .transform(new GranularRoundedCorners(30, 30, 30, 30))
                    .into(holder.binding.foodItem);
        }

        holder.binding.plus.setOnClickListener(v -> {
            managementCart.plusNumberFood(listFoodSelected, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListner.onChanged();
            });
        });

        holder.binding.minus.setOnClickListener(v -> {
            managementCart.minusNumberFood(listFoodSelected, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListner.onChanged();
            });
        });
    }

    @Override
    public int getItemCount() {
        return listFoodSelected.size();
    }

    public static class CartListViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public CartListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderCartBinding.bind(itemView);
        }
    }
}