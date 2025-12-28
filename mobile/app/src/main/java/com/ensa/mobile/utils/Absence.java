package com.ensa.mobile.utils;

public class Absence {

    public String matiere;
    public String date;
    public String heure;
    public String etat; // NON_JUSTIFIEE, EN_ATTENTE, VALIDEE

    public Absence(String matiere, String date, String heure, String etat) {
        this.matiere = matiere;
        this.date = date;
        this.heure = heure;
        this.etat = etat;
    }
}
