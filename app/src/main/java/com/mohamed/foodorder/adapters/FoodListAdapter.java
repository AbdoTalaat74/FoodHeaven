package com.mohamed.foodorder.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.mohamed.foodorder.R;
import com.mohamed.foodorder.domain.FoodDomain;
import java.util.ArrayList;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.ViewHolder> {
    private final ArrayList<FoodDomain> items;

    public FoodListAdapter(ArrayList<FoodDomain> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_food_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FoodDomain item = items.get(position);
        holder.title.setText(item.getTitle());

        // Decode Base64 image
        try {
            byte[] decodedBytes = Base64.decode(item.getPicUrl(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            holder.image.setImageBitmap(bitmap);
        } catch (Exception e) {
            holder.image.setImageResource(R.drawable.placeholder_image);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView time;
        TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_pic); // Adjust ID
            time = itemView.findViewById(R.id.tv_time);
            title = itemView.findViewById(R.id.item_title); // Adjust ID
        }
    }
}