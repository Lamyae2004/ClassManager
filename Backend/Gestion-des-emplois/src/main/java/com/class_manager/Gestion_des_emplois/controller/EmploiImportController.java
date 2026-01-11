package com.class_manager.Gestion_des_emplois.controller;

import com.class_manager.Gestion_des_emplois.client.TeacherClient;
import com.class_manager.Gestion_des_emplois.client.EtudiantClient;
import com.class_manager.Gestion_des_emplois.model.dto.*;
import com.class_manager.Gestion_des_emplois.model.entity.*;
import com.class_manager.Gestion_des_emplois.service.EmploiImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/emploi")
@RequiredArgsConstructor
public class EmploiImportController {

    private final EmploiImportService emploiService;
    private static final String EMPLOI_DIR = "uploads/emplois/";
    private final TeacherClient teacherClient ;
    private final EtudiantClient etudiantClient;


    @GetMapping("/classes/matieres-profs")
    public ResponseEntity<List<MatiereProfDTO>> getMatieresEtProfs(
            @RequestParam String niveau,
            @RequestParam Filiere filiere
    ) {
        return ResponseEntity.ok(
                emploiService.getMatieresEtProfs(niveau, filiere)
        );
    }


    @GetMapping("/classe/{classeId}")
    public List<EmploiDuTempsDTO> getEmploiByClasse(@PathVariable Long classeId) {
        return emploiService.getEmploiByClasse(classeId);
    }

    @GetMapping("/students-status/{profId}")
    public ResponseEntity<List<Map<String, Object>>> getStudentsStatusByClass(
            @PathVariable Long profId) {

        return ResponseEntity.ok(
                emploiService.getStudentsStatusPerClass(profId)
        );
    }

