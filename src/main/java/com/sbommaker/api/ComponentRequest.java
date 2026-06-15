package com.sbommaker.api;

import jakarta.validation.constraints.NotBlank;

public record ComponentRequest(
        @NotBlank String name,
        @NotBlank String manufacturer,
        String sourceUrl
) {}
