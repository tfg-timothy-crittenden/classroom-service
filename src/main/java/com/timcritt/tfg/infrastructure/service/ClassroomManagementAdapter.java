package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.JoinCodeGenerator;
import com.timcritt.tfg.application.port.outbound.repository.MaterialDetailsRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MembershipRepositoryPort;
import com.timcritt.tfg.application.service.useCase.ClassroomManagementUseCaseImpl;
import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.domain.projection.MaterialDetails;
import com.timcritt.tfg.domain.aggregate.classroom.MaterialReference;
import com.timcritt.tfg.infrastructure.web.dto.MaterialReferenceWithDetailsDto;
import com.timcritt.tfg.infrastructure.web.dto.TeacherDto;
import com.timcritt.tfg.infrastructure.web.dto.UpdateClassroomMaterialsRequest;
import com.timcritt.tfg.infrastructure.web.dtoMapper.MaterialReferenceWithDetailsDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class ClassroomManagementAdapter  {

    private final ClassroomManagementUseCaseImpl delegate;
    private final MemberRoleServiceAdapter memberRoleService;
    private final MaterialDetailsRepositoryPort materialDetailsRepository;


    public ClassroomManagementAdapter(ClassroomRepositoryPort repository,
                                      MembershipRepositoryPort memberRepository,
                                      JoinCodeGenerator joinCodeGenerator,
                                      MemberRoleServiceAdapter memberRoleService,
                                      MaterialReferenceRepositoryPort materialReferenceRepository,
                                      MaterialDetailsRepositoryPort materialDetailsRepository) {
        this.delegate = new ClassroomManagementUseCaseImpl(repository, memberRepository, joinCodeGenerator, materialReferenceRepository);
        this.memberRoleService = memberRoleService;
        this.materialDetailsRepository = materialDetailsRepository;
    }

    // ***************************** QUERIES *************************************

    public List<Membership> getTeachersByClassroomId(Long classroomId) {
        return delegate.getMembersByRole(classroomId, ClassroomRole.TEACHER);
    }

    public List<Membership> getStudentsByClassroomId(Long classroomId) {
        return delegate.getMembersByRole(classroomId, ClassroomRole.STUDENT);
    }

    public Classroom save(Classroom classroom) {
        return delegate.save(classroom);
    }

    public Optional<ClassroomRole> getRoleInClassroom(Long classroomId, Long userId) {
        return memberRoleService.getRoleInClassroom(classroomId, userId);
    }

    public List<MaterialReference> getClassroomMaterials(Long classroomId) {
        return delegate.getAllClassroomMaterials(classroomId);
    }

    public List<MaterialReferenceWithDetailsDto> getClassroomMaterialsByRole(Long classroomId, ClassroomRole role) {
        return delegate.getClassroomMaterialsByRole(classroomId, role).stream()
                .map(materialReference -> {
                    MaterialDetails materialDetails = materialDetailsRepository.findByMaterialId(materialReference.getMaterialId());
                    return MaterialReferenceWithDetailsDtoMapper.toDto(materialReference, materialDetails);
                })
                .toList();
    }


    // ************************************** COMMANDS *****************************************************
    @Transactional
    public Classroom assignTeacherToClassroom(Long classroomId, TeacherDto teacherDto) {
        return delegate.assignTeacherToClassroom(
                classroomId,
                teacherDto.getUserId(),
                teacherDto.getName(),
                teacherDto.getSurname()
        );
    }

    @Transactional
    public Classroom syncTeachersForClassroom(Long classroomId, List<TeacherDto> teachers) {
        List<Membership> teacherMemberships = teachers.stream().map(teacherDto -> {
            Membership membership = new Membership();
            membership.setUserId(teacherDto.getUserId());
            membership.setRole(ClassroomRole.TEACHER);
            membership.setCreatedAt(java.time.Instant.now());
            membership.setUpdatedAt(java.time.Instant.now());
            return membership;
        }).collect(java.util.stream.Collectors.toList());

        return delegate.syncTeachersForClassroom(classroomId, teacherMemberships);
    }

    @Transactional
    public Classroom joinClassroom(Long userId, String classCode) {
        return delegate.joinClassroom(userId, classCode);
    }

    @Transactional
    public void removeMemberFromClassroom(Long classroomId, Long userId) {
        delegate.removeMemberFromClassroom(classroomId, userId);
    }

    @Transactional
    public int revokeTeacherRoleFromUser(Long userId) {
        return delegate.revokeTeacherRoleFromUser(userId);
    }

    @Transactional
    public void replaceMaterials(Long classroomId, UpdateClassroomMaterialsRequest request) {
        List<UpdateClassroomMaterialsCommand.MaterialAssignment> desired = request == null || request.getMaterials() == null
                ? List.of()
                : request.getMaterials().stream()
                .map(m -> new UpdateClassroomMaterialsCommand.MaterialAssignment(
                        m.getMaterialId(),
                        m.getAssignedToRole()
                ))
                .toList();
        UpdateClassroomMaterialsCommand command = new UpdateClassroomMaterialsCommand(classroomId, desired);
        delegate.replaceMaterials(command);

        // Insert only if an entry does not already exist. Don't overwrite exiting rows. Material service is the
        // canonical source for MaterialDetails field values.
        if (request != null && request.getMaterials() != null) {
            request.getMaterials().forEach(m -> {
                if (m.getMaterialId() == null || m.getName() == null) return;
                MaterialDetails details = MaterialDetails.builder()
                        .materialId(m.getMaterialId())
                        .name(m.getName())
                        .description(m.getDescription())
                        .part1Title(m.getPart1Title())
                        .part2Title(m.getPart2Title())
                        .build();
                if(materialDetailsRepository.findByMaterialId(m.getMaterialId()) == null) {
                    materialDetailsRepository.save(details);
                }

            });
        }
    }

}
