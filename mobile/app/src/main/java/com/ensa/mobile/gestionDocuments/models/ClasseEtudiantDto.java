package com.ensa.mobile.gestionDocuments.models;

public class ClasseEtudiantDto {

    private Long classeId;
    private String niveau;
    private String filiere;
    private String classe;

    public Long getClasseId() { return classeId; }

    public void setClasseId(Long classeId) {
        this.classeId = classeId;
    }

    public String getNiveau() { return niveau; }
    public String getFiliere() { return filiere; }
    public String getClasse() { return classe; }
}
