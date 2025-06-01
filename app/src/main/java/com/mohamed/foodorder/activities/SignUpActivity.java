package com.mohamed.foodorder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference db;
    private GoogleSignInClient googleSignInClient;
    private EditText usernameField, emailField, passwordField, confirmPasswordField;
    private Spinner roleSpinner;
    private MaterialButton signupButton;
    private TextView goToLogin;
    private ImageView googleSignUp;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        usernameField = findViewById(R.id.username);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        confirmPasswordField = findViewById(R.id.repassword);
        roleSpinner = findViewById(R.id.role_spinner);
        signupButton = findViewById(R.id.signupbtn);
        goToLogin = findViewById(R.id.go_to_login);
        googleSignUp = findViewById(R.id.google);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.user_roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Log.d("SignUpActivity", "Google Sign-In result code: " + result.getResultCode());
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                try {
                    GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                    Log.d("SignUpActivity", "Google Sign-In account: " + account.getEmail());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.e("SignUpActivity", "Google Sign-In failed: " + e.getStatusCode() + " - " + e.getMessage());
                    Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w("SignUpActivity", "Google Sign-In cancelled or failed, result code: " + result.getResultCode());
                Toast.makeText(this, "Google Sign-In cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        // Set click listeners
        signupButton.setOnClickListener(v -> signupUser());
        goToLogin.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));
        googleSignUp.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            Log.d("SignUpActivity", "Launching Google Sign-In intent");
            googleSignInLauncher.launch(signInIntent);
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        mAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d("SignUpActivity", "Firebase auth successful for user: " + user.getEmail());
                            checkUserRole(user);
                        }
                    } else {
                        Log.e("SignUpActivity", "Firebase auth failed", task.getException());
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                        if (user != null) {
                            User userData = new User(email, role, username, System.currentTimeMillis());
                            db.child("users").child(user.getUid()).setValue(userData)
                                    .addOnSuccessListener(aVoid -> checkUserRole(user))
                                    .addOnFailureListener(e -> Toast.makeText(this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                            ? new Intent(SignUpActivity.this, RestaurantHomeActivity.class)
                            : new Intent(SignUpActivity.this, MainActivity.class);
                } else {
                    // New Google Sign-In user, default to customer role
                    String username = user.getDisplayName() != null ? user.getDisplayName() : "User";
                    User newUser = new User(user.getEmail(), "customer", username, System.currentTimeMillis());
                    db.child("users").child(user.getUid()).setValue(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(SignUpActivity.this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                    return;
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(SignUpActivity.this, "Error fetching user data: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}