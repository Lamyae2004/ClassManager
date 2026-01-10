package com.ensa.mobile.gestionDocuments.models;

public class ClasseDto {

    private Long id;
    private String nom;
    private String filiere;

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getFiliere() {
        return filiere;
    }

    // Option pratique
    public String getDisplayName() {
        return nom + " - " + filiere;
    }
}

