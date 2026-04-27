package com.timcritt.tfg.infrastructure.web.controller;

import com.timcritt.tfg.application.service.MaterialAccessDecision;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.security.InternalApiKeyFilter;
import com.timcritt.tfg.infrastructure.service.MaterialAccessAuthorizationServiceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InternalAuthorizationControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InternalApiKeyFilter filter = new InternalApiKeyFilter();
        ReflectionTestUtils.setField(filter, "internalApiKey", "test-api-key");

        InternalAuthorizationController controller = new InternalAuthorizationController(
                new MaterialAccessAuthorizationServiceAdapter(null, null) {
                    @Override
                    public MaterialAccessDecision checkMaterialAccess(String userId, Long materialId, String action) {
                        return new MaterialAccessDecision(true, MaterialAccessDecision.Reason.OK, ClassroomRole.STUDENT);
                    }
                }
        );

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .addFilters(filter)
                .setValidator(validator)
                .build();
    }

    @Test
    void returnsUnauthorizedWhenApiKeyMissing() throws Exception {
        mockMvc.perform(post("/internal/authorization/material-access:check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"42\",\"materialId\":9,\"action\":\"READ\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsUnauthorizedWhenApiKeyInvalid() throws Exception {
        mockMvc.perform(post("/internal/authorization/material-access:check")
                        .header(InternalApiKeyFilter.HEADER_NAME, "wrong-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"42\",\"materialId\":9,\"action\":\"READ\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void returnsOkWhenApiKeyValid() throws Exception {
        mockMvc.perform(post("/internal/authorization/material-access:check")
                        .header(InternalApiKeyFilter.HEADER_NAME, "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"42\",\"materialId\":9,\"action\":\"READ\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.reason").value("OK"))
                .andExpect(jsonPath("$.effectiveRole").value("STUDENT"));
    }

    @Test
    void returnsBadRequestWhenPayloadIsInvalid() throws Exception {
        mockMvc.perform(post("/internal/authorization/material-access:check")
                        .header(InternalApiKeyFilter.HEADER_NAME, "test-api-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"42\",\"action\":\"READ\"}"))
                .andExpect(status().isBadRequest());
    }
}





