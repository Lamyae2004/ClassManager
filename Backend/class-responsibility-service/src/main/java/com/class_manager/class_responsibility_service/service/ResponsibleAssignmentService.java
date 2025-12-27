package com.class_manager.class_responsibility_service.service;

import com.class_manager.class_responsibility_service.client.ClasseClient;
import com.class_manager.class_responsibility_service.client.StudentClient;
import com.class_manager.class_responsibility_service.model.Filiere;
import com.class_manager.class_responsibility_service.model.Mapper;
import com.class_manager.class_responsibility_service.model.Niveau;
import com.class_manager.class_responsibility_service.model.dto.ClassDTO;
import com.class_manager.class_responsibility_service.model.dto.ResponsibleAssignmentDto;
import com.class_manager.class_responsibility_service.model.dto.ResponsibleHistoryDto;
import com.class_manager.class_responsibility_service.model.dto.StudentDto;
import com.class_manager.class_responsibility_service.model.entity.ResponsibleAssignment;
import com.class_manager.class_responsibility_service.repository.ResponsibleAssignmentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResponsibleAssignmentService {

    private final ResponsibleAssignmentRepository repository;
    private final StudentClient student;
    private final ClasseClient classeClient;
    public StudentDto assignRandomResponsible(Niveau niveau, Filiere filiere){
        ClassDTO classe;
        try {
            classe = classeClient.getClassByNiveauAndFiliere(
                    niveau.name(),
                    filiere.name()
            );
        } catch (FeignException.BadRequest e) {

            throw new IllegalArgumentException(
                    "Aucune classe trouvée pour ce niveau et cette filière"
            );
        }
        Long classId = classe.getId();
        Optional<ResponsibleAssignment> respo = getActiveAssignmentByClassId(classId);
        if (respo.isPresent()) {
            Long studentId = respo.get().getStudentId();
            return student.getStudentById(studentId);
        }
        StudentDto randomStudent ;
        try {
            randomStudent = student.getRandomStudent(niveau,filiere);
        } catch (FeignException.BadRequest e) {
            throw new IllegalArgumentException(
                    "Aucun étudiant disponible pour cette filière et ce niveau"
            );
        }
            ResponsibleAssignmentDto newResponsible = ResponsibleAssignmentDto.builder()
                    .studentId(randomStudent.getId())
                    .classId(classId)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(3))
                    .active(true)
                    .build();
        repository.save(Mapper.RespAssignDtotoEntity(newResponsible));
        return randomStudent;

    }
    public Optional<ResponsibleAssignment> getActiveAssignmentByClassId(Long classId) {
        Optional<ResponsibleAssignment> assignmentOpt = repository.findByClassIdAndActiveTrue(classId);

        if (assignmentOpt.isPresent()) {
            ResponsibleAssignment assignment = assignmentOpt.get();
            if (assignment.getEndDate() != null && assignment.getEndDate().isBefore(LocalDate.now())) {
                assignment.setActive(false);
                repository.save(assignment);
                return Optional.empty();
            }
        }

        return assignmentOpt;
    }

    public List<ResponsibleHistoryDto> getHistory() {
        return repository.findAll()
                .stream()
                .map(Mapper::RespAssigntoDto)
                .map(dto -> {
                    ClassDTO classe = classeClient.getClasseById(dto.getClassId());
                    StudentDto s = student.getStudentById(dto.getStudentId());

                    return ResponsibleHistoryDto.builder()
                            .assignmentId(dto.getId())
                            .firstname(s.getFirstname())
                            .lastname(s.getLastname())
                            .email(s.getEmail())
                            .classId(dto.getClassId())
                            .niveau(classe.getNom())
                            .filiere(classe.getFiliere())
                            .startDate(dto.getStartDate())
                            .endDate(dto.getEndDate())
                            .active(dto.isActive())
                            .build();
                })
                .toList();


    }
}
