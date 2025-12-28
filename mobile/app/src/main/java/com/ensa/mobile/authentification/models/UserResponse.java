package com.ensa.mobile.authentification.models;

public class UserResponse {
    private Long id;
    private String email;
    private String firstname;
    private String lastname;
    private String role;

    // Nouveaux champs pour l'étudiant
    private String classe;   // ex: "CI1"
    private String filiere;  // ex: "INDUS"
    private String niveau;   // ex: "S6" ou "L3", selon ton backend

    public UserResponse() {
    }

    public UserResponse(Long id, String email, String firstname, String lastname, String role,
                        String classe, String filiere, String niveau) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
        this.classe = classe;
        this.filiere = filiere;
        this.niveau = niveau;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // --- Getters et setters pour les infos étudiants ---
    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getFiliere() {
        return filiere;
    }

    public void setFiliere(String filiere) {
        this.filiere = filiere;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }
}
