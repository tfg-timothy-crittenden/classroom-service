package com.timcritt.tfg.application.port.inbound;

import com.timcritt.tfg.application.query.ClassroomSummaryView;

import java.util.List;

public interface ClassroomSummaryQueryUseCase {

    List<ClassroomSummaryView> getClassroomSummariesByMember(Long userId);

    List<ClassroomSummaryView> getAllClassroomSummaries();
}
