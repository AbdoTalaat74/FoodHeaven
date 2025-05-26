package com.mohamed.foodorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.mohamed.foodorder.R;
import com.mohamed.foodorder.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Password visibility toggle
        binding.password.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (binding.password.getRight() - binding.password.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        binding.login.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            String errorMessage;
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                errorMessage = "User does not exist";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                errorMessage = "Invalid password";
                            } catch (Exception e) {
                                errorMessage = "Login failed: " + e.getMessage();
                            }
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Social media button mappings
        binding.facebook.setOnClickListener(v -> openSocial("com.facebook.katana", "https://www.facebook.com/login"));
        binding.twitter.setOnClickListener(v -> openSocial("com.twitter.android", "https://twitter.com/login"));
        binding.github.setOnClickListener(v -> openWebsite("https://github.com/login"));

        // Go to signup button
        binding.goToLogin.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
    }

    private void togglePasswordVisibility() {
        boolean isPasswordVisible = binding.password.getTransformationMethod() == null;
        if (isPasswordVisible) {
            binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
        } else {
            binding.password.setTransformationMethod(null);
            binding.password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
        }
        binding.password.setSelection(binding.password.getText().length());
    }

    private void openSocial(String packageName, String fallbackUrl) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            startActivity(intent);
        } else {
            openWebsite(fallbackUrl);
        }
    }

    private void openWebsite(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(android.net.Uri.parse(url));
        startActivity(i);
    }
}