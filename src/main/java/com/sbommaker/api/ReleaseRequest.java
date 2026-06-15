package com.sbommaker.api;

import jakarta.validation.constraints.NotBlank;

public record ReleaseRequest(@NotBlank String name, @NotBlank String tag, String description) {}
