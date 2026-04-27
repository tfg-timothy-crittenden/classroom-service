package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceAssignmentView;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.port.outbound.MemberRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;

import java.util.List;

public class MaterialAccessAuthorizationService {

    private final MaterialReferenceRepositoryPort materialReferenceRepository;
    private final MemberRepositoryPort memberRepository;

    public MaterialAccessAuthorizationService(
            MaterialReferenceRepositoryPort materialReferenceRepository,
            MemberRepositoryPort memberRepository
    ) {
        this.materialReferenceRepository = materialReferenceRepository;
        this.memberRepository = memberRepository;
    }

    public MaterialAccessDecision checkReadAccess(String userId, Long materialId, String action) {
        if (!"READ".equalsIgnoreCase(action)) {
            return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.UNSUPPORTED_ACTION, null);
        }

        Long parsedUserId = parseUserId(userId);
        if (parsedUserId == null || materialId == null) {
            return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.NO_MEMBERSHIP, null);
        }

        List<MaterialReferenceAssignmentView> assignments = materialReferenceRepository.findAssignmentsByMaterialId(materialId);
        if (assignments == null || assignments.isEmpty()) {
            return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.NO_ASSIGNMENT, null);
        }

        ClassroomRole matchedRole = null;
        boolean membershipFound = false;

        for (MaterialReferenceAssignmentView assignment : assignments) {
            if (assignment == null || assignment.classroomId() == null) {
                continue;
            }

            ClassroomRole membershipRole = memberRepository
                    .findRoleByClassroomIdAndUserId(assignment.classroomId(), parsedUserId)
                    .orElse(null);

            if (membershipRole == null) {
                continue;
            }

            membershipFound = true;
            if (matchedRole == null) {
                matchedRole = membershipRole;
            }

            ClassroomRole assignedToRole = assignment.assignedToRole();
            if (assignedToRole == null || assignedToRole == membershipRole) {
                return new MaterialAccessDecision(true, MaterialAccessDecision.Reason.OK, membershipRole);
            }
        }

        if (!membershipFound) {
            return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.NO_MEMBERSHIP, null);
        }

        return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.ROLE_NOT_ALLOWED, matchedRole);
    }

    private Long parseUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

