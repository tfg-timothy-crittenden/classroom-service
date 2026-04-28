package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.JoinCodeGenerator;
import com.timcritt.tfg.application.service.ClassroomUseCaseImpl;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.domain.model.Member;
import com.timcritt.tfg.infrastructure.web.dto.TeacherDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class ClassroomServiceAdapter {

    private final ClassroomUseCaseImpl delegate;
    private final MemberRoleServiceAdapter memberRoleService;

    public ClassroomServiceAdapter(ClassroomRepositoryPort repository,
                                   JoinCodeGenerator joinCodeGenerator,
                                   MemberRoleServiceAdapter memberRoleService) {
        this.delegate = new ClassroomUseCaseImpl(repository, joinCodeGenerator);
        this.memberRoleService = memberRoleService;
    }


    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByMember(Long userId) {
        return delegate.getClassroomsByMember(userId);
    }

    @Transactional(readOnly = true)
    public List<Member> getTeachersByClassroomId(Long classroomId) {
        return delegate.getMembersByRole(classroomId, ClassroomRole.TEACHER);
    }

    @Transactional(readOnly = true)
    public List<Member> getStudentsByClassroomId(Long classroomId) {
        return delegate.getMembersByRole(classroomId, ClassroomRole.STUDENT);
    }


    @Transactional(readOnly = true)
    public List<Classroom> getAllClassrooms() {
        return delegate.getAllClassrooms();
    }


    @Transactional
    public Classroom save(Classroom classroom) {
        return delegate.save(classroom);
    }

    @Transactional
    public Classroom assignTeacherToClassroom(Long classroomId, TeacherDto teacherDto) {
        Member member = new Member();
        member.setUserId(teacherDto.getUserId());
        member.setName(teacherDto.getName());
        member.setSurname(teacherDto.getSurname());
        member.setRole(ClassroomRole.TEACHER);
        member.setCreatedAt(java.time.Instant.now());
        member.setUpdatedAt(java.time.Instant.now());
        return delegate.assignTeacherToClassroom(classroomId, member);
    }

    @Transactional
    public Classroom syncTeachersForClassroom(Long classroomId, List<TeacherDto> teachers) {
        List<Member> teacherMembers = teachers.stream().map(teacherDto -> {
            Member member = new Member();
            member.setUserId(teacherDto.getUserId());
            member.setName(teacherDto.getName());
            member.setSurname(teacherDto.getSurname());
            member.setRole(ClassroomRole.TEACHER);
            member.setCreatedAt(java.time.Instant.now());
            member.setUpdatedAt(java.time.Instant.now());
            return member;
        }).collect(java.util.stream.Collectors.toList());
        return delegate.syncTeachersForClassroom(classroomId, teacherMembers);
    }

    @Transactional(readOnly = true)
    public Classroom getClassroomById(Long classroomId) {
        return delegate.getClassroomById(classroomId);
    }

    @Transactional
    public void deleteClassroomById(Long classroomId) {
        delegate.deleteClassroomById(classroomId);
    }

    @Transactional
    public void deleteClassroomsByIds(List<Long> classroomIds) {
        delegate.deleteClassroomsByIds(classroomIds);
    }

    @Transactional
    public Classroom joinClassroom(Long userId, String classCode, String name, String surname) {
        return delegate.joinClassroom(userId, classCode, name, surname);
    }

    @Transactional
    public boolean removeMemberFromClassroom(Long classroomId, Long userId) {
        return delegate.removeMemberFromClassroom(classroomId, userId);
    }

    @Transactional(readOnly = true)
    public Optional<ClassroomRole> getRoleInClassroom(Long classroomId, Long userId) {
        return memberRoleService.getRoleInClassroom(classroomId, userId);
    }
}
