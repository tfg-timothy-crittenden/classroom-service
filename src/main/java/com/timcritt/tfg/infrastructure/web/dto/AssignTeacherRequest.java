package com.timcritt.tfg.infrastructure.web.dto;

import lombok.Data;

@Data
public class AssignTeacherRequest {
    private Long userId;
    private String teacherName;
    private String teacherSurname;



}

