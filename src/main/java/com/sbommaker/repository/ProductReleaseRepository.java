package com.sbommaker.repository;

import com.sbommaker.model.ProductRelease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductReleaseRepository extends JpaRepository<ProductRelease, Long> {
    List<ProductRelease> findAllByOrderByTagAsc();
    Optional<ProductRelease> findByTagIgnoreCase(String tag);
}
