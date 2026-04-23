package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.JoinCodeGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomJoinCodeGenerator implements JoinCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateJoinCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int idx = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(idx));
        }
        return sb.toString();
    }
}

