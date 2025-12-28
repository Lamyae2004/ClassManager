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
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyEmailActivity extends AppCompatActivity {
    private TextInputEditText etEmail;
    private Button btnSendMessage;
    private ProgressBar progressBar;
    private String mode = "validate"; // "validate" or "forgot"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        progressBar = findViewById(R.id.progressBar);

        mode = getIntent().getStringExtra("mode");
        if (mode == null) {
            mode = "validate";
        }

        btnSendMessage.setOnClickListener(v -> handleSendMessage());
    }

    private void handleSendMessage() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSendMessage.setEnabled(false);

        Call<String> call;
        if ("validate".equals(mode)) {
            call = RetrofitClient.getInstance().getAuthApiService().requestValidateAccount(email);
        } else {
            call = RetrofitClient.getInstance().getAuthApiService().requestForgotPassword(email);
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                btnSendMessage.setEnabled(true);

                if (response.isSuccessful()) {
                    String message = response.body();
                    if (message == null) {
                        message = "OTP sent to email";
                    }
                    Toast.makeText(VerifyEmailActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Navigate to OTP verification
                    Intent intent = new Intent(VerifyEmailActivity.this, VerifyOtpActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                } else {
                    String errorMessage = "Failed to send OTP";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(VerifyEmailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnSendMessage.setEnabled(true);
                Toast.makeText(VerifyEmailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
