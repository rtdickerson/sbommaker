package com.sbommaker.repository;

import com.sbommaker.model.ProductRelease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductReleaseRepository extends JpaRepository<ProductRelease, Long> {
    List<ProductRelease> findAllByOrderByTagAsc();
}
