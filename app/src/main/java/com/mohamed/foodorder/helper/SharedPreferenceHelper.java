package com.mohamed.foodorder.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SharedPreferenceHelper {
    public static final String PREF_NAME = "UserPref";
    public static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private static final String IMAGE_KEY = "profile_image";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save user data (called during signup)
    public void saveUserData(String username, String email, String password) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply(); // or commit()
    }

    public void saveUserDataWithoutPassword(String username, String email) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.apply(); // or commit()
    }

    // Retrieve username
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    // Retrieve email
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    // Retrieve password
    public String getPassword() {
        return sharedPreferences.getString(KEY_PASSWORD, null);
    }

    // Clear user data (if needed)
    public void clearUserData() {
        editor.clear();
        editor.apply();
    }

    public void saveImageToPreferences(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(IMAGE_KEY, encodedImage);
        editor.apply();
    }

    public void loadProfileImage(CircleImageView userImage) {
        String encodedImage = sharedPreferences.getString(IMAGE_KEY, null);
        if (encodedImage != null) {
            byte[] byteArray = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            userImage.setImageBitmap(bitmap);
        }
    }

}

