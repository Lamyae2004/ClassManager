package com.ensa.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREFS_NAME = "ClassManagerPrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_EMAIL = "user_email";
    
    private static TokenManager instance;
    private SharedPreferences prefs;
    private static final String KEY_ROLE = "user_role";
    private static final String KEY_FIRSTNAME = "user_firstname";
    private static final String KEY_LASTNAME = "user_lastname";
    
    private TokenManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    
    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }
    
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }
    
    public void saveEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }
    
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }
    
    public void clearToken() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_EMAIL).remove(KEY_ROLE).remove(KEY_FIRSTNAME).remove(KEY_LASTNAME).apply();
    }
    
    public boolean isLoggedIn() {
        return getToken() != null;
    }


    public void saveRole(String role) {
        prefs.edit().putString(KEY_ROLE, role).apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "STUDENT"); // par défaut
    }

    public void saveFirstname(String firstname) {
        prefs.edit().putString(KEY_FIRSTNAME, firstname).apply();
    }

    public String getFirstname() {
        return prefs.getString(KEY_FIRSTNAME, "");
    }

    public void saveLastname(String lastname) {
        prefs.edit().putString(KEY_LASTNAME, lastname).apply();
    }

    public String getLastname() {
        return prefs.getString(KEY_LASTNAME, "");
    }

    public String getFullName() {
        String firstname = getFirstname();
        String lastname = getLastname();
        if (firstname.isEmpty() && lastname.isEmpty()) {
            return "";
        }
        return firstname + " " + lastname;
    }

    public void saveStudentId(long id) {
        prefs.edit().putLong("studentId", id).apply();
    }

    public long getStudentId() {
        return prefs.getLong("studentId", -1);
    }

    public void saveClasse(String classe) {
        prefs.edit().putString("classe", classe).apply();
    }

    public String getClasse() {
        return prefs.getString("classe", "");
    }

    public void saveFiliere(String filiere) {
        prefs.edit().putString("filiere", filiere).apply();
    }

    public String getFiliere() {
        return prefs.getString("filiere", "");
    }

    public void saveNiveau(String niveau) {
        prefs.edit().putString("niveau", niveau).apply();
    }

    public String getNiveau() {
        return prefs.getString("niveau", "");
    }

}

