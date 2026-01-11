package com.class_manager.Gestion_des_absences.controller;

import com.class_manager.Gestion_des_absences.model.dto.AbsenceResponseDTO;
import com.class_manager.Gestion_des_absences.model.dto.SeanceDTO;
import com.class_manager.Gestion_des_absences.model.entity.Seance;
import com.class_manager.Gestion_des_absences.service.AbsenceService;
import com.class_manager.Gestion_des_absences.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
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
import java.util.List;

@RestController
@RequestMapping("/absences")
@CrossOrigin(origins = {"http://localhost:5173", "*"})
@RequiredArgsConstructor
public class AbsenceController {
    private final SeanceService seanceService;
    private final AbsenceService absenceService;

    @GetMapping("/classes/{classeId}/user/{userId}")
    public ResponseEntity<List<Seance>> getSeancesForUser(
            @PathVariable Long classeId,
            @PathVariable Long userId) {
        List<Seance> seances = seanceService.getSeancesByClasseAndUser(classeId, userId);
        return ResponseEntity.ok(seances);
    }

    @GetMapping("/student/{etudiantId}")
    public ResponseEntity<List<AbsenceResponseDTO>> getAbsencesByStudent(
            @PathVariable Long etudiantId) {
        List<AbsenceResponseDTO> absences = absenceService.getAbsencesByEtudiantId(etudiantId);
        return ResponseEntity.ok(absences);
    }

    @PostMapping("/{absenceId}/justify")
    public ResponseEntity<AbsenceResponseDTO> uploadJustification(
            @PathVariable Long absenceId,
            @RequestParam("file") MultipartFile file) {
        try {
            AbsenceResponseDTO result = absenceService.uploadJustification(absenceId, file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Seance> saveAbsences(@RequestBody SeanceDTO request) {
        Seance seance = seanceService.saveSeanceWithAbsences(request);
        return ResponseEntity.ok(seance);
    }

    @PutMapping("/{absenceId}/justification")
    public ResponseEntity<AbsenceResponseDTO> updateJustificationStatus(
            @PathVariable Long absenceId,
            @RequestParam boolean justifie) {
        try {
            AbsenceResponseDTO result = absenceService.updateJustificationStatus(absenceId, justifie);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/justifications/{filename:.+}")
    public ResponseEntity<Resource> getJustificationFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/justifications/").resolve(filename).normalize();
            Resource resource = new FileSystemResource(filePath);

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
