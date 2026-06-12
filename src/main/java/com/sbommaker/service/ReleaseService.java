package com.sbommaker.service;

import com.sbommaker.model.ComponentVersion;
import com.sbommaker.model.ProductRelease;
import com.sbommaker.model.ReleaseItem;
import com.sbommaker.repository.ComponentVersionRepository;
import com.sbommaker.repository.ProductReleaseRepository;
import com.sbommaker.repository.ReleaseItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReleaseService {

    private final ProductReleaseRepository releaseRepo;
    private final ReleaseItemRepository itemRepo;
    private final ComponentVersionRepository versionRepo;

    public ReleaseService(ProductReleaseRepository releaseRepo,
                          ReleaseItemRepository itemRepo,
                          ComponentVersionRepository versionRepo) {
        this.releaseRepo = releaseRepo;
        this.itemRepo = itemRepo;
        this.versionRepo = versionRepo;
    }

    public List<ProductRelease> findAll() {
        return releaseRepo.findAllByOrderByTagAsc();
    }

    public ProductRelease findById(Long id) {
        return releaseRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Release not found: " + id));
    }

    public ProductRelease save(ProductRelease release) {
        return releaseRepo.save(release);
    }

    public void delete(Long id) {
        releaseRepo.deleteById(id);
    }

    public void addComponentVersion(Long releaseId, Long componentVersionId) {
        ProductRelease release = findById(releaseId);
        ComponentVersion version = versionRepo.findById(componentVersionId)
                .orElseThrow(() -> new IllegalArgumentException("Version not found: " + componentVersionId));
        boolean alreadyPresent = release.getItems().stream()
                .anyMatch(i -> i.getComponentVersion().getId().equals(componentVersionId));
        if (!alreadyPresent) {
            ReleaseItem item = new ReleaseItem();
            item.setRelease(release);
            item.setComponentVersion(version);
            itemRepo.save(item);
        }
    }

    public void removeItem(Long releaseId, Long itemId) {
        itemRepo.deleteById(itemId);
    }
}
