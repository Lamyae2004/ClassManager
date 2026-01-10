package com.ensa.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ensa.mobile.authentification.api.RetrofitClient;
import com.ensa.mobile.authentification.models.UserResponse;
import com.ensa.mobile.emploitemps.ui.FragmentEmploi;
import com.ensa.mobile.authentification.activities.LoginActivity;
import com.ensa.mobile.gestionDocuments.ui.FragmentDocumentsEtudiants;
import com.ensa.mobile.utils.TokenManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TokenManager tokenManager;
    private TextView headerUserName;
    private TextView headerUserRole;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tokenManager = TokenManager.getInstance(this);

        // Check if user is logged in
        if (!tokenManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }

        drawerLayout = findViewById(R.id.drawerLayout);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigationView);

        // --- CONFIGURATION DU MENU SELON LE RÔLE ---
        setupMenuBasedOnRole();

        // --- AJOUT DU HEADER DYNAMIQUE ---
        View headerView = navigationView.getHeaderView(0);
        headerUserName = headerView.findViewById(R.id.headerUserName);
        headerUserRole = headerView.findViewById(R.id.headerUserRole);

        // Load user profile to get full information
        loadUserProfile();

        // Icône menu
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        // --- Ajout du popup profil ---
        LinearLayout profileCompact = findViewById(R.id.profileCompact);
        profileCompact.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, view);
            popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

            // Update popup menu items with user info
            MenuItem nameItem = popup.getMenu().findItem(R.id.headerUserName);
            MenuItem roleItem = popup.getMenu().findItem(R.id.headerUserRole);

            String fullName = tokenManager.getFullName();
            if (fullName.isEmpty()) {
                fullName = tokenManager.getEmail();
            }
            String role = tokenManager.getRole();
            String roleDisplay = role.equalsIgnoreCase("TEACHER") ? "Teacher" : "Student";

            if (nameItem != null) {
                nameItem.setTitle(fullName);
            }
            if (roleItem != null) {
                roleItem.setTitle(roleDisplay);
            }

            popup.show();
        });

        // Fragment par défaut : Emploi du temps
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new FragmentEmploi())
                .commit();

        // Navigation menu
        navigationView.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.nav_emploi) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new FragmentEmploi())
                        .commit();
            }
            if (item.getItemId() == R.id.nav_documentsTeacher) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.fragmentContainer,
                                new com.ensa.mobile.gestionDocuments.ui.UploadDocumentFragment()
                        )
                        .addToBackStack(null)
                        .commit();
            }


            if (item.getItemId() == R.id.nav_absence) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new FragmentAbsenceEtudiant())
                        .commit();
            }

            if (item.getItemId() == R.id.nav_documents) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new FragmentDocumentsEtudiants())
                        .commit();
            }

            // Gestion du logout
            if (item.getItemId() == R.id.nav_logout) {
                handleLogout();
                return true;
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    /**
     * Configure le menu du NavigationView selon le rôle de l'utilisateur
     */
    private void setupMenuBasedOnRole() {
        String role = tokenManager.getRole();
        Menu menu = navigationView.getMenu();
        MenuItem absenceItem = menu.findItem(R.id.nav_absence);
        MenuItem documentsItem = menu.findItem(R.id.nav_documentsTeacher);
        if (role != null && role.equalsIgnoreCase("STUDENT")) {
            // Afficher "Mes absences" pour les étudiants
            if (absenceItem != null) {
                absenceItem.setVisible(true);
            }
            if (documentsItem != null) documentsItem.setVisible(false);
        } else if (role != null && role.equalsIgnoreCase("TEACHER")) {
            // Masquer "Mes absences" pour les professeurs
            if (absenceItem != null) {
                absenceItem.setVisible(false);
                if (documentsItem != null) documentsItem.setVisible(true);
            }
        }
    }

    /**
     * Gère la déconnexion de l'utilisateur
     */
    private void handleLogout() {
        tokenManager.clearToken();
        navigateToLogin();
    }

    private void loadUserProfile() {
        String token = tokenManager.getToken();
        if (token == null) {
            navigateToLogin();
            return;
        }

        Call<UserResponse> call = RetrofitClient.getInstance().getAuthApiService().getProfile("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    // Update TokenManager with latest user info
                    tokenManager.saveFirstname(user.getFirstname());
                    tokenManager.saveLastname(user.getLastname());
                    tokenManager.saveRole(user.getRole());

                    // Update nav header (if TextViews exist)
                    if (headerUserName != null && headerUserRole != null) {
                        String fullName = user.getFirstname() + " " + user.getLastname();
                        headerUserName.setText(fullName);
                        String roleDisplay = user.getRole().equalsIgnoreCase("TEACHER") ? "Teacher" : "Student";
                        headerUserRole.setText(roleDisplay);
                    }
                } else {
                    // Token might be invalid, redirect to login
                    tokenManager.clearToken();
                    navigateToLogin();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // If API call fails, use cached values (if TextViews exist)
                if (headerUserName != null && headerUserRole != null) {
                    String fullName = tokenManager.getFullName();
                    if (!fullName.isEmpty()) {
                        headerUserName.setText(fullName);
                    } else {
                        headerUserName.setText(tokenManager.getEmail());
                    }
                    String roleDisplay = tokenManager.getRole().equalsIgnoreCase("TEACHER") ? "Teacher" : "Student";
                    headerUserRole.setText(roleDisplay);
                }
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}