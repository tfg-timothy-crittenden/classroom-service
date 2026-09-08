package com.timcritt.tfg.application.port.outbound;

public interface IntegrationOutboxPort {

    void append(IntegrationOutboxMessage message);
}

