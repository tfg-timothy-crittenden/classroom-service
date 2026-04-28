package com.timcritt.tfg.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void convertsRolesClaimIntoGrantedAuthorities() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claim("roles", List.of("ROLE_STUDENT", "ROLE_ADMIN", "ROLE_TEACHER"))
                .build();

        Set<String> authorities = securityConfig.jwtAuthenticationConverter()
                .convert(jwt)
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("ROLE_STUDENT"));
        assertTrue(authorities.contains("ROLE_TEACHER"));
    }
}

