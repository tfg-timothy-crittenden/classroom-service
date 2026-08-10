package com.timcritt.tfg.infrastructure.web.controller;

import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.infrastructure.service.ClassroomAuthorizationService;
import com.timcritt.tfg.infrastructure.service.ClassroomServiceAdapter;
import com.timcritt.tfg.infrastructure.web.dtoMapper.ClassroomDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClassroomControllerTest {

    private MockMvc mockMvc;
    private ClassroomServiceAdapter classroomService;

    @BeforeEach
    void setUp() {
        classroomService = mock(ClassroomServiceAdapter.class);
        ClassroomAuthorizationService authorizationService = new ClassroomAuthorizationService(null);

        when(classroomService.save(any(Classroom.class))).thenAnswer(invocation -> {
            Classroom classroom = invocation.getArgument(0);
            classroom.setId(99L);
            return classroom;
        });

        ClassroomController controller = new ClassroomController(
                classroomService,
                authorizationService,
                new ClassroomDtoMapper(),
                null,
                null
        );

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void createClassroom_returnsCreatedWithoutBody() throws Exception {
        var admin = new UsernamePasswordAuthenticationToken(
                "admin",
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        mockMvc.perform(post("/api/classrooms")
                        .principal(admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" +
                                "\"name\":\"B2 Tuesday\"," +
                                "\"description\":\"Speaking practice\"" +
                                "}"))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

        verify(classroomService).save(any(Classroom.class));
    }
}

