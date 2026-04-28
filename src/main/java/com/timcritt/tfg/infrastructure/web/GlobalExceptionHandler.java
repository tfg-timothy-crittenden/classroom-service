package com.timcritt.tfg.infrastructure.web;

import com.timcritt.tfg.application.service.ClassroomUseCaseImpl.MemberAlreadyInClassroomException;
import com.timcritt.tfg.application.service.ClassroomUseCaseImpl.TeacherAlreadyAssignedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(TeacherAlreadyAssignedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleTeacherAlreadyAssignedException(TeacherAlreadyAssignedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MemberAlreadyInClassroomException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleMemberAlreadyInClassroomException(MemberAlreadyInClassroomException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

}
