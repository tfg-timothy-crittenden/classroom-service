package com.timcritt.tfg.infrastructure.web;

import com.timcritt.tfg.application.exception.ClassroomNotFoundException;
import com.timcritt.tfg.application.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Spring MVC exception handler — lives in infrastructure/web because it's framework-specific (adapter layer).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, Object> body = Map.of(
                "message", ex.getMessage(),
                "userId", ex.getTestId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ClassroomNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleClassroomNotFound(ClassroomNotFoundException ex) {
        Map<String, Object> body = Map.of(
                "message", ex.getMessage(),
                "classroomId", ex.getClassroomId()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
