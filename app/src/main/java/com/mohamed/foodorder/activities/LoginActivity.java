package com.mohamed.foodorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mohamed.foodorder.R;
import com.mohamed.foodorder.domain.models.User;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private EditText emailField, passwordField;
    private MaterialButton loginButton;
    private TextView goToSignup;
    private CheckBox rememberCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        goToSignup = findViewById(R.id.go_to_signup);
        rememberCheckBox = findViewById(R.id.remember);

        loginButton.setOnClickListener(v -> loginUser());
        goToSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));

        findViewById(R.id.facebook).setOnClickListener(v -> Toast.makeText(this, "Facebook login not implemented", Toast.LENGTH_SHORT).show());
        findViewById(R.id.twitter).setOnClickListener(v -> Toast.makeText(this, "Twitter login not implemented", Toast.LENGTH_SHORT).show());
        findViewById(R.id.github).setOnClickListener(v -> Toast.makeText(this, "GitHub login not implemented", Toast.LENGTH_SHORT).show());
    }

    private void loginUser() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserRole();
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User userData = snapshot.getValue(User.class);
                        String role = userData.getRole();
                        Intent intent;
                        if ("restaurant".equals(role)) {
                            intent = new Intent(LoginActivity.this, RestaurantHomeActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Error fetching user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}