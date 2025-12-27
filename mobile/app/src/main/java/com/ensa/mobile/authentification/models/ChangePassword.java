package com.ensa.mobile.authentification.models;

public class ChangePassword {
    private String password;
    private String repeatPassword;

    public ChangePassword() {
    }

    public ChangePassword(String password, String repeatPassword) {
        this.password = password;
        this.repeatPassword = repeatPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}

