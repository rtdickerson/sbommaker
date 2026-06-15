package com.sbommaker.api;

import com.sbommaker.model.SoftwareComponent;

public record ComponentResponse(
        Long id,
        String name,
        String manufacturer,
        String sourceUrl,
        int versionCount,
        boolean created
) {
    public static ComponentResponse of(SoftwareComponent c, boolean created) {
        return new ComponentResponse(c.getId(), c.getName(), c.getManufacturer(),
                c.getSourceUrl(), c.getVersions().size(), created);
    }
}
