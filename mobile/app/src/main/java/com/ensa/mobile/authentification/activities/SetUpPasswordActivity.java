package com.ensa.mobile.authentification.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ensa.mobile.R;
import com.ensa.mobile.authentification.api.RetrofitClient;
import com.ensa.mobile.authentification.models.ChangePassword;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetUpPasswordActivity extends AppCompatActivity {
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private String email = "";
    private String mode = "validate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_password);

        // Initialize views
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        email = getIntent().getStringExtra("email");
        mode = getIntent().getStringExtra("mode");
        if (mode == null) {
            mode = "validate";
        }

        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Email not found. Please start from the beginning.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnSubmit.setOnClickListener(v -> handlePasswordSetup());
    }

    private void handlePasswordSetup() {
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, getString(R.string.all_fields_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, getString(R.string.passwords_do_not_match), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        ChangePassword changePassword = new ChangePassword(password, confirmPassword);
        Call<String> call;
        if ("validate".equals(mode)) {
            call = RetrofitClient.getInstance().getAuthApiService().activateAccount(email, changePassword);
        } else {
            call = RetrofitClient.getInstance().getAuthApiService().changePassword(email, changePassword);
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);

                if (response.isSuccessful()) {
                    String message = response.body();
                    if (message == null) {
                        message = "Password set successfully";
                    }
                    Toast.makeText(SetUpPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Navigate to login
                    Intent intent = new Intent(SetUpPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = "Failed to set password";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(SetUpPasswordActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSubmit.setEnabled(true);
                Toast.makeText(SetUpPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
