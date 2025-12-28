package com.class_manager.Gestion_des_emplois.service;
import com.class_manager.Gestion_des_emplois.client.TeacherClient;
import com.class_manager.Gestion_des_emplois.model.dto.*;
import com.class_manager.Gestion_des_emplois.model.entity.*;
import com.class_manager.Gestion_des_emplois.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmploiImportService {

    private final ClasseRepository classeRepo;
    private final MatiereRepository matiereRepo;
    private final SalleRepository salleRepo;
    private final CreneauRepository creneauRepo;
    private final EmploiDuTempsRepository edtRepo;
    private final TeacherClient teacherClient;


    public List<MatiereProfDTO> getMatieresEtProfs(
            String niveau,
            Filiere filiere
    ) {

        List<EmploiDuTemps> emplois =
                edtRepo.findByClasseNomAndFiliere(niveau, filiere);

        return emplois.stream()
                .filter(e -> e.getMatiere() != null && e.getProfId() != null)
                .map(e -> {
                    TeacherDTO prof = teacherClient.getTeacherById(e.getProfId());

                    return new MatiereProfDTO(
                            e.getMatiere().getId(),        // ✅ ID de la matière
                            e.getMatiere().getNom(),
                            prof.getLastname(),
                            prof.getFirstname()
                    );
                })
                .distinct()
                .toList();
    }

    public List<Map<String, Object>> getStudentsStatusPerClass(Long profId) {

        List<Classe> classes = edtRepo.findDistinctClassesByProfId(profId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Classe classe : classes) {
            String className = classe.getNom();
            String filiereName = classe.getFiliere().name(); // récupérer la filière

            int active = teacherClient
                    .countStudentsByClass(className, filiereName, true)
                    .get("count");

            int inactive = teacherClient
                    .countStudentsByClass(className, filiereName, false)
                    .get("count");

            Map<String, Object> map = new HashMap<>();
            map.put("classe", className);
            map.put("filiere", filiereName);   // ajout de la filière
            map.put("activeStudents", active);
            map.put("inactiveStudents", inactive);

            result.add(map);
        }

        return result;
    }



    public int getMyClassesCount(Long profId) {
        List<Classe> classes = edtRepo.findDistinctClassesByProfId(profId);
        return classes.size(); // le nombre de classes distinctes
    }

    public List<EmploiProfDTO> getEmploiDuJourForProf(Long profId) {
        // Obtenir le jour actuel en français (ou utiliser exactement le format stocké dans la DB)
        String today = LocalDate.now().getDayOfWeek()
                .getDisplayName(TextStyle.FULL, Locale.FRENCH);
        System.out.println("Today = '" + today + "'");

        return edtRepo.findAll().stream()
                .filter(e -> e.getProfId() != null &&
                        e.getProfId().equals(profId)
                                //&&
                       // e.getJour() != null &&
                        //e.getJour().equalsIgnoreCase(today)
                )
                .map(e -> new EmploiProfDTO(
                        e.getJour(),
                        e.getCreneau() != null ? e.getCreneau().getHeureDebut() : null,
                        e.getCreneau() != null ? e.getCreneau().getHeureFin() : null,
                        e.getMatiere() != null ? e.getMatiere().getNom() : null,
                        e.getClasse() != null ? e.getClasse().getNom() : null,
                        e.getClasse() != null && e.getClasse().getFiliere() != null ?
                                e.getClasse().getFiliere().name() : null,
                        e.getSalle() != null ? e.getSalle().getNom() : null
                ))
                .collect(Collectors.toList());
    }





    public List<EmploiDuTempsDTO> getEmploiByClasse(Long classeId) {
        return edtRepo.findByClasseId(classeId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private EmploiDuTempsDTO toDTO(EmploiDuTemps e) {
        EmploiDuTempsDTO dto = new EmploiDuTempsDTO();

        dto.setId(e.getId());
        dto.setJour(e.getJour());

        dto.setClasseId(e.getClasse().getId());
        dto.setClasseNom(e.getClasse().getNom());

        dto.setMatiereId(e.getMatiere().getId());
        dto.setMatiereNom(e.getMatiere().getNom());

        dto.setSalleId(e.getSalle().getId());
        dto.setSalleNom(e.getSalle().getNom());

        dto.setCreneauId(e.getCreneau().getId());
        dto.setCreneauDebut(e.getCreneau().getHeureDebut());
        dto.setCreneauFin(e.getCreneau().getHeureFin());
        dto.setProfId(e.getProfId());
        return dto;
    }








    public List<Matiere> getMatieresByClasseAndProf(Long classeId, Long profId) {
        return edtRepo.findMatieresByClasseAndProf(classeId, profId);
    }



    public List<Matiere> getMatieresByClasse(Long classeId) {
        return edtRepo.findAll().stream()
                .filter(e -> e.getClasse() != null
                        && e.getClasse().getId().equals(classeId))
                .map(EmploiDuTemps::getMatiere)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }



    public List<EmploiDuTempsDTO> getEmploiByClasseProfJour(
            Long classeId, Long profId, String jour) {

        return edtRepo.findAll()
                .stream()
                .filter(e ->
                        e.getClasse() != null &&
                                e.getClasse().getId().equals(classeId) &&
                                e.getProfId() != null &&
                                e.getProfId().equals(profId) &&
                                e.getJour() != null &&
                                e.getJour().equalsIgnoreCase(jour)
                )
                .map(e -> new EmploiDuTempsDTO(
                        e.getId(),

                        // Classe
                        e.getClasse() != null ? e.getClasse().getId() : null,
                        e.getClasse() != null ? e.getClasse().getNom() : null,

                        // Matière
                        e.getMatiere() != null ? e.getMatiere().getId() : null,
                        e.getMatiere() != null ? e.getMatiere().getNom() : null,

                        // Salle
                        e.getSalle() != null ? e.getSalle().getId() : null,
                        e.getSalle() != null ? e.getSalle().getNom() : null,

                        // Créneau
                        e.getCreneau() != null ? e.getCreneau().getId() : null,
                        e.getCreneau() != null ? e.getCreneau().getHeureDebut() : null,
                        e.getCreneau() != null ? e.getCreneau().getHeureFin() : null,
                        // Prof
                        e.getProfId(),
                        // Jour
                        e.getJour()

                ))
                .toList();
    }


    public void importEmploi(ImportRequest request) {

        Filiere filiere = null;

        if (request.getFiliere() != null && !request.getFiliere().isBlank()) {
            try {
                filiere = Filiere.valueOf(
                        request.getFiliere().toUpperCase()
                );
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(
                        "Filière inconnue : " + request.getFiliere()
                );
            }
        }

        // 2. Vérifier/créer classe (en tenant compte de la filière)
        Classe classe;
        if (filiere != null) {
            // Chercher classe avec nom ET filière
            Filiere finalFiliere = filiere;
            classe = classeRepo.findByNomAndFiliere(request.getClasse(), filiere)
                    .orElseGet(() -> {
                        Classe c = new Classe();
                        c.setNom(request.getClasse());
                        c.setFiliere(finalFiliere);
                        return classeRepo.save(c);
                    });
        } else {
            // Si pas de filière, prendre la première classe avec ce nom
            List<Classe> classes = classeRepo.findByNom(request.getClasse());
            if (classes.isEmpty()) {
                Classe c = new Classe();
                c.setNom(request.getClasse());
                classe = classeRepo.save(c);
            } else {
                classe = classes.get(0);
            }
        }

        // 3. Pour chaque ligne d'emploi importée
        for (EmploiImportDTO dto : request.getEmplois()) {

            // Créneau
            String[] parts = dto.getCreneau().split("-");
            Creneau creneau = creneauRepo.findByHeureDebutAndHeureFin(parts[0], parts[1])
                    .orElseGet(() -> {
                        Creneau c = new Creneau();
                        c.setHeureDebut(parts[0]);
                        c.setHeureFin(parts[1]);
                        return creneauRepo.save(c);
                    });

            // Matière
            Matiere matiere = matiereRepo.findByNom(dto.getMatiere())
                    .orElseGet(() -> {
                        Matiere m = new Matiere();
                        m.setNom(dto.getMatiere());
                        return matiereRepo.save(m);
                    });

            // Prof
            Long profId;

            try {

                String cleanProf = dto.getProf().replaceAll("(?i)^pr\\.?\s*", "").trim();

                String[] names = cleanProf.split("\\s+", 2);
                if (names.length < 2) {
                    throw new RuntimeException("Nom du prof invalide : " + dto.getProf());
                }

                String firstname = names[0].toLowerCase();
                String lastname = names[1].toLowerCase();

                var teacher = teacherClient.getTeacherByFullName(firstname, lastname);
                profId = teacher.getId();

            } catch (Exception e) {
                throw new RuntimeException("Prof introuvable : " + dto.getProf());
            }

            // Salle
            Salle salle = salleRepo.findByNom(dto.getSalle())
                    .orElseGet(() -> {
                        Salle s = new Salle();
                        s.setNom(dto.getSalle());
                        return salleRepo.save(s);
                    });

            // Enregistrer EDT
            EmploiDuTemps edt = new EmploiDuTemps();
            edt.setJour(dto.getJour());
            edt.setClasse(classe);
            edt.setProfId(profId);
            edt.setMatiere(matiere);
            edt.setSalle(salle);
            edt.setCreneau(creneau);
            edt.setSemestre(Semestre.valueOf(dto.getSemestre()));
            edt.setFileName(request.getFileName());
            edtRepo.save(edt);
        }
    }


    public List<EmploiDuTemps> getAllEmplois() {
        return edtRepo.findAll();
    }
    public Optional<EmploiDuTemps> getEmploiById(Long id) {
        return edtRepo.findById(id);
    }




    // ✅ Méthode pour mettre à jour une cellule d'emploi
    @Transactional
    public EmploiDuTemps updateEmploiCell(Long emploiId, EmploiCellUpdateDTO updateDTO) {
        // 1. Récupérer l'emploi existant
        EmploiDuTemps emploi = edtRepo.findById(emploiId)
                .orElseThrow(() -> new RuntimeException("Emploi non trouvé avec l'ID: " + emploiId));

        // 2. Mettre à jour les champs selon ce qui est fourni dans le DTO

        // Mise à jour de la matière
        if (updateDTO.getMatiere() != null && !updateDTO.getMatiere().isEmpty()) {
            Matiere matiere = matiereRepo.findByNom(updateDTO.getMatiere())
                    .orElseGet(() -> {
                        Matiere m = new Matiere();
                        m.setNom(updateDTO.getMatiere());
                        return matiereRepo.save(m);
                    });
            emploi.setMatiere(matiere);
        }

        // ===== Mise à jour du prof =====
        if (updateDTO.getProf() != null && !updateDTO.getProf().isBlank()) {
            try {
                // Nettoyage "Pr. Ahmed Benali"
                String cleanProf = updateDTO.getProf()
                        .replace("Pr.", "")
                        .replace("pr.", "")
                        .trim();

                String[] names = cleanProf.split("\\s+", 2);
                if (names.length < 2) {
                    throw new RuntimeException("Nom du prof invalide : " + updateDTO.getProf());
                }

                String firstname = names[0];
                String lastname = names[1];

                var teacher = teacherClient.getTeacherByFullName(firstname, lastname);
                emploi.setProfId(teacher.getId());

            } catch (Exception e) {
                throw new RuntimeException("Prof introuvable : " + updateDTO.getProf());
            }
        }


        // Mise à jour de la salle
        if (updateDTO.getSalle() != null && !updateDTO.getSalle().isEmpty()) {
            Salle salle = salleRepo.findByNom(updateDTO.getSalle())
                    .orElseGet(() -> {
                        Salle s = new Salle();
                        s.setNom(updateDTO.getSalle());
                        return salleRepo.save(s);
                    });
            emploi.setSalle(salle);
        }

        // Mise à jour du créneau
        if (updateDTO.getCreneau() != null && !updateDTO.getCreneau().isEmpty()) {
            String[] parts = updateDTO.getCreneau().split("-");
            if (parts.length == 2) {
                Creneau creneau = creneauRepo.findByHeureDebutAndHeureFin(parts[0], parts[1])
                        .orElseGet(() -> {
                            Creneau c = new Creneau();
                            c.setHeureDebut(parts[0]);
                            c.setHeureFin(parts[1]);
                            return creneauRepo.save(c);
                        });
                emploi.setCreneau(creneau);
            }
        }

        // Mise à jour du jour
        if (updateDTO.getJour() != null && !updateDTO.getJour().isEmpty()) {
            emploi.setJour(updateDTO.getJour());
        }

        // 3. Sauvegarder les modifications
        return edtRepo.save(emploi);
    }


    public void deleteEmploi(Long id) {
        edtRepo.deleteById(id);
    }




    // ✅ Nouvelle méthode pour supprimer par classe/filière/semestre
    public void deleteEmploisByGroup(String className, String filiereName, String semester) {
        List<EmploiDuTemps> emplois = edtRepo.findAll();

        emplois.stream()
                .filter(e -> {
                    boolean matchClass = e.getClasse() != null &&
                            className.equalsIgnoreCase(e.getClasse().getNom());

                    boolean matchFiliere = (filiereName == null || filiereName.isEmpty()) ?
                            (e.getClasse() == null || e.getClasse().getFiliere() == null) :
                            (e.getClasse() != null &&
                                    e.getClasse().getFiliere() != null &&
                                    filiereName.equalsIgnoreCase(e.getClasse().getFiliere().name()));

                    boolean matchSemester = (semester == null || semester.isEmpty()) ?
                            (e.getSemestre() == null || e.getSemestre().isEmpty()) :
                            semester.equalsIgnoreCase(String.valueOf(e.getSemestre()));

                    return matchClass && matchFiliere && matchSemester;
                })
                .forEach(e -> edtRepo.delete(e));
    }


    // ✅ Nouvelle méthode pour récupérer par groupe (classe/filière/semestre)
    public List<EmploiDuTemps> getEmploisByGroup(String className, String filiereName, String semester) {
        List<EmploiDuTemps> emplois = edtRepo.findAll();

        return emplois.stream()
                .filter(e -> {
                    boolean matchClass = e.getClasse() != null &&
                            className.equalsIgnoreCase(e.getClasse().getNom());

                    boolean matchFiliere = (filiereName == null || filiereName.isEmpty()) ?
                            (e.getClasse() == null || e.getClasse().getFiliere() == null) :
                            (e.getClasse() != null &&
                                    e.getClasse().getFiliere() != null &&
                                    filiereName.equalsIgnoreCase(e.getClasse().getFiliere().name()));

                    boolean matchSemester = (semester == null || semester.isEmpty()) ?
                            true :
                            (e.getSemestre() != null &&
                                    semester.equalsIgnoreCase(e.getSemestre().name()));

                    return matchClass && matchFiliere && matchSemester;
                })
                .toList();
    }


    // ...existing code...
    @Transactional
    public EmploiDuTemps createEmploi(EmploiDuTemps emploi) {
        // Classe (recherche par id si fourni, sinon par nom, sinon création)
        Classe classe = null;
        if (emploi.getClasse() != null) {
            if (emploi.getClasse().getId() != null) {
                classe = classeRepo.findById(emploi.getClasse().getId()).orElse(null);
            }
            if (classe == null && emploi.getClasse().getNom() != null) {
                List<Classe> found = classeRepo.findByNom(emploi.getClasse().getNom());
                if (!found.isEmpty()) {
                    classe = found.get(0);
                } else {
                    classe = new Classe();
                    classe.setNom(emploi.getClasse().getNom());
                    if (emploi.getClasse().getFiliere() != null) {
                        classe.setFiliere(emploi.getClasse().getFiliere());

                    }else {
                        classe.setFiliere(Filiere.NONE);
                    }
                    classe = classeRepo.save(classe);
                }
            }
        }

        // Matiere
        Matiere matiere = null;
        if (emploi.getMatiere() != null && emploi.getMatiere().getNom() != null && !emploi.getMatiere().getNom().isBlank()) {
            matiere = matiereRepo.findByNom(emploi.getMatiere().getNom())
                    .orElseGet(() -> {
                        Matiere m = new Matiere();
                        m.setNom(emploi.getMatiere().getNom());
                        return matiereRepo.save(m);
                    });
        }

        // Prof


        // Salle
        Salle salle = null;
        if (emploi.getSalle() != null && emploi.getSalle().getNom() != null && !emploi.getSalle().getNom().isBlank()) {
            salle = salleRepo.findByNom(emploi.getSalle().getNom())
                    .orElseGet(() -> {
                        Salle s = new Salle();
                        s.setNom(emploi.getSalle().getNom());
                        return salleRepo.save(s);
                    });
        }

        // Creneau
        Creneau creneau = null;
        if (emploi.getCreneau() != null && emploi.getCreneau().getHeureDebut() != null) {
            String hd = emploi.getCreneau().getHeureDebut();
            String hf = emploi.getCreneau().getHeureFin();
            creneau = creneauRepo.findByHeureDebutAndHeureFin(hd, hf)
                    .orElseGet(() -> {
                        Creneau c = new Creneau();
                        c.setHeureDebut(hd);
                        c.setHeureFin(hf);
                        return creneauRepo.save(c);
                    });
        }

        // Construire et sauvegarder EmploiDuTemps
        EmploiDuTemps newEmploi = new EmploiDuTemps();
        newEmploi.setClasse(classe);
        newEmploi.setMatiere(matiere);
        newEmploi.setSalle(salle);
        newEmploi.setCreneau(creneau);
        if (emploi.getJour() != null) newEmploi.setJour(emploi.getJour());
        if (emploi.getSemestre() != null) newEmploi.setSemestre(emploi.getSemestre());
        if (emploi.getFileName() != null) newEmploi.setFileName(emploi.getFileName());
        newEmploi.setProfId(emploi.getProfId());
        return edtRepo.save(newEmploi);
    }
// ...existing code...


    public List<EmploiDuTempsDTO> getEmploiForStudent(
            String classeName,
            String filiereName,
            String semester
    ) {

        return edtRepo.findAll()
                .stream()
                .filter(e -> {
                    boolean matchClasse =
                            e.getClasse() != null &&
                                    classeName.equalsIgnoreCase(e.getClasse().getNom());

                    boolean matchFiliere =
                            e.getClasse() != null &&
                                    e.getClasse().getFiliere() != null &&
                                    filiereName.equalsIgnoreCase(e.getClasse().getFiliere().name());

                    boolean matchSemester =
                            semester == null || semester.isEmpty() ||
                                    (e.getSemestre() != null &&
                                            semester.equalsIgnoreCase(e.getSemestre().name()));

                    return matchClasse && matchFiliere && matchSemester;
                })
                .map(this::toDTO)
                .toList();
    }




}