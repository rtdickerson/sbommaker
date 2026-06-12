package com.sbommaker.repository;

import com.sbommaker.model.SoftwareComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoftwareComponentRepository extends JpaRepository<SoftwareComponent, Long> {
    List<SoftwareComponent> findAllByOrderByNameAsc();
}
