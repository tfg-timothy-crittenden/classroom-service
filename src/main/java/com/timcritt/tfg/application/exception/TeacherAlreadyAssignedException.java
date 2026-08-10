package com.timcritt.tfg.application.exception;

public class TeacherAlreadyAssignedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TeacherAlreadyAssignedException(String message) {
        super(message);
    }
}

