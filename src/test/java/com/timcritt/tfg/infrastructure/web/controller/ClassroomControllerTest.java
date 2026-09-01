package com.timcritt.tfg.infrastructure.web.controller;

import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.infrastructure.service.ClassroomAuthorizationService;
import com.timcritt.tfg.infrastructure.service.ClassroomDirectoryAdapter;
import com.timcritt.tfg.infrastructure.service.ClassroomManagementAdapter;
import com.timcritt.tfg.infrastructure.service.ClassroomMemberQueryAdapter;
import com.timcritt.tfg.infrastructure.service.ClassroomSummaryQueryAdapter;
import com.timcritt.tfg.infrastructure.web.dtoMapper.ClassroomDtoMapper;
import com.timcritt.tfg.infrastructure.web.dtoMapper.ClassroomSummaryDtoMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ClassroomControllerTest {

    private MockMvc mockMvc;
    private ClassroomManagementAdapter classroomManagementService;
    private ClassroomDirectoryAdapter classroomDirectoryService;
    private ClassroomMemberQueryAdapter classroomMemberQueryService;
    private ClassroomSummaryQueryAdapter classroomSummaryQueryService;

    @BeforeEach
    void setUp() {
        classroomManagementService = mock(ClassroomManagementAdapter.class);
        classroomDirectoryService = mock(ClassroomDirectoryAdapter.class);
        classroomMemberQueryService = mock(ClassroomMemberQueryAdapter.class);
        classroomSummaryQueryService = mock(ClassroomSummaryQueryAdapter.class);
        ClassroomAuthorizationService authorizationService = new ClassroomAuthorizationService(null);
        ClassroomDtoMapper classroomDtoMapper = new ClassroomDtoMapper();
        ClassroomSummaryDtoMapper classroomSummaryDtoMapper = new ClassroomSummaryDtoMapper();

        when(classroomManagementService.save(any(Classroom.class))).thenAnswer(invocation -> {
            Classroom classroom = invocation.getArgument(0);
            classroom.setId(99L);
            return classroom;
        });
        when(classroomSummaryQueryService.getAllClassroomSummaries()).thenReturn(List.of());

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ClassroomController controller = new ClassroomController(
                classroomManagementService,
                classroomDirectoryService,
                authorizationService,
                classroomMemberQueryService,
                classroomSummaryQueryService,
                classroomDtoMapper,
                classroomSummaryDtoMapper
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void getAllClassroomSummaries_returnsEmptyList() throws Exception {
        var admin = new UsernamePasswordAuthenticationToken(
                "admin",
                "n/a",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        mockMvc.perform(get("/api/classrooms")
                        .principal(admin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(classroomSummaryQueryService).getAllClassroomSummaries();
    }
}
