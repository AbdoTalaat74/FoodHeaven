package com.mohamed.foodorder.adapters;

import android.content.Context;
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

    ArrayList<FoodDomain> listFoodSelected;
    private ManagementCart managementCart;
    ChangeNumberItemsListner changeNumberItemsListner;

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
        holder.binding.itemName.setText(listFoodSelected.get(position).getTitle());
        holder.binding.fee.setText("$" + listFoodSelected.get(position).getPrice());
        holder.binding.total.setText("$" + Math.round(listFoodSelected.get(position).getNumberInCart() * listFoodSelected.get(position).getPrice()));
        holder.binding.quantity.setText(String.valueOf(listFoodSelected.get(position).getNumberInCart()));

        int drawableResourceId = holder.binding.getRoot().getContext().getResources().getIdentifier(
                listFoodSelected.get(position).getPicUrl(),
                "drawable",
                holder.binding.getRoot().getContext().getPackageName()
        );
        Glide.with(holder.binding.getRoot().getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30, 30, 30, 30))
                .into(holder.binding.foodItem);

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

    public class CartListViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public CartListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderCartBinding.bind(itemView);
        }
    }
}
