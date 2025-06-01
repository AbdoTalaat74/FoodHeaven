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
import com.mohamed.foodorder.domain.models.CartItem;
import com.mohamed.foodorder.helper.ChangeNumberItemsListener;
import com.mohamed.foodorder.helper.ManagementCart;
import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartListViewHolder> {

    private List<CartItem> cartItems;
    private ManagementCart managementCart;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private Context context;

    public CartListAdapter(List<CartItem> cartItems, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.cartItems = cartItems;
        this.context = context;
        this.managementCart = new ManagementCart(context);
        this.changeNumberItemsListener = changeNumberItemsListener;
    }

    @NonNull
    @Override
    public CartListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new CartListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.binding.itemName.setText(item.getName());
        holder.binding.fee.setText(String.format("$%.2f", item.getPrice())); // Unit price
        holder.binding.quantity.setText(String.valueOf(item.getQuantity()));

        // Decode Base64 image
        try {
            byte[] decodedBytes = Base64.decode(item.getImageBase64(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            Glide.with(context)
                    .load(bitmap)
                    .transform(new GranularRoundedCorners(30, 30, 30, 30))
                    .into(holder.binding.foodItem);
        } catch (Exception e) {
            Glide.with(context)
                    .load(R.drawable.placeholder_image)
                    .transform(new GranularRoundedCorners(30, 30, 30, 30))
                    .into(holder.binding.foodItem);
        }

        holder.binding.plus.setOnClickListener(v -> {
            managementCart.plusNumberFood(cartItems, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListener.onChanged();
            });
        });

        holder.binding.minus.setOnClickListener(v -> {
            managementCart.minusNumberFood(cartItems, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListener.onChanged();
            });
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartListViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;

        public CartListViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ViewholderCartBinding.bind(itemView);
        }
    }
}