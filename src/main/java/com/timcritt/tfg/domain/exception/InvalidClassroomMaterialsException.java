package com.timcritt.tfg.domain.exception;

public class InvalidClassroomMaterialsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidClassroomMaterialsException(String message) {
        super(message);
    }
}
