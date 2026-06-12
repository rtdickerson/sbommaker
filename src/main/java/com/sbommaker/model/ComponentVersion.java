package com.sbommaker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "component_version")
public class ComponentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "component_id", nullable = false)
    private SoftwareComponent component;

    @NotBlank
    @Column(nullable = false)
    private String version;

    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public SoftwareComponent getComponent() { return component; }
    public void setComponent(SoftwareComponent component) { this.component = component; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
