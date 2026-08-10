package com.timcritt.tfg.application.exception;

public class MemberAlreadyInClassroomException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MemberAlreadyInClassroomException(String message) {
        super(message);
    }
}

