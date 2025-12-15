export const classes = [
  { id: 1, nom: "CP1", id_filiere: 1 },
  { id: 2, nom: "CP2", id_filiere: 1 }
];

export const etudiants = [
  { id_etudiant: 1, nom: "El Amrani", prenom: "Sara", id_classe: 1 },
  { id_etudiant: 2, nom: "Mouline", prenom: "Youssef", id_classe: 1 },
  { id_etudiant: 3, nom: "Fadli", prenom: "Aya", id_classe: 1 },
];

export const creneaux = [
  { id: 1, debut: "08:30", fin: "10:30" },
  { id: 2, debut: "10:45", fin: "12:45" },
  { id: 3, debut: "14:00", fin: "16:00" },
  { id: 4, debut: "16:15", fin: "18:15" },
];

export const matieres = [
  { id_matiere: 1, nom_matiere: "Algorithmique" },
  { id_matiere: 2, nom_matiere: "Analyse" }
];

export const salles = [
  { id_salle: 1, nom_salle: "B1" },
  { id_salle: 5, nom_salle: "B5" }
];


export const absences = [
  { id_absence: 1, id_seance: 1, id_etudiant: 1, present: true, justifie: false, justificatif: null },
  { id_absence: 2, id_seance: 1, id_etudiant: 2, present: false, justifie: true, justificatif: "certificat_medical.pdf" },
  { id_absence: 3, id_seance: 1, id_etudiant: 3, present: true, justifie: false, justificatif: null },
  { id_absence: 4, id_seance: 2, id_etudiant: 1, present: true, justifie: false, justificatif: null },
  { id_absence: 5, id_seance: 2, id_etudiant: 2, present: true, justifie: false, justificatif: null },
  { id_absence: 6, id_seance: 2, id_etudiant: 3, present: false, justifie: true, justificatif: "certificat_medical_aya.pdf" }
];


export const seances = [
  {
    id_seance: 1,
    id_edt: 1,
    date_seance: "2025-12-02"
  },
  {
    id_seance: 2,
    id_edt: 2,
    date_seance: "2025-12-02"
  }
];

export const emploi = [
  {
    id_edt: 1,
    id_classe: 1,
    id_prof: 10,           
    jour: "Lundi",
    id_creneau: 1,
    id_matiere: 1,
    id_salle: 5
  },
  {
    id_edt: 2,
    id_classe: 1,
    id_prof: 10,           
    jour: "Lundi",
    id_creneau: 3,
    id_matiere: 2,
    id_salle: 1
  }
];
