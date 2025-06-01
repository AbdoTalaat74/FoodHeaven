package com.mohamed.foodorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
    private GoogleSignInClient googleSignInClient;
    private EditText emailField, passwordField;
    private MaterialButton loginButton;
    private TextView goToSignup;
    private CheckBox rememberCheckBox;
    private ImageView googleLogin;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

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
        googleLogin = findViewById(R.id.google_login);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize ActivityResultLauncher for Google Sign-In
        googleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d("LoginActivity", "Google Sign-In result code: " + result.getResultCode());
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                try {
                    GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                    Log.d("LoginActivity", "Google Sign-In account: " + account.getEmail());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.e("LoginActivity", "Google Sign-In failed: " + e.getStatusCode() + " - " + e.getMessage());
                    Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w("LoginActivity", "Google Sign-In cancelled or failed, result code: " + result.getResultCode());
                Toast.makeText(this, "Google Sign-In cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listeners
        loginButton.setOnClickListener(v -> loginUser());
        goToSignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class)));
        googleLogin.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            Log.d("LoginActivity", "Launching Google Sign-In intent");
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d("LoginActivity", "Firebase auth successful for user: " + user.getEmail());
                            checkUserRole(user);
                        }
                    } else {
                        Log.e("LoginActivity", "Firebase auth failed", task.getException());
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserRole(user);
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRole(FirebaseUser user) {
        db.child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Intent intent;
                if (snapshot.exists()) {
                    User userData = snapshot.getValue(User.class);
                    String role = userData.getRole();
                    intent = "restaurant".equals(role)
                            ? new Intent(LoginActivity.this, RestaurantHomeActivity.class)
                            : new Intent(LoginActivity.this, MainActivity.class);
                } else {
                    // New Google Sign-In user, default to customer role
                    String username = user.getDisplayName() != null ? user.getDisplayName() : "User";
                    User newUser = new User(user.getEmail(), "customer", username, System.currentTimeMillis());
                    db.child("users").child(user.getUid()).setValue(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    return;
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Error fetching user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}