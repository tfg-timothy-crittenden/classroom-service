package com.timcritt.tfg.infrastructure.web.controller;

import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.infrastructure.service.ClassroomAuthorizationService;
import com.timcritt.tfg.infrastructure.service.ClassroomDirectoryAdapter;
import com.timcritt.tfg.infrastructure.service.ClassroomManagementAdapter;
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
    private ClassroomManagementAdapter classroomManagementService;
    private ClassroomDirectoryAdapter classroomDirectoryService;

    @BeforeEach
    void setUp() {
        classroomManagementService = mock(ClassroomManagementAdapter.class);
        classroomDirectoryService = mock(ClassroomDirectoryAdapter.class);
        ClassroomAuthorizationService authorizationService = new ClassroomAuthorizationService(null);
        ClassroomDtoMapper classroomDtoMapper = new ClassroomDtoMapper();

        when(classroomManagementService.save(any(Classroom.class))).thenAnswer(invocation -> {
            Classroom classroom = invocation.getArgument(0);
            classroom.setId(99L);
            return classroom;
        });

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ClassroomController controller = new ClassroomController(
                classroomManagementService,
                classroomDirectoryService,
                authorizationService,
                classroomDtoMapper
        );

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

        verify(classroomManagementService).save(any(Classroom.class));
    }
}
