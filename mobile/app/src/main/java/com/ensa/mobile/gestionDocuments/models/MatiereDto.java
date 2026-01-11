package com.ensa.mobile.gestionDocuments.models;
public class MatiereDto {
    private Long id;
    private String nom;

    public MatiereDto(Long id, String name) {
        this.id = id;
        this.nom = name;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    @Override
    public String toString() {
        return nom;
    }
}
