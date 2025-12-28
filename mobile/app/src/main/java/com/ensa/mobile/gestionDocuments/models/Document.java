package com.ensa.mobile.gestionDocuments.models;

public class Document {
    private String nom;
    private String type;     // Cours, TP, TD

    private Long matiereId;
    private Long classeId;
    private Long profId;

    private String url;

    public Document(String nom, String type, Long matiereId, Long classeId,Long profId, String url) {
        this.nom = nom;
        this.type = type;
        this.matiereId = matiereId;
        this.classeId = classeId;
        this.profId = profId;
        this.url = url;
    }

    // Getters
    public String getNom() { return nom; }
    public String getType() { return type; }
    public Long getMatiereId() { return matiereId; }
    public Long getClasseId() { return classeId; }
    public String getUrl() { return url; }
}
