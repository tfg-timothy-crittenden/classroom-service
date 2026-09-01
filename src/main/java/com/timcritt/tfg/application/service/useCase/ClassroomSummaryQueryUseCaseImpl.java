package com.timcritt.tfg.application.service.useCase;

import com.timcritt.tfg.application.port.inbound.ClassroomSummaryQueryUseCase;
import com.timcritt.tfg.application.port.outbound.query.ClassroomMemberQueryPort;
import com.timcritt.tfg.application.port.outbound.repository.ClassroomRepositoryPort;
import com.timcritt.tfg.application.query.ClassroomMemberView;
import com.timcritt.tfg.application.query.ClassroomSummaryView;
import com.timcritt.tfg.domain.aggregate.classroom.Classroom;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;

import java.util.List;
import java.util.Objects;

public class ClassroomSummaryQueryUseCaseImpl implements ClassroomSummaryQueryUseCase {

    private final ClassroomRepositoryPort classroomRepository;
    private final ClassroomMemberQueryPort classroomMemberQueryPort;

    public ClassroomSummaryQueryUseCaseImpl(ClassroomRepositoryPort classroomRepository,
                                             ClassroomMemberQueryPort classroomMemberQueryPort) {
        this.classroomRepository = classroomRepository;
        this.classroomMemberQueryPort = classroomMemberQueryPort;
    }

    @Override
    public List<ClassroomSummaryView> getClassroomSummariesByMember(Long userId) {
        Objects.requireNonNull(userId, "userId cannot be null");
        return classroomRepository.findByMemberUserId(userId).stream()
                .map(this::toSummaryView)
                .toList();
    }

    @Override
    public List<ClassroomSummaryView> getAllClassroomSummaries() {
        return classroomRepository.findAll().stream()
                .map(this::toSummaryView)
                .toList();
    }

    private ClassroomSummaryView toSummaryView(Classroom classroom) {
        List<ClassroomMemberView> teachers = classroomMemberQueryPort.findByClassroomIdAndRole(
                classroom.getId(),
                ClassroomRole.TEACHER
        );

        int studentCount = classroom.getMembers() == null ? 0 : (int) classroom.getMembers().values().stream()
                .filter(member -> member.getRole() == ClassroomRole.STUDENT)
                .count();

        int materialCount = classroom.getMaterials() == null ? 0 : classroom.getMaterials().size();

        return new ClassroomSummaryView(
                classroom.getId(),
                classroom.getName(),
                classroom.getDescription(),
                classroom.getCreatedAt(),
                classroom.getUpdatedAt(),
                studentCount,
                materialCount,
                teachers
        );
    }
}
