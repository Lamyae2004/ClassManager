package com.ensa.mobile.absence.models;

import com.google.gson.annotations.SerializedName;

public class AbsenceResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("etudiantId")
    private Long etudiantId;

    @SerializedName("seanceId")
    private Long seanceId;

    @SerializedName("present")
    private boolean present;

    @SerializedName("justifie")
    private boolean justifie;

    @SerializedName("filePath")
    private String filePath;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("date")
    private String date;

    @SerializedName("matiereId")
    private Long matiereId;

    @SerializedName("creneauId")
    private Long creneauId;

    @SerializedName("heureDebut")
    private String heureDebut;

    @SerializedName("heureFin")
    private String heureFin;

    @SerializedName("matiereNom")
    private String matiereNom;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(Long etudiantId) {
        this.etudiantId = etudiantId;
    }

    public Long getSeanceId() {
        return seanceId;
    }

    public void setSeanceId(Long seanceId) {
        this.seanceId = seanceId;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public boolean isJustifie() {
        return justifie;
    }

    public void setJustifie(boolean justifie) {
        this.justifie = justifie;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Long getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(Long matiereId) {
        this.matiereId = matiereId;
    }

    public Long getCreneauId() {
        return creneauId;
    }

    public void setCreneauId(Long creneauId) {
        this.creneauId = creneauId;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }

    public String getMatiereNom() {
        return matiereNom;
    }

    public void setMatiereNom(String matiereNom) {
        this.matiereNom = matiereNom;
    }

    // Helper method to get status string
    public String getStatusString() {
        if (justifie) {
            return "VALIDEE";
        } else if (filePath != null && !filePath.isEmpty()) {
            return "EN_ATTENTE";
        } else {
            return "NON_JUSTIFIEE";
        }
    }

    // Helper method to format date
    public String getFormattedDate() {
        if (date == null) return "";
        // Assuming date is in format "YYYY-MM-DD"
        String[] parts = date.split("-");
        if (parts.length == 3) {
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        }
        return date;
    }

    // Helper method to get time range
    public String getTimeRange() {
        if (heureDebut != null && heureFin != null) {
            return heureDebut + " - " + heureFin;
        }
        return "";
    }
}

