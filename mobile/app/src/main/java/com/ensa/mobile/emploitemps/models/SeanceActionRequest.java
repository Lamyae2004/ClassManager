package com.ensa.mobile.emploitemps.models;

public class SeanceActionRequest {
    private Long emploiId;
    private String action; // "RETARD" ou "ANNULER"
    private String motif;  // optionnel

    // Getters et setters
    public Long getEmploiId() { return emploiId; }
    public void setEmploiId(Long emploiId) { this.emploiId = emploiId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}
