package com.sbommaker.api;

import jakarta.validation.constraints.NotBlank;

public record ReleaseComponentRequest(@NotBlank String componentName, @NotBlank String version) {}
