package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.domain.model.ClassroomRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClassroomAuthorizationServiceTest {

    private final MemberRoleServiceAdapter memberRoleService = new MemberRoleServiceAdapter(null) {
        @Override
        public Optional<ClassroomRole> getRoleInClassroom(Long classroomId, Long userId) {
            if (Long.valueOf(7L).equals(classroomId) && Long.valueOf(42L).equals(userId)) {
                return Optional.of(ClassroomRole.STUDENT);
            }
            if (Long.valueOf(7L).equals(classroomId) && Long.valueOf(43L).equals(userId)) {
                return Optional.of(ClassroomRole.TEACHER);
            }
            return Optional.empty();
        }
    };

    private final ClassroomAuthorizationService authorizationService = new ClassroomAuthorizationService(memberRoleService);

    @Test
    void allowsMemberToReadTeachers() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("42", Map.of("name", "John"));

        assertDoesNotThrow(() -> authorizationService.ensureCanReadTeachers(authentication, 7L));
    }

    @Test
    void allowsAdminWithoutMembership() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN")
        );

        assertDoesNotThrow(() -> authorizationService.ensureCanReadTeachers(authentication, 7L));
    }

    @Test
    void allowsTeacherToReadStudents() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("43", Map.of("name", "Jane"));

        assertDoesNotThrow(() -> authorizationService.ensureCanReadStudents(authentication, 7L));
    }

    @Test
    void throwsForbiddenWhenStudentTriesToReadStudents() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("42", Map.of("name", "John"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authorizationService.ensureCanReadStudents(authentication, 7L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void allowsUserToReadOwnRole() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("42", Map.of("name", "John"));

        assertDoesNotThrow(() -> authorizationService.ensureCanReadMemberRole(authentication, 42L));
    }

    @Test
    void allowsAdminToReadAnotherUsersRole() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN")
        );

        assertDoesNotThrow(() -> authorizationService.ensureCanReadMemberRole(authentication, 42L));
    }

    @Test
    void throwsForbiddenWhenUserTriesToReadAnotherUsersRole() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("99", Map.of("name", "John"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authorizationService.ensureCanReadMemberRole(authentication, 42L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void allowsUserToReadOwnClassrooms() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("42", Map.of("name", "John"));

        assertDoesNotThrow(() -> authorizationService.ensureCanReadMemberClassrooms(authentication, 42L));
    }

    @Test
    void allowsAdminToReadMemberClassrooms() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN")
        );

        assertDoesNotThrow(() -> authorizationService.ensureCanReadMemberSummaries(authentication, 42L));
    }

    @Test
    void throwsForbiddenWhenUserTriesToReadAnotherMembersSummaries() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("99", Map.of("name", "John"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authorizationService.ensureCanReadMemberSummaries(authentication, 42L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void allowsTeacherToReadJoinCode() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("43", Map.of("name", "Jane"));

        assertDoesNotThrow(() -> authorizationService.ensureCanReadJoinCode(authentication, 7L));
    }

    @Test
    void throwsForbiddenWhenStudentTriesToReadJoinCode() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("42", Map.of("name", "John"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authorizationService.ensureCanReadJoinCode(authentication, 7L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void allowsTeacherToReadStudentMaterials() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("43", Map.of("name", "Jane"));

        assertDoesNotThrow(() -> authorizationService.ensureCanReadMaterials(authentication, 7L, ClassroomRole.STUDENT));
    }

    @Test
    void allowsStudentToReadStudentMaterials() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("42", Map.of("name", "John"));

        assertDoesNotThrow(() -> authorizationService.ensureCanReadMaterials(authentication, 7L, ClassroomRole.STUDENT));
    }

    @Test
    void throwsForbiddenWhenStudentTriesToReadTeacherMaterials() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("42", Map.of("name", "John"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authorizationService.ensureCanReadMaterials(authentication, 7L, ClassroomRole.TEACHER)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void allowsAdminToManageClassroomResources() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN")
        );

        assertDoesNotThrow(() -> authorizationService.ensureSystemAdmin(authentication));
    }

    @Test
    void allowsUserToRemoveThemselvesFromClassroom() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("42", Map.of("name", "John"));

        assertDoesNotThrow(() -> authorizationService.ensureCanRemoveMember(authentication, 7L, 42L));
    }

    @Test
    void allowsTeacherToRemoveAnotherUserFromClassroom() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("43", Map.of("name", "Jane"));

        assertDoesNotThrow(() -> authorizationService.ensureCanRemoveMember(authentication, 7L, 42L));
    }

    @Test
    void allowsAdminToRemoveAnyUserFromClassroom() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN")
        );

        assertDoesNotThrow(() -> authorizationService.ensureCanRemoveMember(authentication, 7L, 42L));
    }

    @Test
    void throwsForbiddenWhenStudentTriesToRemoveAnotherUser() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("42", Map.of("name", "John"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authorizationService.ensureCanRemoveMember(authentication, 7L, 99L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    @Test
    void throwsUnauthorizedWhenUserIdMissing() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken(null, Map.of("name", "John"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authorizationService.ensureCanReadTeachers(authentication, 7L)
        );

        assertEquals(401, exception.getStatusCode().value());
    }

    @Test
    void throwsForbiddenWhenUserIsNotMember() {
        JwtAuthenticationToken authentication = jwtAuthenticationToken("99", Map.of("name", "John"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authorizationService.ensureCanReadTeachers(authentication, 7L)
        );

        assertEquals(403, exception.getStatusCode().value());
    }

    private JwtAuthenticationToken jwtAuthenticationToken(String userId, Map<String, Object> claims) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60));

        if (userId != null) {
            builder.claim("userId", userId);
        }

        claims.forEach(builder::claim);
        return new JwtAuthenticationToken(builder.build());
    }
}

