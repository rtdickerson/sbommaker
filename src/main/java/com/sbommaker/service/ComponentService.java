package com.sbommaker.service;

import com.sbommaker.model.ComponentVersion;
import com.sbommaker.model.SoftwareComponent;
import com.sbommaker.repository.ComponentVersionRepository;
import com.sbommaker.repository.SoftwareComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ComponentService {

    private final SoftwareComponentRepository componentRepo;
    private final ComponentVersionRepository versionRepo;

    public ComponentService(SoftwareComponentRepository componentRepo,
                            ComponentVersionRepository versionRepo) {
        this.componentRepo = componentRepo;
        this.versionRepo = versionRepo;
    }

    public List<SoftwareComponent> findAll() {
        return componentRepo.findAllByOrderByNameAsc();
    }

    public SoftwareComponent findById(Long id) {
        return componentRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Component not found: " + id));
    }

    public SoftwareComponent save(SoftwareComponent component) {
        return componentRepo.save(component);
    }

    public Optional<SoftwareComponent> findByName(String name) {
        return componentRepo.findByNameIgnoreCase(name);
    }

    /** Returns the saved component and true if created, false if updated. */
    public UpsertResult upsert(String name, String manufacturer, String sourceUrl) {
        return componentRepo.findByNameIgnoreCase(name)
                .map(existing -> {
                    existing.setManufacturer(manufacturer);
                    existing.setSourceUrl(sourceUrl);
                    return new UpsertResult(componentRepo.save(existing), false);
                })
                .orElseGet(() -> {
                    SoftwareComponent c = new SoftwareComponent();
                    c.setName(name);
                    c.setManufacturer(manufacturer);
                    c.setSourceUrl(sourceUrl);
                    return new UpsertResult(componentRepo.save(c), true);
                });
    }

    public record UpsertResult(SoftwareComponent component, boolean created) {}

    public void delete(Long id) {
        componentRepo.deleteById(id);
    }

    public ComponentVersion addVersion(Long componentId, ComponentVersion version) {
        SoftwareComponent component = findById(componentId);
        version.setComponent(component);
        return versionRepo.save(version);
    }

    public ComponentVersion findVersionById(Long versionId) {
        return versionRepo.findById(versionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + versionId));
    }

    public void deleteVersion(Long versionId) {
        versionRepo.deleteById(versionId);
    }
}
