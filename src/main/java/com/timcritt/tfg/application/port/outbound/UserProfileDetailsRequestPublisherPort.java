package com.timcritt.tfg.application.port.outbound;

import java.util.List;

public interface UserProfileDetailsRequestPublisherPort {
    void requestUserProfileDetails(List<Long> userIds);
}
