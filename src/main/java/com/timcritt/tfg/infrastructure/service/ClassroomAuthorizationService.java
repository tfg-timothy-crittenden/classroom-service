package com.timcritt.tfg.infrastructure.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.timcritt.tfg.domain.model.ClassroomRole;

@Service
public class ClassroomAuthorizationService {

    private final MemberRoleServiceAdapter memberRoleService;

    public ClassroomAuthorizationService(MemberRoleServiceAdapter memberRoleService) {
        this.memberRoleService = memberRoleService;
    }

    public void ensureCanReadTeachers(Authentication authentication, Long classroomId) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        if (isSystemAdmin(authentication)) {
            return;
        }

        Long authenticatedUserId = extractAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user id is missing or invalid");
        }

        if (memberRoleService.getRoleInClassroom(classroomId, authenticatedUserId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this classroom");
        }
    }

    public void ensureCanReadStudents(Authentication authentication, Long classroomId) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        if (isSystemAdmin(authentication)) {
            return;
        }

        Long authenticatedUserId = extractAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user id is missing or invalid");
        }

        ClassroomRole role = memberRoleService.getRoleInClassroom(classroomId, authenticatedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this classroom"));

        if (role != ClassroomRole.TEACHER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only teachers assigned to this classroom can access student members");
        }
    }

    public void ensureCanReadMemberClassrooms(Authentication authentication, Long requestedUserId) {
        ensureSelfOrAdmin(authentication, requestedUserId);
    }

    public void ensureCanReadMemberSummaries(Authentication authentication, Long requestedUserId) {
        ensureSelfOrAdmin(authentication, requestedUserId);
    }

    public void ensureCanReadMemberRole(Authentication authentication, Long requestedUserId) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        if (isSystemAdmin(authentication)) {
            return;
        }

        Long authenticatedUserId = extractAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user id is missing or invalid");
        }

        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the authenticated user can access this resource");
        }
    }

    public void ensureSystemAdmin(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        if (!isSystemAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin privileges required");
        }
    }

    public void ensureCanReadJoinCode(Authentication authentication, Long classroomId) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        if (isSystemAdmin(authentication)) {
            return;
        }

        Long authenticatedUserId = extractAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user id is missing or invalid");
        }

        ClassroomRole role = memberRoleService.getRoleInClassroom(classroomId, authenticatedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this classroom"));

        if (role != ClassroomRole.TEACHER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only teachers assigned to this classroom can access the join code");
        }
    }

    public void ensureCanReadMaterials(Authentication authentication, Long classroomId, ClassroomRole requestedRole) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        if (isSystemAdmin(authentication)) {
            return;
        }

        Long authenticatedUserId = extractAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user id is missing or invalid");
        }

        ClassroomRole memberRole = memberRoleService.getRoleInClassroom(classroomId, authenticatedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this classroom"));

        if (memberRole == ClassroomRole.TEACHER) {
            return;
        }

        if (requestedRole != ClassroomRole.STUDENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student members can only access student materials");
        }
    }

    public void ensureCanRemoveMember(Authentication authentication, Long classroomId, Long targetUserId) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        if (isSystemAdmin(authentication)) {
            return;
        }

        Long authenticatedUserId = extractAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user id is missing or invalid");
        }

        ClassroomRole authenticatedUserRole = memberRoleService.getRoleInClassroom(classroomId, authenticatedUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of this classroom"));

        if (authenticatedUserId.equals(targetUserId)) {
            return;
        }

        if (authenticatedUserRole == ClassroomRole.TEACHER) {
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the member, a classroom teacher, or an admin can remove this user");
    }

    private Long extractAuthenticatedUserId(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            return null;
        }

        Object userIdClaim = jwtAuth.getToken().getClaim("userId");
        if (userIdClaim instanceof Number number) {
            return number.longValue();
        }
        if (userIdClaim != null) {
            try {
                return Long.valueOf(userIdClaim.toString());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private void ensureSelfOrAdmin(Authentication authentication, Long requestedUserId) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        if (isSystemAdmin(authentication)) {
            return;
        }

        Long authenticatedUserId = extractAuthenticatedUserId(authentication);
        if (authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user id is missing or invalid");
        }

        if (!authenticatedUserId.equals(requestedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the authenticated user or an admin can access this resource");
        }
    }

    private boolean isSystemAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        boolean hasAdminAuthority = authentication.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority != null && (
                        authority.equalsIgnoreCase("ROLE_ADMIN")
                                || authority.equalsIgnoreCase("ADMIN")
                                || authority.equalsIgnoreCase("SYSTEM_ADMIN")
                                || authority.equalsIgnoreCase("ROLE_SYSTEM_ADMIN")
                                || authority.equalsIgnoreCase("SCOPE_ADMIN")));
        if (hasAdminAuthority) {
            return true;
        }

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return claimContainsAdmin(jwtAuth.getToken().getClaim("role"))
                    || claimContainsAdmin(jwtAuth.getToken().getClaim("roles"))
                    || claimContainsAdmin(jwtAuth.getToken().getClaim("authorities"))
                    || claimContainsAdmin(jwtAuth.getToken().getClaim("scope"));
        }

        return false;
    }

    private boolean claimContainsAdmin(Object claim) {
        return switch (claim) {
            case null -> false;
            case String value -> matchesAdminValue(value);
            case Iterable<?> values -> {
                for (Object value : values) {
                    if (value != null && matchesAdminValue(value.toString())) {
                        yield true;
                    }
                }
                yield false;
            }
            case Object[] values -> {
                for (Object value : values) {
                    if (value != null && matchesAdminValue(value.toString())) {
                        yield true;
                    }
                }
                yield false;
            }
            default -> matchesAdminValue(claim.toString());
        };
    }

    private boolean matchesAdminValue(String value) {
        if (value == null) {
            return false;
        }

        return value.equalsIgnoreCase("ADMIN")
                || value.equalsIgnoreCase("ROLE_ADMIN")
                || value.equalsIgnoreCase("SYSTEM_ADMIN")
                || value.equalsIgnoreCase("ROLE_SYSTEM_ADMIN")
                || value.equalsIgnoreCase("SCOPE_ADMIN");
    }
}

