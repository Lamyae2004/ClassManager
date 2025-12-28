package com.ensa.mobile.gestionDocuments.models;
public class Matiere {
    private Long id;
    private String name;

    public Matiere(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
