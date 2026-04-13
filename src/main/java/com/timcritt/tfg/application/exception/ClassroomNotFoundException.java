package com.timcritt.tfg.application.exception;

public class ClassroomNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final Long classroomId;

    public ClassroomNotFoundException(Long classroomId) {
        super("Classroom not found with id: " + classroomId);
        this.classroomId = classroomId;
    }

    public ClassroomNotFoundException(Long classroomId, String message) {
        super(message);
        this.classroomId = classroomId;
    }

    public Long getClassroomId() {
        return classroomId;
    }
}