    @GetMapping("/my-classes/{profId}")
    public ResponseEntity<Map<String, Integer>> getMyClasses(@PathVariable Long profId) {
        int myClasses = emploiService.getMyClassesCount(profId);
        Map<String, Integer> response = new HashMap<>();
        response.put("myClasses", myClasses);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/prof/{id}")
    public List<EmploiProfDTO> getEmploiProf(@PathVariable("id") Long profId) {
        return emploiService.getEmploiDuJourForProf(profId);
    }


    @GetMapping("/classe/{classeId}/prof/{profId}/jour/{jour}")

    public List<EmploiDuTempsDTO> getEmploiByClasseProfJour(
            @PathVariable Long classeId,
            @PathVariable Long profId,
            @PathVariable String jour
    ) {
        return emploiService.getEmploiByClasseProfJour(classeId, profId, jour);
    }



    @PostMapping("/import")
    public ResponseEntity<String> importEmploi(@RequestBody ImportRequest request) {
        emploiService.importEmploi(request);
        return ResponseEntity.ok("Emploi du temps importé avec succès");
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadEmploi(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Fichier manquant");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Files.createDirectories(Paths.get(EMPLOI_DIR));

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // ✅ Utiliser le nom original pour la sauvegarde
            String savedFilename = originalFilename;

            Path target = Paths.get(EMPLOI_DIR).resolve(savedFilename).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            Map<String, String> response = new HashMap<>();
            response.put("fileName", savedFilename);
            response.put("originalName", originalFilename);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Erreur sauvegarde fichier");
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping()
    public ResponseEntity<List<EmploiDuTemps>> getAllEmplois() {
        List<EmploiDuTemps> emplois = emploiService.getAllEmplois();
        return ResponseEntity.ok(emplois);
    }

    // ...existing code...
    @PostMapping("/create")
    public ResponseEntity<EmploiDuTemps> createEmploi(@RequestBody EmploiDuTemps emploi) {
        try {
            EmploiDuTemps saved = emploiService.createEmploi(emploi);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
// ...existing code...

    // ✅ IMPORTANT : Définir /group AVANT /{id} pour éviter la confusion de routing
    @GetMapping("/group")
    public ResponseEntity<List<EmploiDuTemps>> getEmploiGroup(
            @RequestParam String classe,
            @RequestParam(required = false) String filiere,
            @RequestParam(required = false) String semester) {
        List<EmploiDuTemps> emplois = emploiService.getEmploisByGroup(classe, filiere, semester);
        return ResponseEntity.ok(emplois);
    }

    // ✅ Téléchargement du fichier
    @GetMapping("/{filename:.+}/file")
    public ResponseEntity<Resource> downloadEmploi(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(EMPLOI_DIR).resolve(filename).normalize();
            UrlResource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Détecter le type de contenu
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // ✅ Supprimer un emploi individuel (pour l'édition future)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmploi(@PathVariable Long id) {
        emploiService.deleteEmploi(id);
        return ResponseEntity.noContent().build();
    }




    // ✅ Mettre à jour une cellule d'emploi existant
    @PutMapping("/{id}/cell")
    public ResponseEntity<EmploiDuTemps> updateEmploiCell(
            @PathVariable Long id,
            @RequestBody EmploiCellUpdateDTO updateDTO) {

        try {
            EmploiDuTemps updatedEmploi = emploiService.updateEmploiCell(id, updateDTO);
            return ResponseEntity.ok(updatedEmploi);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ✅ Optionnel : Récupérer un emploi spécifique pour l'édition
   /* @GetMapping("/{id}")
    public ResponseEntity<EmploiDuTemps> getEmploiById(@PathVariable Long id) {
        return emploiService.getEmploiById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }*/

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEmploiWithTeacher(@PathVariable Long id) {
        Optional<EmploiDuTemps> emploiOpt = emploiService.getEmploiById(id);

        if (emploiOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        EmploiDuTemps emploi = emploiOpt.get();

        // Récupérer le professeur via Feign
        TeacherDTO teacher = null;
        try {
            teacher = teacherClient.getTeacherById(emploi.getProfId());
        } catch (Exception e) {
            // fallback si erreur
        }

        Map<String, Object> response = new HashMap<>();
        response.put("emploi", emploi);
        if (teacher != null) {
            Map<String, String> profInfo = new HashMap<>();
            profInfo.put("firstname", teacher.getFirstname());
            profInfo.put("lastname", teacher.getLastname());
            response.put("prof", profInfo);
        }

        return ResponseEntity.ok(response);
    }








    @DeleteMapping("/group")
    public ResponseEntity<Void> deleteEmploiGroup(
            @RequestParam String classe,
            @RequestParam(required = false) String filiere,
            @RequestParam(required = false) String semester) {
        emploiService.deleteEmploisByGroup(classe, filiere, semester);
        return ResponseEntity.noContent().build();
    }

    //pour les etudiants emploi de temps


    @GetMapping("/student")
    public List<EmploiDuTempsDTO> getEmploiForStudent(
            @RequestParam String classe,
            @RequestParam String filiere,
            @RequestParam(required = false) String semester
    ) {
        return emploiService.getEmploiForStudent(classe, filiere, semester);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getEmploiByEtudiantId(@PathVariable Long studentId) {

        EtudiantDTO etudiant = teacherClient.getEtudiantById(studentId);
        if (etudiant == null) {
            return ResponseEntity.notFound().build();
        }

        List<EmploiDuTempsDTO> emplois =
                emploiService.getEmploiForStudent(
                        etudiant.getNiveau(),
                        etudiant.getFiliere(),
                        null
                );

        // 🔥 1 appel FEIGN seulement
        List<TeacherDTO> teachers = teacherClient.getAllTeachers();

        // Map profId -> "Nom Prénom"
        Map<Long, String> profMap = teachers.stream()
                .collect(Collectors.toMap(
                        TeacherDTO::getId,
                        t -> t.getFirstname() + " " + t.getLastname()
                ));

        // Enrichissement
        List<EmploiAvecProfResponse> emploisAvecProf = emplois.stream()
                .map(e -> new EmploiAvecProfResponse(
                        e,
                        profMap.getOrDefault(e.getProfId(), "Inconnu")
                ))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("etudiant", etudiant);
        response.put("emploi", emploisAvecProf);

        return ResponseEntity.ok(response);
    }

}