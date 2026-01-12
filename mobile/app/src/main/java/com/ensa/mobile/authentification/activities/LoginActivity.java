package com.ensa.mobile.authentification.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ensa.mobile.MainActivity;
import com.ensa.mobile.R;
import com.ensa.mobile.authentification.api.RetrofitClient;
import com.ensa.mobile.authentification.models.AuthenticationRequest;
import com.ensa.mobile.authentification.models.AuthenticationResponse;
import com.ensa.mobile.authentification.models.UserResponse;
import com.ensa.mobile.utils.TokenManager;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;

import com.ensa.mobile.utils.SimpleIdlingResource;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private Button btnLogin;
    private TextView tvError;
    private TextView tvFirstLogin;
    private TextView tvForgotPassword;
    private ProgressBar progressBar;
    private TokenManager tokenManager;
    @Nullable
    private SimpleIdlingResource idlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        tvFirstLogin = findViewById(R.id.tvFirstLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        tokenManager = TokenManager.getInstance(this);

        // Check if already logged in
        if (tokenManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        tvFirstLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, VerifyEmailActivity.class);
            intent.putExtra("mode", "validate");
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, VerifyEmailActivity.class);
            intent.putExtra("mode", "forgot");
            startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        hideError(); // Cacher les erreurs précédentes

        if (email.isEmpty() || password.isEmpty()) {
            showError(getString(R.string.all_fields_required));
            return;
        }
// Indiquer que l'activité n'est plus idle
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        AuthenticationRequest request = new AuthenticationRequest(email, password);
        Call<AuthenticationResponse> call = RetrofitClient.getInstance().getAuthApiService().authenticate(request);

        call.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    tokenManager.saveToken(token);
                    tokenManager.saveEmail(email);

                    // Get user profile
                    loadUserProfile(token);
                } else {
                    String errorMessage = "Login failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showError(errorMessage);
                    // L'opération est terminée
                    if (idlingResource != null) {
                        idlingResource.setIdleState(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                showError("Error: " + t.getMessage());
                // L'opération est terminée
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }
        });
    }

    private void loadUserProfile(String token) {
        Call<UserResponse> call = RetrofitClient.getInstance().getAuthApiService().getProfile("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();

                    // DÉCLARER role ICI, au début
                    String role = user.getRole();

                    // Sauvegarder les infos de base
                    tokenManager.saveRole(role);
                    tokenManager.saveFirstname(user.getFirstname());
                    tokenManager.saveLastname(user.getLastname());
                    tokenManager.saveStudentId(user.getId());
                    tokenManager.saveClasse(user.getClasse());
                    tokenManager.saveFiliere(user.getFiliere());
                    tokenManager.saveNiveau(user.getNiveau());

                    // Sauvegarder l'ID selon le rôle
                    if (role != null && role.equalsIgnoreCase("TEACHER")) {
                        tokenManager.saveTeacherId(user.getId());
                    }

                    // Check if role is TEACHER or STUDENT (case-insensitive)
                    if (role != null && (role.equalsIgnoreCase("TEACHER") || role.equalsIgnoreCase("STUDENT"))) {
                        // L'opération est terminée avant la navigation
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                        navigateToMain();
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        showError("Access denied. Only TEACHER and STUDENT can access this app.");
                        tokenManager.clearToken();
                        // L'opération est terminée
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                } else {
                    showError("Failed to get user profile");
                    // L'opération est terminée
                    if (idlingResource != null) {
                        idlingResource.setIdleState(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showError("Error loading profile: " + t.getMessage());
                // L'opération est terminée
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);

        // IMPORTANT: Afficher aussi le CardView parent
        View cvError = findViewById(R.id.cvError);
        if (cvError != null) {
            cvError.setVisibility(View.VISIBLE);
        }
    }

    private void hideError() {
        tvError.setVisibility(View.GONE);
        View cvError = findViewById(R.id.cvError);
        if (cvError != null) {
            cvError.setVisibility(View.GONE);
        }
    }

    /**
     * Méthode pour les tests uniquement
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }
}

