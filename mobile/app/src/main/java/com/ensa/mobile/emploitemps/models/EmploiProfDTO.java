package com.ensa.mobile.emploitemps.models;

public class EmploiProfDTO {
    private String jour;
    private String creneauDebut;
    private String creneauFin;
    private String matiereNom;
    private String classeNom;
    private String filiere;
    private String salleNom;

    // Constructeurs
    public EmploiProfDTO() {}

    // Getters et Setters
    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }

    public String getCreneauDebut() { return creneauDebut; }
    public void setCreneauDebut(String creneauDebut) { this.creneauDebut = creneauDebut; }

    public String getCreneauFin() { return creneauFin; }
    public void setCreneauFin(String creneauFin) { this.creneauFin = creneauFin; }

    public String getMatiereNom() { return matiereNom; }
    public void setMatiereNom(String matiereNom) { this.matiereNom = matiereNom; }

    public String getClasseNom() { return classeNom; }
    public void setClasseNom(String classeNom) { this.classeNom = classeNom; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public String getSalleNom() { return salleNom; }
    public void setSalleNom(String salleNom) { this.salleNom = salleNom; }
}