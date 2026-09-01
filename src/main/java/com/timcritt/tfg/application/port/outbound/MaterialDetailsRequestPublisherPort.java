package com.timcritt.tfg.application.port.outbound;

import java.util.List;

public interface MaterialDetailsRequestPublisherPort {

    void requestMaterialDetails(List<Long> materialIds);
}

