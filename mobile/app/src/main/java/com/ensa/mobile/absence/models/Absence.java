package com.ensa.mobile.absence.models;

public class Absence {

    public String matiere;
    public String date;
    public String heure;
    public String etat; // NON_JUSTIFIEE, EN_ATTENTE, VALIDEE
    public Long id; // Absence ID from backend
    public String fileName; // File name if justification uploaded

    public Absence(String matiere, String date, String heure, String etat) {
        this.matiere = matiere;
        this.date = date;
        this.heure = heure;
        this.etat = etat;
    }

    public Absence(String matiere, String date, String heure, String etat, Long id, String fileName) {
        this.matiere = matiere;
        this.date = date;
        this.heure = heure;
        this.etat = etat;
        this.id = id;
        this.fileName = fileName;
    }
}
