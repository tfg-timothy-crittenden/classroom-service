package com.timcritt.tfg.infrastructure.grpc;

import com.timcritt.tfg.application.service.MaterialAccessDecision;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.grpc.protov1.CheckMaterialAccessRequest;
import com.timcritt.tfg.infrastructure.grpc.protov1.CheckMaterialAccessResponse;
import com.timcritt.tfg.infrastructure.grpc.protov1.ClassroomAuthorizationServiceGrpc;
import com.timcritt.tfg.infrastructure.service.MaterialAccessAuthorizationServiceAdapter;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.MetadataUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassroomAuthorizationGrpcServiceTest {

    private static final String API_KEY = "test-api-key";

    private Server server;
    private ManagedChannel channel;

    @AfterEach
    void tearDown() {
        if (channel != null) {
            channel.shutdownNow();
        }
        if (server != null) {
            server.shutdownNow();
        }
    }

    @Test
    void returnsAllowedResponseWhenUserHasAccess() throws Exception {
        startServerWithDecision(new MaterialAccessDecision(true, MaterialAccessDecision.Reason.OK, ClassroomRole.TEACHER));

        CheckMaterialAccessResponse response = stubWithApiKey().checkMaterialAccess(CheckMaterialAccessRequest.newBuilder()
                .setUserId("42")
                .setMaterialId(99L)
                .setAction("READ")
                .build());

        assertTrue(response.getAllowed());
        assertEquals("OK", response.getReason());
        assertEquals("TEACHER", response.getEffectiveRole());
    }

    @Test
    void returnsDeniedResponseWhenUserDoesNotHaveAccess() throws Exception {
        startServerWithDecision(new MaterialAccessDecision(false, MaterialAccessDecision.Reason.ROLE_NOT_ALLOWED, ClassroomRole.STUDENT));

        CheckMaterialAccessResponse response = stubWithApiKey().checkMaterialAccess(CheckMaterialAccessRequest.newBuilder()
                .setUserId("42")
                .setMaterialId(99L)
                .setAction("READ")
                .build());

        assertFalse(response.getAllowed());
        assertEquals("ROLE_NOT_ALLOWED", response.getReason());
        assertEquals("STUDENT", response.getEffectiveRole());
    }

    @Test
    void returnsUnsupportedActionDecision() throws Exception {
        startServerWithDecision(new MaterialAccessDecision(false, MaterialAccessDecision.Reason.UNSUPPORTED_ACTION, null));

        CheckMaterialAccessResponse response = stubWithApiKey().checkMaterialAccess(CheckMaterialAccessRequest.newBuilder()
                .setUserId("42")
                .setMaterialId(99L)
                .setAction("WRITE")
                .build());

        assertFalse(response.getAllowed());
        assertEquals("UNSUPPORTED_ACTION", response.getReason());
        assertEquals("", response.getEffectiveRole());
    }

    @Test
    void rejectsRequestWhenInternalApiKeyIsMissing() throws Exception {
        startServerWithDecision(new MaterialAccessDecision(true, MaterialAccessDecision.Reason.OK, ClassroomRole.STUDENT));

        StatusRuntimeException exception = assertThrows(
                StatusRuntimeException.class,
                () -> rawStub().checkMaterialAccess(CheckMaterialAccessRequest.newBuilder()
                        .setUserId("42")
                        .setMaterialId(99L)
                        .setAction("READ")
                        .build())
        );

        assertEquals(Status.Code.UNAUTHENTICATED, exception.getStatus().getCode());
    }

    private void startServerWithDecision(MaterialAccessDecision decision) throws IOException {
        MaterialAccessAuthorizationServiceAdapter adapter = new MaterialAccessAuthorizationServiceAdapter(null, null) {
            @Override
            public MaterialAccessDecision checkMaterialAccess(String userId, Long materialId, String action) {
                return decision;
            }
        };

        String serverName = "classroom-auth-" + UUID.randomUUID();
        server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(io.grpc.ServerInterceptors.intercept(
                        new ClassroomAuthorizationGrpcService(adapter),
                        new GrpcExceptionHandlingInterceptor(),
                        new GrpcInternalApiKeyInterceptor(API_KEY)
                ))
                .build()
                .start();

        channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();
    }

    private ClassroomAuthorizationServiceGrpc.ClassroomAuthorizationServiceBlockingStub stubWithApiKey() {
        Metadata metadata = new Metadata();
        metadata.put(GrpcInternalApiKeyInterceptor.API_KEY_METADATA_KEY, API_KEY);
        return rawStub().withInterceptors(MetadataUtils.newAttachHeadersInterceptor(metadata));
    }

    private ClassroomAuthorizationServiceGrpc.ClassroomAuthorizationServiceBlockingStub rawStub() {
        return ClassroomAuthorizationServiceGrpc.newBlockingStub(channel);
    }

}

