package com.timcritt.tfg.authorization;

import com.timcritt.tfg.application.service.MaterialAccessAuthorizationService;
import com.timcritt.tfg.application.service.MaterialAccessDecision;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceAssignmentView;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.port.outbound.MemberRepositoryPort;
import com.timcritt.tfg.domain.model.ClassroomRole;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaterialAccessAuthorizationServiceTest {

    @Test
    void allowsReadWhenMemberRoleMatchesAssignment() {
        MaterialAccessAuthorizationService service = new MaterialAccessAuthorizationService(
                new InMemoryMaterialReferenceRepository(List.of(
                        new MaterialReferenceAssignmentView(10L, 99L, ClassroomRole.STUDENT)
                )),
                new InMemoryMemberRepository(Map.of(
                        new Key(10L, 42L), ClassroomRole.STUDENT
                ))
        );

        MaterialAccessDecision decision = service.checkReadAccess("42", 99L, "READ");

        assertTrue(decision.allowed());
        assertEquals(MaterialAccessDecision.Reason.OK, decision.reason());
        assertEquals(ClassroomRole.STUDENT, decision.effectiveRole());
    }

    @Test
    void returnsNoAssignmentWhenMaterialIsUnknown() {
        MaterialAccessAuthorizationService service = new MaterialAccessAuthorizationService(
                new InMemoryMaterialReferenceRepository(List.of()),
                new InMemoryMemberRepository(Map.of())
        );

        MaterialAccessDecision decision = service.checkReadAccess("42", 99L, "READ");

        assertFalse(decision.allowed());
        assertEquals(MaterialAccessDecision.Reason.NO_ASSIGNMENT, decision.reason());
        assertNull(decision.effectiveRole());
    }

    @Test
    void returnsNoMembershipWhenUserIsNotEnrolledInAnyAssignedClassroom() {
        MaterialAccessAuthorizationService service = new MaterialAccessAuthorizationService(
                new InMemoryMaterialReferenceRepository(List.of(
                        new MaterialReferenceAssignmentView(10L, 99L, ClassroomRole.STUDENT)
                )),
                new InMemoryMemberRepository(Map.of())
        );

        MaterialAccessDecision decision = service.checkReadAccess("42", 99L, "READ");

        assertFalse(decision.allowed());
        assertEquals(MaterialAccessDecision.Reason.NO_MEMBERSHIP, decision.reason());
        assertNull(decision.effectiveRole());
    }

    @Test
    void returnsRoleNotAllowedWhenMembershipRoleDoesNotMatchAssignment() {
        MaterialAccessAuthorizationService service = new MaterialAccessAuthorizationService(
                new InMemoryMaterialReferenceRepository(List.of(
                        new MaterialReferenceAssignmentView(10L, 99L, ClassroomRole.TEACHER)
                )),
                new InMemoryMemberRepository(Map.of(
                        new Key(10L, 42L), ClassroomRole.STUDENT
                ))
        );

        MaterialAccessDecision decision = service.checkReadAccess("42", 99L, "READ");

        assertFalse(decision.allowed());
        assertEquals(MaterialAccessDecision.Reason.ROLE_NOT_ALLOWED, decision.reason());
        assertEquals(ClassroomRole.STUDENT, decision.effectiveRole());
    }

    @Test
    void returnsUnsupportedActionForNonReadRequests() {
        MaterialAccessAuthorizationService service = new MaterialAccessAuthorizationService(
                new InMemoryMaterialReferenceRepository(List.of(
                        new MaterialReferenceAssignmentView(10L, 99L, ClassroomRole.STUDENT)
                )),
                new InMemoryMemberRepository(Map.of(
                        new Key(10L, 42L), ClassroomRole.STUDENT
                ))
        );

        MaterialAccessDecision decision = service.checkReadAccess("42", 99L, "WRITE");

        assertFalse(decision.allowed());
        assertEquals(MaterialAccessDecision.Reason.UNSUPPORTED_ACTION, decision.reason());
        assertNull(decision.effectiveRole());
    }

    @Test
    void allowsReadWhenAssignmentDoesNotRestrictRole() {
        MaterialAccessAuthorizationService service = new MaterialAccessAuthorizationService(
                new InMemoryMaterialReferenceRepository(List.of(
                        new MaterialReferenceAssignmentView(10L, 99L, null)
                )),
                new InMemoryMemberRepository(Map.of(
                        new Key(10L, 42L), ClassroomRole.TEACHER
                ))
        );

        MaterialAccessDecision decision = service.checkReadAccess("42", 99L, "READ");

        assertTrue(decision.allowed());
        assertEquals(MaterialAccessDecision.Reason.OK, decision.reason());
        assertEquals(ClassroomRole.TEACHER, decision.effectiveRole());
    }

    private record Key(Long classroomId, Long userId) {
    }

    private static class InMemoryMaterialReferenceRepository implements MaterialReferenceRepositoryPort {
        private final List<MaterialReferenceAssignmentView> assignments;

        private InMemoryMaterialReferenceRepository(List<MaterialReferenceAssignmentView> assignments) {
            this.assignments = assignments;
        }

        @Override
        public List<com.timcritt.tfg.domain.model.MaterialReference> findByClassroomId(Long classroomId) {
            return List.of();
        }

        @Override
        public List<com.timcritt.tfg.domain.model.MaterialReference> findByClassroomIdAndAssignedToRole(Long classroomId, ClassroomRole role) {
            return List.of();
        }

        @Override
        public List<MaterialReferenceAssignmentView> findAssignmentsByMaterialId(Long materialId) {
            return assignments.stream()
                    .filter(assignment -> assignment.materialId().equals(materialId))
                    .toList();
        }
    }

    private static class InMemoryMemberRepository implements MemberRepositoryPort {
        private final Map<Key, ClassroomRole> rolesByMembership;

        private InMemoryMemberRepository(Map<Key, ClassroomRole> rolesByMembership) {
            this.rolesByMembership = new HashMap<>(rolesByMembership);
        }

        @Override
        public Optional<ClassroomRole> findRoleByClassroomIdAndUserId(Long classroomId, Long userId) {
            return Optional.ofNullable(rolesByMembership.get(new Key(classroomId, userId)));
        }

        @Override
        public int deleteTeacherMembershipsByUserId(Long userId) {
            return 0;
        }
    }
}



