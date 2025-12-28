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

public class VerifyOtpActivity extends AppCompatActivity {
    private TextInputEditText etOtp;
    private Button btnValidateOtp;
    private ProgressBar progressBar;
    private String email = "";
    private String mode = "validate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        // Initialize views
        etOtp = findViewById(R.id.etOtp);
        btnValidateOtp = findViewById(R.id.btnValidateOtp);
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

        btnValidateOtp.setOnClickListener(v -> handleOtpVerification());
    }

    private void handleOtpVerification() {
        String otpText = etOtp.getText().toString().trim();

        if (otpText.isEmpty()) {
            Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        int otp;
        try {
            otp = Integer.parseInt(otpText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid OTP format", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnValidateOtp.setEnabled(false);

        Call<String> call;
        if ("validate".equals(mode)) {
            call = RetrofitClient.getInstance().getAuthApiService().verifyAccountActivation(email, otp);
        } else {
            call = RetrofitClient.getInstance().getAuthApiService().verifyForgotPasswordOtp(email, otp);
        }

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressBar.setVisibility(View.GONE);
                btnValidateOtp.setEnabled(true);

                if (response.isSuccessful()) {
                    String message = response.body();
                    if (message == null) {
                        message = "OTP verified";
                    }
                    Toast.makeText(VerifyOtpActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Navigate to password setup
                    Intent intent = new Intent(VerifyOtpActivity.this, SetUpPasswordActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = "OTP verification failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(VerifyOtpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnValidateOtp.setEnabled(true);
                Toast.makeText(VerifyOtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
