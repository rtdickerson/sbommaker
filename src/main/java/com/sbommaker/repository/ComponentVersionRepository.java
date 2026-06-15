package com.sbommaker.repository;

import com.sbommaker.model.ComponentVersion;
import com.sbommaker.model.SoftwareComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ComponentVersionRepository extends JpaRepository<ComponentVersion, Long> {
    List<ComponentVersion> findByComponentOrderByVersionAsc(SoftwareComponent component);
    Optional<ComponentVersion> findByComponentAndVersionIgnoreCase(SoftwareComponent component, String version);
}
