package com.timcritt.tfg.infrastructure.kafka;

/** An incoming integration event cannot be parsed or fails required-field validation. */
public class InvalidIntegrationEventException extends RuntimeException {

    public InvalidIntegrationEventException(String message) {
        super(message);
    }

    public InvalidIntegrationEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
