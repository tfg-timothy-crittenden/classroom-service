package com.timcritt.tfg.application.service.useCase;

import com.timcritt.tfg.application.command.UpdateClassroomMaterialsCommand;
import com.timcritt.tfg.application.exception.ClassroomNotFoundException;
import com.timcritt.tfg.application.port.inbound.ClassroomManagementUseCase;
import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.JoinCodeGenerator;
import com.timcritt.tfg.application.port.outbound.MaterialDetailsRequestPublisherPort;
import com.timcritt.tfg.application.port.outbound.repository.MaterialDetailsRepositoryPort;
import com.timcritt.tfg.application.port.outbound.repository.MembershipRepositoryPort;
import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.domain.aggregate.classroom.MaterialReference;
import com.timcritt.tfg.domain.aggregate.classroom.Membership;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;

import java.util.List;
import java.util.Objects;

public class ClassroomManagementUseCaseImpl implements ClassroomManagementUseCase {

    private final ClassroomRepositoryPort classroomRepository;
    private final MembershipRepositoryPort memberRepository;
    private final JoinCodeGenerator joinCodeGenerator;
    private final MaterialDetailsRepositoryPort materialDetailsRepository;
    private final MaterialDetailsRequestPublisherPort materialDetailsRequestPublisher;

    public ClassroomManagementUseCaseImpl(
            ClassroomRepositoryPort repository,
            MembershipRepositoryPort memberRepository,
            JoinCodeGenerator joinCodeGenerator,
            MaterialDetailsRepositoryPort materialDetailsRepository,
            MaterialDetailsRequestPublisherPort materialDetailsRequestPublisher
    ) {
        this.classroomRepository = repository;
        this.memberRepository = memberRepository;
        this.joinCodeGenerator = joinCodeGenerator;
        this.materialDetailsRepository = materialDetailsRepository;
        this.materialDetailsRequestPublisher = materialDetailsRequestPublisher;
    }


    // ***************************** QUERIES *************************************
    @Override
    public List<Membership> getMembersByRole(Long classroomId, ClassroomRole role) {
        Classroom classroom = classroomRepository.findById(classroomId);
        if (classroom == null) {
            throw new ClassroomNotFoundException(classroomId);
        }
        return classroom.getMembersByRole(role);
    }

    @Override
    public List<MaterialReference> getAllClassroomMaterials(Long classroomId) {
        Classroom classroom = classroomRepository.findById(classroomId);
        if(classroom == null) {
            throw new ClassroomNotFoundException(classroomId);
        }
        return classroom.getMaterials();
    }

    @Override
    public List<MaterialReference> getClassroomMaterialsByRole(Long classroomId, ClassroomRole role) {
        Classroom classroom = classroomRepository.findById(classroomId);
        if (classroom == null) {
            throw new ClassroomNotFoundException(classroomId);
        }
        return classroom.getMaterials().stream()
                .filter(material -> material.getAssignedToRole() == role)
                .toList();
    }

    // ************************************** COMMANDS *****************************************************
    @Override
    public Classroom save(Classroom classroom) {
        if (classroom.getJoinCode() == null || classroom.getJoinCode().isEmpty()) {
            classroom.setJoinCode(joinCodeGenerator.generateJoinCode());
        }
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom assignTeacherToClassroom(Long classroomId, Long userId) {
        Classroom classroom = classroomRepository.findById(classroomId);
        if (classroom == null) {
            throw new ClassroomNotFoundException(classroomId);
        }
        classroom.assignTeacher(userId);
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom joinClassroom(Long userId, String classCode) {
        Classroom classroom = classroomRepository.findByJoinCode(classCode);
        if (classroom == null) {
            throw new ClassroomNotFoundException("Classroom not found for code: " + classCode);
        }
        classroom.assignStudent(userId);
        return classroomRepository.save(classroom);
    }

    @Override
    public Classroom syncTeachersForClassroom(Long classroomId, List<Membership> teachers) {
        Classroom classroom = classroomRepository.findById(classroomId);
        if (classroom == null) {
            throw new ClassroomNotFoundException(classroomId);
        }
        classroom.syncTeachers(teachers);
        return classroomRepository.save(classroom);
    }

    @Override
    public void removeMemberFromClassroom(Long classroomId, Long userId) {
        Classroom classroom = classroomRepository.findById(classroomId);
        if(classroom == null) {
            throw new ClassroomNotFoundException(classroomId);
        }
        classroom.removeMember(userId);
        classroomRepository.save(classroom);
    }

    @Override
    public int revokeTeacherRoleFromUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        return memberRepository.deleteTeacherMembershipsByUserId(userId);
    }

    @Override
    public void replaceMaterials(UpdateClassroomMaterialsCommand command) {

        Classroom classroom = classroomRepository.findById(command.classroomId());

        if(classroom == null) {
            throw new ClassroomNotFoundException(command.classroomId());
        }

        List<MaterialReference> materialReferences = command.materials().stream()
                .map(item -> new MaterialReference(null, item.materialId(), item.assignedToRole()))
                .toList();

        classroom.replaceMaterials(materialReferences);
        classroomRepository.save(classroom);

        List<Long> missingMaterialDetailsIds = command.materials().stream()
                .map(UpdateClassroomMaterialsCommand.MaterialAssignment::materialId)
                .filter(Objects::nonNull)
                .distinct()
                .filter(materialId -> materialDetailsRepository.findByMaterialId(materialId) == null)
                .toList();

        if (!missingMaterialDetailsIds.isEmpty()) {
            materialDetailsRequestPublisher.requestMaterialDetails(missingMaterialDetailsIds);
        }
    }



}
