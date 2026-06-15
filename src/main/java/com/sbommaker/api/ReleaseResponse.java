package com.sbommaker.api;

import com.sbommaker.model.ProductRelease;

public record ReleaseResponse(Long id, String name, String tag, String description,
                               int componentCount, boolean created) {
    public static ReleaseResponse of(ProductRelease r, boolean created) {
        return new ReleaseResponse(r.getId(), r.getName(), r.getTag(),
                r.getDescription(), r.getItems().size(), created);
    }
}
