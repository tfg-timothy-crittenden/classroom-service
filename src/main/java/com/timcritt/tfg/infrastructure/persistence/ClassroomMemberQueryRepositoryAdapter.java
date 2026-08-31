package com.timcritt.tfg.infrastructure.persistence;

import com.timcritt.tfg.application.port.outbound.query.ClassroomMemberQueryPort;
import com.timcritt.tfg.application.query.ClassroomMemberView;
import com.timcritt.tfg.domain.aggregate.classroom.ClassroomRole;
import com.timcritt.tfg.infrastructure.persistence.spring.ClassroomMemberQueryRow;
import com.timcritt.tfg.infrastructure.persistence.spring.MembershipJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClassroomMemberQueryRepositoryAdapter
        implements ClassroomMemberQueryPort {

    private final MembershipJpaRepository membershipJpaRepository;

    public ClassroomMemberQueryRepositoryAdapter(
            MembershipJpaRepository membershipJpaRepository
    ) {
        this.membershipJpaRepository = membershipJpaRepository;
    }

    @Override
    public List<ClassroomMemberView> findByClassroomIdAndRole(
            Long classroomId,
            ClassroomRole role
    ) {
        return membershipJpaRepository
                .findMemberViewsByClassroomIdAndRole(
                        classroomId,
                        role.name()
                )
                .stream()
                .map(this::toView)
                .toList();
    }

    private ClassroomMemberView toView(
            ClassroomMemberQueryRow row
    ) {
        return new ClassroomMemberView(
                row.getMembershipId(),
                row.getUserId(),
                ClassroomRole.valueOf(row.getRole()),
                row.getFirstName(),
                row.getLastName()
        );
    }
}