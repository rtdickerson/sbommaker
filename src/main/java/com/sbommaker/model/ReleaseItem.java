package com.sbommaker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "release_item",
       uniqueConstraints = @UniqueConstraint(columnNames = {"release_id", "component_version_id"}))
public class ReleaseItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "release_id", nullable = false)
    private ProductRelease release;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "component_version_id", nullable = false)
    private ComponentVersion componentVersion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductRelease getRelease() { return release; }
    public void setRelease(ProductRelease release) { this.release = release; }

    public ComponentVersion getComponentVersion() { return componentVersion; }
    public void setComponentVersion(ComponentVersion componentVersion) { this.componentVersion = componentVersion; }
}
