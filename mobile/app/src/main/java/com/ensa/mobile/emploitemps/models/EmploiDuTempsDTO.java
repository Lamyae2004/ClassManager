package com.ensa.mobile.emploitemps.models;

public class EmploiDuTempsDTO {
    private Long id;
    private String classeNom;
    private String matiereNom;
    private String salleNom;
    private String profNom;
    private String jour;
    private String creneauDebut;
    private String creneauFin;
    private String etat; // nouveau champ

    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClasseNom() { return classeNom; }
    public void setClasseNom(String classeNom) { this.classeNom = classeNom; }

    public String getMatiereNom() { return matiereNom; }
    public void setMatiereNom(String matiereNom) { this.matiereNom = matiereNom; }

    public String getSalleNom() { return salleNom; }
    public void setSalleNom(String salleNom) { this.salleNom = salleNom; }

    public String getProfNom() { return profNom; }
    public void setProfNom(String profNom) { this.profNom = profNom; }

    public String getJour() { return jour; }
    public void setJour(String jour) { this.jour = jour; }

    public String getCreneauDebut() { return creneauDebut; }
    public void setCreneauDebut(String creneauDebut) { this.creneauDebut = creneauDebut; }

    public String getCreneauFin() { return creneauFin; }
    public void setCreneauFin(String creneauFin) { this.creneauFin = creneauFin; }
}
