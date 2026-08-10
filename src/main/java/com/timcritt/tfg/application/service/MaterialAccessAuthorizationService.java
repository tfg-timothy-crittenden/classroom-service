package com.timcritt.tfg.application.service;

import com.timcritt.tfg.application.port.outbound.MaterialReferenceAssignmentView;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.port.outbound.MemberRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;

import java.util.List;

/**
 * Application service responsible for determining whether a user is allowed
 * to access a specific material resource within the classroom context.
 *
 * <p>Access is granted when the user is a member of at least one classroom
 * that has the material assigned, and the assignment's role restriction
 * either is absent or matches the user's role in that classroom.</p>
 */
public class MaterialAccessAuthorizationService {

    private final MaterialReferenceRepositoryPort materialReferenceRepository;
    private final MemberRepositoryPort memberRepository;

    /**
     * Constructs the service with the required repository ports.
     *
     * @param materialReferenceRepository port for querying material-to-classroom assignments
     * @param memberRepository            port for querying classroom memberships
     */
    public MaterialAccessAuthorizationService(
            MaterialReferenceRepositoryPort materialReferenceRepository,
            MemberRepositoryPort memberRepository
    ) {
        this.materialReferenceRepository = materialReferenceRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * Checks whether the given user is allowed to perform a READ operation on the specified material.
     *
     * The decision process is:
     *
     *   Reject immediately if the requested action is not "READ".
     *   Reject if the userId or materialId cannot be resolved.
     *   Look up all classroom assignments for the material.
     *   For each assignment, verify that the user is a member of the assigned classroom.
     *   Grant access if the assignment has no role restriction, or if the restriction
     *       matches the user's role in that classroom.
     *   If the user is a member but no assignment permits access, deny with ROLE_NOT_ALLOWED.
     *   If the user is not a member of any relevant classroom, deny with NO_MEMBERSHIP.
     *
     *
     * @param userId     the raw string identifier of the requesting user
     * @param materialId the ID of the material being accessed
     * @param action     the requested action (only "READ" is supported)
     * @return a {@link MaterialAccessDecision} describing whether access is granted and why
     */
    public MaterialAccessDecision checkReadAccess(String userId, Long materialId, String action) {
        // Only READ is currently supported; reject any other action upfront
        if (!"READ".equalsIgnoreCase(action)) {
            return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.UNSUPPORTED_ACTION, null);
        }

        // Parse and validate both identifiers before touching the database
        Long parsedUserId = parseUserId(userId);
        if (parsedUserId == null || materialId == null) {
            return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.NO_MEMBERSHIP, null);
        }

        // Retrieve all classroom assignments that reference this material
        List<MaterialReferenceAssignmentView> assignments = materialReferenceRepository.findAssignmentsByMaterialId(materialId);
        if (assignments == null || assignments.isEmpty()) {
            // Material is not assigned to any classroom — access is denied
            return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.NO_ASSIGNMENT, null);
        }

        // The first role found for the user across all assignments (used in denial responses)
        ClassroomRole matchedRole = null;
        boolean membershipFound = false;

        for (MaterialReferenceAssignmentView assignment : assignments) {
            // Skip malformed assignment entries
            if (assignment == null || assignment.classroomId() == null) {
                continue;
            }

            // Check whether the user is a member of the classroom this assignment belongs to
            ClassroomRole membershipRole = memberRepository
                    .findRoleByClassroomIdAndUserId(assignment.classroomId(), parsedUserId)
                    .orElse(null);

            if (membershipRole == null) {
                // User is not a member of this particular classroom — try the next assignment
                continue;
            }

            membershipFound = true;

            // Remember the first matched role so we can include it in a denial decision if needed
            if (matchedRole == null) {
                matchedRole = membershipRole;
            }

            ClassroomRole assignedToRole = assignment.assignedToRole();
            // Teachers get access to all materials in their classroom; students only get access to materials assigned to them
            if (assignedToRole == null || assignedToRole == membershipRole || membershipRole == ClassroomRole.TEACHER) {
                return new MaterialAccessDecision(true, MaterialAccessDecision.Reason.OK, membershipRole);
            }
        }

        if (!membershipFound) {
            // User is not a member of any classroom that has this material assigned
            return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.NO_MEMBERSHIP, null);
        }

        // User is a member of a relevant classroom but their role is not permitted by any assignment
        return new MaterialAccessDecision(false, MaterialAccessDecision.Reason.ROLE_NOT_ALLOWED, matchedRole);
    }

    /**
     * Parses a raw user ID string into a {@link Long}.
     *
     * @param userId the string representation of the user ID
     * @return the parsed {@code Long}, or {@code null} if the input is blank or not a valid number
     */
    private Long parseUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException ex) {
            // Non-numeric user ID — treat as unresolvable
            return null;
        }
    }
}
