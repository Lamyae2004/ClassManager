package com.ensa.mobile.emploitemps.models;

public class StudentForActionDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email; // <- doit correspondre au backend
    private String apogeeNumber;
    private String filiere;
    private String niveau;

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getApogeeNumber() { return apogeeNumber; }
    public void setApogeeNumber(String apogeeNumber) { this.apogeeNumber = apogeeNumber; }

    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }
}
