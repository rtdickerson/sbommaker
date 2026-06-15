package com.sbommaker.api;

import com.sbommaker.model.ComponentVersion;

public record VersionResponse(Long id, String componentName, String version, String notes, boolean created) {
    public static VersionResponse of(ComponentVersion cv, boolean created) {
        return new VersionResponse(cv.getId(), cv.getComponent().getName(),
                cv.getVersion(), cv.getNotes(), created);
    }
}
