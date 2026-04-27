package com.timcritt.tfg.infrastructure.service;

import com.timcritt.tfg.application.port.outbound.ClassroomRepositoryPort;
import com.timcritt.tfg.application.port.outbound.MaterialReferenceRepositoryPort;
import com.timcritt.tfg.application.port.outbound.JoinCodeGenerator;
import com.timcritt.tfg.application.service.ClassroomUseCaseImpl;
import com.timcritt.tfg.application.service.MaterialReferenceQueryService;
import com.timcritt.tfg.domain.model.Classroom;
import com.timcritt.tfg.domain.model.MaterialReference;
import com.timcritt.tfg.domain.model.Member;
import com.timcritt.tfg.domain.model.ClassroomRole;
import com.timcritt.tfg.infrastructure.web.dto.SyncTeachersRequest;
import com.timcritt.tfg.infrastructure.web.dto.TeacherDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.timcritt.tfg.infrastructure.web.dto.MemberDto;

@Service
public class ClassroomServiceAdapter {

    private final ClassroomUseCaseImpl delegate;
    private final MaterialReferenceQueryService materialReferenceQueryService;

    public ClassroomServiceAdapter(ClassroomRepositoryPort repository, MaterialReferenceRepositoryPort materialReferenceRepository, JoinCodeGenerator joinCodeGenerator) {
        this.delegate = new ClassroomUseCaseImpl(repository, joinCodeGenerator);
        this.materialReferenceQueryService = new MaterialReferenceQueryService(materialReferenceRepository);
    }


    @Transactional(readOnly = true)
    public List<Classroom> getClassroomsByMember(Long userId) {
        return delegate.getClassroomsByMember(userId);
    }

    @Transactional(readOnly = true)
    public List<MemberDto> getTeachersByClassroomId(Long classroomId) {
        List<Member> teachers = delegate.getMembersByRole(classroomId, ClassroomRole.TEACHER);
        return teachers.stream().map(member -> {
            MemberDto dto = new MemberDto();
            dto.setUserId(member.getUserId());
            dto.setRole(member.getRole());
            dto.setName(member.getName());
            dto.setSurname(member.getSurname());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemberDto> getStudentsByClassroomId(Long classroomId) {
        List<Member> students = delegate.getMembersByRole(classroomId, ClassroomRole.STUDENT);
        return students.stream().map(member -> {
            MemberDto dto = new MemberDto();
            dto.setUserId(member.getUserId());
            dto.setRole(member.getRole());
            dto.setName(member.getName());
            dto.setSurname(member.getSurname());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<Classroom> getAllClassrooms() {
        return delegate.getAllClassrooms();
    }

    @Transactional(readOnly = true)
    public List<MaterialReference> getMaterialsByClassroom(Long classroomId) {
        return materialReferenceQueryService.getMaterialsByClassroom(classroomId);
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
}
