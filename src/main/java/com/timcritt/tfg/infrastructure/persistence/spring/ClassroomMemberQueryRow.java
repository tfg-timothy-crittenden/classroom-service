package com.timcritt.tfg.infrastructure.persistence.spring;

public interface ClassroomMemberQueryRow {

    Long getMembershipId();

    Long getUserId();

    String getRole();

    String getFirstName();

    String getLastName();
}