package com.timcritt.tfg.infrastructure.web.controller;

import com.timcritt.tfg.application.service.MaterialAccessDecision;
import com.timcritt.tfg.infrastructure.service.MaterialAccessAuthorizationServiceAdapter;
import com.timcritt.tfg.infrastructure.web.dto.MaterialAccessCheckRequest;
import com.timcritt.tfg.infrastructure.web.dto.MaterialAccessCheckResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/authorization")
@RequiredArgsConstructor
@Slf4j
public class InternalAuthorizationController {

    private final MaterialAccessAuthorizationServiceAdapter materialAccessAuthorizationService;

//    TODO: implement https so that the response to this call cab be trusted
    @PostMapping("/material-access:check")
    public MaterialAccessCheckResponse checkMaterialAccess(@Valid @RequestBody MaterialAccessCheckRequest request) {
        log.info("Internal material access check requested: userId={}, materialId={}, action={}",
                request.getUserId(), request.getMaterialId(), request.getAction());

        MaterialAccessDecision decision = materialAccessAuthorizationService.checkMaterialAccess(
                request.getUserId(),
                request.getMaterialId(),
                request.getAction()
        );

        if (decision.allowed()) {
            log.info("Internal material access check allowed: userId={}, materialId={}, role={}",
                    request.getUserId(), request.getMaterialId(), decision.effectiveRole());
        } else {
            log.warn("Internal material access check denied: userId={}, materialId={}, reason={}, role={}",
                    request.getUserId(), request.getMaterialId(), decision.reason(), decision.effectiveRole());
        }

        MaterialAccessCheckResponse response = new MaterialAccessCheckResponse();
        response.setAllowed(decision.allowed());
        response.setReason(decision.reason().name());
        response.setEffectiveRole(decision.effectiveRole() != null ? decision.effectiveRole().name() : null);
        return response;
    }
}

