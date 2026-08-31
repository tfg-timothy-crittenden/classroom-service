package com.timcritt.tfg.application.service;

import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;

public record MaterialAccessDecision(
        boolean allowed,
        Reason reason,
        ClassroomRole effectiveRole
) {
    public enum Reason {
        OK,
        NO_MEMBERSHIP,
        NO_ASSIGNMENT,
        ROLE_NOT_ALLOWED,
        UNSUPPORTED_ACTION
    }
}

