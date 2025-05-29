package com.mohamed.foodorder.helper;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed.foodorder.domain.models.Cart;
import com.mohamed.foodorder.domain.models.CartItem;
import com.mohamed.foodorder.domain.models.Meal;
import com.mohamed.foodorder.domain.models.Order;

import java.util.ArrayList;
import java.util.List;

public class ManagementCart {
    private Context context;
    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private String userId;

    public ManagementCart(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseDatabase.getInstance().getReference();
        this.userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    }

    public void insertFood(Meal item) {
        if (userId == null) {
            Toast.makeText(context, "Please log in to add items to cart", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItem cartItem = new CartItem(item, item.getNumberInCart());
        db.child("users").child(userId).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cart cart = snapshot.getValue(Cart.class);
                if (cart == null) {
                    cart = new Cart(userId, new ArrayList<>(), 0.0);
                }

                List<CartItem> meals = cart.getMeals();
                boolean existAlready = false;
                int index = -1;
                for (int i = 0; i < meals.size(); i++) {
                    if (meals.get(i).getMealId().equals(item.getId())) {
                        existAlready = true;
                        index = i;
                        break;
                    }
                }

                if (existAlready) {
                    meals.get(index).setQuantity(cartItem.getQuantity());
                } else {
                    meals.add(cartItem);
                }

                double totalPrice = 0.0;
                for (CartItem ci : meals) {
                    totalPrice += ci.getPrice() * ci.getQuantity();
                }
                cart.setTotalPrice(totalPrice);

                db.child("users").child(userId).child("cart").setValue(cart)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Added to Your Cart", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Error adding to cart: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error accessing cart: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getListCart(OnCartListener listener) {
        if (userId == null) {
            listener.onCartRetrieved(new ArrayList<>());
            return;
        }

        db.child("users").child(userId).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cart cart = snapshot.getValue(Cart.class);
                List<CartItem> meals = (cart != null && cart.getMeals() != null) ? cart.getMeals() : new ArrayList<>();
                listener.onCartRetrieved(meals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error loading cart: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                listener.onCartRetrieved(new ArrayList<>());
            }
        });
    }

    public void plusNumberFood(List<CartItem> cartItems, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (userId == null) return;

        CartItem item = cartItems.get(position);
        item.setQuantity(item.getQuantity() + 1);

        updateCart(cartItems, changeNumberItemsListener);
    }

    public void minusNumberFood(List<CartItem> cartItems, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (userId == null) return;

        CartItem item = cartItems.get(position);
        if (item.getQuantity() == 1) {
            cartItems.remove(position);
        } else {
            item.setQuantity(item.getQuantity() - 1);
        }

        updateCart(cartItems, changeNumberItemsListener);
    }

    private void updateCart(List<CartItem> cartItems, ChangeNumberItemsListener changeNumberItemsListener) {
        Cart cart = new Cart(userId, cartItems, 0.0);
        double totalPrice = 0.0;
        for (CartItem ci : cartItems) {
            totalPrice += ci.getPrice() * ci.getQuantity();
        }
        cart.setTotalPrice(totalPrice);

        db.child("users").child(userId).child("cart").setValue(cart)
                .addOnSuccessListener(aVoid -> changeNumberItemsListener.onChanged())
                .addOnFailureListener(e -> Toast.makeText(context, "Error updating cart: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void placeOrder(double tax, double deliveryFee, OnOrderListener listener) {
        if (userId == null) {
            Toast.makeText(context, "Please log in to place an order", Toast.LENGTH_SHORT).show();
            listener.onOrderResult(false, "User not logged in");
            return;
        }

        getListCart(cartItems -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(context, "Your cart is empty", Toast.LENGTH_SHORT).show();
                listener.onOrderResult(false, "Cart is empty");
                return;
            }

            double subTotal = 0.0;
            for (CartItem item : cartItems) {
                subTotal += item.getPrice() * item.getQuantity();
            }
            double total = subTotal + tax + deliveryFee;

            String orderId = db.child("orders").push().getKey();
            Order order = new Order(
                    orderId,
                    userId,
                    new ArrayList<>(cartItems),
                    subTotal,
                    tax,
                    deliveryFee,
                    total,
                    System.currentTimeMillis(),
                    "pending"
            );

            db.child("orders").child(orderId).setValue(order)
                    .addOnSuccessListener(aVoid -> {
                        // Clear the cart
                        db.child("users").child(userId).child("cart").removeValue()
                                .addOnSuccessListener(aVoid1 -> {
                                    Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show();
                                    listener.onOrderResult(true, "Order placed");
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error clearing cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    listener.onOrderResult(false, "Failed to clear cart");
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error placing order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        listener.onOrderResult(false, "Failed to place order");
                    });
        });
    }

    public double getTotalFee() {
        final double[] fee = {0.0};
        db.child("users").child(userId).child("cart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cart cart = snapshot.getValue(Cart.class);
                if (cart != null && cart.getMeals() != null) {
                    for (CartItem item : cart.getMeals()) {
                        fee[0] += item.getPrice() * item.getQuantity();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error silently
            }
        });
        return fee[0];
    }

    public interface OnCartListener {
        void onCartRetrieved(List<CartItem> cartItems);
    }

    public interface OnOrderListener {
        void onOrderResult(boolean success, String message);
    }
}