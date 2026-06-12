package com.sbommaker.repository;

import com.sbommaker.model.ReleaseItem;
import com.sbommaker.model.ProductRelease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReleaseItemRepository extends JpaRepository<ReleaseItem, Long> {
    List<ReleaseItem> findByRelease(ProductRelease release);
    void deleteByReleaseIdAndComponentVersionId(Long releaseId, Long componentVersionId);
}
