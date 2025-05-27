package com.mohamed.foodorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private EditText usernameField, emailField, passwordField, confirmPasswordField;
    private Spinner roleSpinner;
    private MaterialButton signupButton;
    private TextView goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        // Initialize UI elements
        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.repassword);
        roleSpinner = findViewById(R.id.role_spinner);
        signupButton = findViewById(R.id.signupbtn);
        goToLogin = findViewById(R.id.go_to_login);

        // Setup role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        signupButton.setOnClickListener(v -> signupUser());
        goToLogin.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));

        // Social sign-up buttons (placeholder)
        findViewById(R.id.google).setOnClickListener(v -> Toast.makeText(this, "Google sign-up not implemented", Toast.LENGTH_SHORT).show());
        findViewById(R.id.facebook).setOnClickListener(v -> Toast.makeText(this, "Facebook sign-up not implemented", Toast.LENGTH_SHORT).show());
        findViewById(R.id.twitter).setOnClickListener(v -> Toast.makeText(this, "Twitter sign-up not implemented", Toast.LENGTH_SHORT).show());
    }

    private void signupUser() {
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();
        String role = roleSpinner.getSelectedItem().toString().toLowerCase();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        User userData = new User(email, role, username, System.currentTimeMillis());
                        db.child("users").child(user.getUid()).setValue(userData)
                                .addOnSuccessListener(aVoid -> checkUserRole())
                                .addOnFailureListener(e -> Toast.makeText(this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                            intent = new Intent(SignUpActivity.this, RestaurantHomeActivity.class);
                        } else {
                            intent = new Intent(SignUpActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(SignUpActivity.this, "Error fetching user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}