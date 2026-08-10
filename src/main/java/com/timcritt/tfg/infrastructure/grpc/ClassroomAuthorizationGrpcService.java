package com.timcritt.tfg.infrastructure.grpc;

import com.timcritt.tfg.application.service.MaterialAccessDecision;
import com.timcritt.tfg.infrastructure.grpc.protov1.CheckMaterialAccessRequest;
import com.timcritt.tfg.infrastructure.grpc.protov1.CheckMaterialAccessResponse;
import com.timcritt.tfg.infrastructure.grpc.protov1.ClassroomAuthorizationServiceGrpc;
import com.timcritt.tfg.infrastructure.service.MaterialAccessAuthorizationServiceAdapter;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassroomAuthorizationGrpcService extends ClassroomAuthorizationServiceGrpc.ClassroomAuthorizationServiceImplBase {

    private final MaterialAccessAuthorizationServiceAdapter materialAccessAuthorizationService;

    @Override
    public void checkMaterialAccess(
            CheckMaterialAccessRequest request,
            StreamObserver<CheckMaterialAccessResponse> responseObserver
    ) {
        log.info("gRPC material access check requested: userId={}, materialId={}, action={}",
                request.getUserId(), request.getMaterialId(), request.getAction());

        try {
            MaterialAccessDecision decision = materialAccessAuthorizationService.checkMaterialAccess(
                    request.getUserId(),
                    request.getMaterialId(),
                    request.getAction()
            );

            if (decision.allowed()) {
                log.info("gRPC material access check allowed: userId={}, materialId={}, role={}",
                        request.getUserId(), request.getMaterialId(), decision.effectiveRole());
            } else {
                log.warn("gRPC material access check denied: userId={}, materialId={}, reason={}, role={}",
                        request.getUserId(), request.getMaterialId(), decision.reason(), decision.effectiveRole());
            }

            responseObserver.onNext(CheckMaterialAccessResponse.newBuilder()
                    .setAllowed(decision.allowed())
                    .setReason(decision.reason().name())
                    .setEffectiveRole(decision.effectiveRole() != null ? decision.effectiveRole().name() : "")
                    .build());
            responseObserver.onCompleted();
        } catch (RuntimeException ex) {
            log.error("gRPC material access check failed: userId={}, materialId={}, action={}",
                    request.getUserId(), request.getMaterialId(), request.getAction(), ex);
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription("Unexpected gRPC server error")
                    .withCause(ex)
                    .asRuntimeException());
        }
    }
}

