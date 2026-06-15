package com.sbommaker.service;

import com.sbommaker.model.ComponentVersion;
import com.sbommaker.model.ProductRelease;
import com.sbommaker.model.ReleaseItem;
import com.sbommaker.model.SoftwareComponent;
import com.sbommaker.repository.ComponentVersionRepository;
import com.sbommaker.repository.ProductReleaseRepository;
import com.sbommaker.repository.ReleaseItemRepository;
import com.sbommaker.repository.SoftwareComponentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReleaseService {

    private final ProductReleaseRepository releaseRepo;
    private final ReleaseItemRepository itemRepo;
    private final ComponentVersionRepository versionRepo;
    private final SoftwareComponentRepository componentRepo;

    public ReleaseService(ProductReleaseRepository releaseRepo,
                          ReleaseItemRepository itemRepo,
                          ComponentVersionRepository versionRepo,
                          SoftwareComponentRepository componentRepo) {
        this.releaseRepo = releaseRepo;
        this.itemRepo = itemRepo;
        this.versionRepo = versionRepo;
        this.componentRepo = componentRepo;
    }

    public List<ProductRelease> findAll() {
        return releaseRepo.findAllByOrderByTagAsc();
    }

    public ProductRelease findById(Long id) {
        return releaseRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Release not found: " + id));
    }

    public ProductRelease findByTag(String tag) {
        return releaseRepo.findByTagIgnoreCase(tag)
                .orElseThrow(() -> new IllegalArgumentException("Release not found: " + tag));
    }

    public ProductRelease save(ProductRelease release) {
        return releaseRepo.save(release);
    }

    public void delete(Long id) {
        releaseRepo.deleteById(id);
    }

    public record UpsertResult(ProductRelease release, boolean created) {}

    public UpsertResult upsert(String name, String tag, String description) {
        return releaseRepo.findByTagIgnoreCase(tag)
                .map(existing -> {
                    existing.setName(name);
                    existing.setDescription(description);
                    return new UpsertResult(releaseRepo.save(existing), false);
                })
                .orElseGet(() -> {
                    ProductRelease r = new ProductRelease();
                    r.setName(name);
                    r.setTag(tag);
                    r.setDescription(description);
                    return new UpsertResult(releaseRepo.save(r), true);
                });
    }

    public void addComponentVersion(Long releaseId, Long componentVersionId) {
        ProductRelease release = findById(releaseId);
        ComponentVersion version = versionRepo.findById(componentVersionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + componentVersionId));
        linkIfAbsent(release, version);
    }

    public void addComponentVersionByName(String releaseTag, String componentName, String versionStr) {
        ProductRelease release = findByTag(releaseTag);
        SoftwareComponent component = componentRepo.findByNameIgnoreCase(componentName)
                .orElseThrow(() -> new IllegalArgumentException("Component not found: " + componentName));
        ComponentVersion version = versionRepo.findByComponentAndVersionIgnoreCase(component, versionStr)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Version not found: " + componentName + " " + versionStr));
        linkIfAbsent(release, version);
    }

    public void removeItem(Long releaseId, Long itemId) {
        itemRepo.deleteById(itemId);
    }

    private void linkIfAbsent(ProductRelease release, ComponentVersion version) {
        boolean alreadyPresent = release.getItems().stream()
                .anyMatch(i -> i.getComponentVersion().getId().equals(version.getId()));
        if (!alreadyPresent) {
            ReleaseItem item = new ReleaseItem();
            item.setRelease(release);
            item.setComponentVersion(version);
            itemRepo.save(item);
        }
    }
}
