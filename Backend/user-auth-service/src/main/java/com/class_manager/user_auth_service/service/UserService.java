package com.class_manager.user_auth_service.service;

import com.class_manager.user_auth_service.model.dto.*;
import com.class_manager.user_auth_service.model.entity.Filiere;
import com.class_manager.user_auth_service.model.entity.Niveau;
import com.class_manager.user_auth_service.model.entity.Student;
import com.class_manager.user_auth_service.model.entity.Teacher;
import com.class_manager.user_auth_service.repository.AdminRepository;
import com.class_manager.user_auth_service.repository.StudentRepository;
import com.class_manager.user_auth_service.repository.TeacherRepository;
import com.class_manager.user_auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final AdminRepository adminRepo;



    public TeacheremploiDTO getTeacherById(Long id) {
        Teacher teacher = teacherRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Prof non trouvé avec id : " + id));
        return toDto(teacher);
    }

    private TeacheremploiDTO toDto(Teacher teacher) {
        return new TeacheremploiDTO(teacher.getId(), teacher.getLastname(), teacher.getFirstname(), teacher.getEmail(),teacher.getSpeciality());
    }


    public StudentClasseDTO getClasseInfoByStudentId(Long studentId) {

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Étudiant introuvable"));

        String niveau = student.getNiveau().name();
        String filiere = student.getFiliere().name();
        String classe = niveau + "-" + filiere;

        return new StudentClasseDTO(niveau, filiere, classe);
    }



    public List<Map<String, Object>> getAllClassesWithId() {
        List<Object[]> classesRaw = userRepo.countStudentsPerClass(); // ex: { "CP1INFO", 30 }
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : classesRaw) {
            String className = row[0].toString(); // ex: "CP1INFO"

            // Extraire le Niveau
            Niveau niveau = null;
            for (Niveau n : Niveau.values()) {
                if (className.startsWith(n.name())) {
                    niveau = n;
                    break;
                }
            }

            // Extraire la Filiere
            Filiere filiere = null;
            for (Filiere f : Filiere.values()) {
                if (className.endsWith(f.name())) {
                    filiere = f;
                    break;
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("id", className); // ou générer un id si tu veux
            map.put("nom", className); // ex: "CP1INFO"
            map.put("niveau", niveau != null ? niveau.name() : "UNKNOWN");
            map.put("filiere", filiere != null ? filiere.name() : "NONE");

            result.add(map);
        }

        return result;
    }

    public int countStudentsByClassAndFiliere(
            String niveauStr,   // ex: "CI2"
            String filiereStr,  // ex: "MECA"
            boolean activated
    ) {
        Niveau niveau;
        Filiere filiere;

        try {
            niveau = Niveau.valueOf(niveauStr);    // conversion string -> enum
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Niveau inconnu : " + niveauStr);
        }

        try {
            filiere = Filiere.valueOf(filiereStr); // conversion string -> enum
        } catch (IllegalArgumentException e) {
            filiere = Filiere.NONE; // par défaut si filière invalide
        }

        return userRepo.countByNiveauAndFiliereAndActivated(
                niveau,
                filiere,
                activated
        );
    }


    public List<Student> getStudentsByClasse(Filiere filiere, Niveau niveau) {
        return studentRepo.findByNiveauAndFiliere( niveau,filiere);
    }



    public List<StudentDto> getAllStudents() {
        return studentRepo.findAll()
                .stream()
                .map(student -> UserMapper.toStudentDto(student))
                .toList();
    }
    public List<StudentDto> getStudentsByNiveauAndFiliere(Niveau niveau, Filiere filiere){
        return studentRepo.findByNiveauAndFiliere(niveau,filiere)
                .stream()
                .map(student -> UserMapper.toStudentDto(student))
                .toList();
    }
    public List<TeacherDto> getAllTeachers() {
        return teacherRepo.findAll()
                .stream()
                .map(UserMapper::toTeacherDto)
                .toList();
    }
    public List<AdminDto> getAllAdmins() {
        return adminRepo.findAll()
                .stream()
                .map(UserMapper::toAdminDto)
                .toList();
    }

    public StudentDto getStudentById(Long studentId) {
        return studentRepo.findById(studentId)
                .map(UserMapper::toStudentDto)
                .orElseThrow(() ->
                        new RuntimeException("Étudiant introuvable"));
    }

    public StudentDto getRandomStudent(Niveau niveau, Filiere filiere) {
        List<Student> students =
                studentRepo.findByNiveauAndFiliere(niveau, filiere);

        if (students.isEmpty()) {
            throw new IllegalArgumentException("Aucun étudiant disponible pour cette filière et ce niveau");
        }

        Student random =
                students.get(new Random().nextInt(students.size()));

        return UserMapper.toStudentDto(random);
    }
}
