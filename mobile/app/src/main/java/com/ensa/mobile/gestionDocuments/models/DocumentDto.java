package com.ensa.mobile.gestionDocuments.models;
public class DocumentDto {
    private String nom;
    private String type;     // Cours, TP, TD
    private Long matiereId;
    private  Long profId;
    private Long classeId;
    private String url;

    public DocumentDto(String nom, String type, Long matiere,Long profId, Long classe, String url) {
        this.nom = nom;
        this.type = type;
        this.matiereId = matiere;
        this.classeId = classe;
        this.url = url;
    }

    // Getters
    public String getNom() { return nom; }
    public String getType() { return type; }
    public Long getMatiere() { return matiereId; }
    public Long getClasse() { return classeId; }
    public String getUrl() { return url; }
}