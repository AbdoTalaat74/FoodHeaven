package com.mohamed.foodorder.helper;

import android.content.Context;
import android.widget.Toast;

import com.mohamed.foodorder.domain.FoodDomain;

import java.util.ArrayList;

public class ManagementCart {
    private Context context;
    private TinyDB tinyDB;

    public ManagementCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertFood(FoodDomain item) {
        ArrayList<FoodDomain> foodList = getListCart();
        boolean existAlready = false;
        int n = 0;
        for (int y = 0; y < foodList.size(); y++) {
            if (foodList.get(y).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = y;
                break;
            }
        }
        if (existAlready) {
            foodList.get(n).setNumberInCart(item.getNumberInCart());
        } else {
            foodList.add(item);
        }
        tinyDB.putListObject("CartList", foodList);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }

    public ArrayList<FoodDomain> getListCart() {
        return tinyDB.getListObject("CartList");
    }


    public void minusNumberFood(ArrayList<FoodDomain> listFood, int position, ChangeNumberItemsListner listner) {
        if (listFood.get(position).getNumberInCart() == 1) {
            listFood.remove(position);
        } else {
            listFood.get(position).setNumberInCart(listFood.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList", listFood);
        listner.onChanged();
    }

    public void plusNumberFood(ArrayList<FoodDomain> listFood, int position, ChangeNumberItemsListner listner) {
        listFood.get(position).setNumberInCart(listFood.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listFood);
        listner.onChanged();
    }

    public Double getFee() {
        ArrayList<FoodDomain> listFood2 = getListCart();
        double fee = 0;
        for (int i = 0; i < listFood2.size(); i++) {
            fee = fee + (listFood2.get(i).getPrice() * listFood2.get(i).getNumberInCart());
        }
        return fee;
    }

}
