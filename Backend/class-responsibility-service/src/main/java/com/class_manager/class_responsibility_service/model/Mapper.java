package com.class_manager.class_responsibility_service.model;

import com.class_manager.class_responsibility_service.model.dto.ResponsibleAssignmentDto;
import com.class_manager.class_responsibility_service.model.entity.ResponsibleAssignment;

public class Mapper {
  public static ResponsibleAssignmentDto RespAssigntoDto (ResponsibleAssignment entity){
      return ResponsibleAssignmentDto.builder()
              .id(entity.getId())
              .classId(entity.getClassId())
              .studentId(entity.getStudentId())
              .startDate(entity.getStartDate())
              .endDate(entity.getEndDate())
              .active(entity.isActive())
              .build();
  }
  public  static ResponsibleAssignment RespAssignDtotoEntity (ResponsibleAssignmentDto dto){
      return ResponsibleAssignment.builder()
              .id(dto.getId())
              .classId(dto.getClassId())
              .studentId(dto.getStudentId())
              .startDate(dto.getStartDate())
              .endDate(dto.getEndDate())
              .active(dto.isActive())
              .build();
  }
}
