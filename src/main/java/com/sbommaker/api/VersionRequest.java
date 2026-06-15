package com.sbommaker.api;

import jakarta.validation.constraints.NotBlank;

public record VersionRequest(@NotBlank String version, String notes) {}
