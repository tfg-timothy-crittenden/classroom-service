package com.timcritt.tfg.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, InternalApiKeyFilter internalApiKeyFilter) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/internal/**").permitAll()
                        .requestMatchers("/api/classrooms").authenticated()
                        .requestMatchers("/api/classrooms/*").authenticated()
                        .requestMatchers("/api/classrooms/*/members/teachers").authenticated()
                        .requestMatchers("/api/classrooms/*/members/students").authenticated()
                        .requestMatchers("/api/classrooms/*/members/*/role").authenticated()
                        .requestMatchers("/api/classrooms/*/members/*").authenticated()
                        .requestMatchers("/api/classrooms/*/materials").authenticated()
                        .requestMatchers("/api/classrooms/*/materials/role/*").authenticated()
                        .requestMatchers("/api/classrooms/*/assign-teacher").authenticated()
                        .requestMatchers("/api/classrooms/*/teachers").authenticated()
                        .requestMatchers("/api/classrooms/*/join-code").authenticated()
                        .requestMatchers("/api/classrooms/batch").authenticated()
                        .requestMatchers("/api/classrooms/member/*").authenticated()
                        .requestMatchers("/api/classrooms/summary/member/*").authenticated()
                        .requestMatchers("/api/classrooms/join").authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(internalApiKeyFilter, BearerTokenAuthenticationFilter.class)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        String secret = "change-me-change-me-change-me-change-me";
        SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}