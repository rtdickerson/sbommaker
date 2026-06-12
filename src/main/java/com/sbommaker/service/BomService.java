package com.sbommaker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sbommaker.model.ComponentVersion;
import com.sbommaker.model.ProductRelease;
import com.sbommaker.model.ReleaseItem;
import com.sbommaker.model.SoftwareComponent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BomService {

    private final ReleaseService releaseService;
    private final ObjectMapper objectMapper;

    public BomService(ReleaseService releaseService, ObjectMapper objectMapper) {
        this.releaseService = releaseService;
        this.objectMapper = objectMapper;
    }

    public String generateCycloneDxJson(Long releaseId) throws Exception {
        ProductRelease release = releaseService.findById(releaseId);

        ObjectNode bom = objectMapper.createObjectNode();
        bom.put("bomFormat", "CycloneDX");
        bom.put("specVersion", "1.5");
        bom.put("serialNumber", "urn:uuid:" + UUID.randomUUID());
        bom.put("version", 1);

        ObjectNode metadata = bom.putObject("metadata");
        metadata.put("timestamp", Instant.now().toString());

        ObjectNode metaComponent = metadata.putObject("component");
        metaComponent.put("type", "application");
        metaComponent.put("bom-ref", "release-" + release.getId());
        metaComponent.put("name", release.getName());
        metaComponent.put("version", release.getTag());
        if (release.getDescription() != null && !release.getDescription().isBlank()) {
            metaComponent.put("description", release.getDescription());
        }

        ArrayNode components = bom.putArray("components");

        for (ReleaseItem item : release.getItems()) {
            ComponentVersion cv = item.getComponentVersion();
            SoftwareComponent sc = cv.getComponent();

            ObjectNode comp = components.addObject();
            comp.put("type", "library");
            comp.put("bom-ref", "cv-" + cv.getId());
            comp.put("name", sc.getName());
            comp.put("version", cv.getVersion());

            ObjectNode supplier = comp.putObject("supplier");
            supplier.put("name", sc.getManufacturer());

            if (sc.getSourceUrl() != null && !sc.getSourceUrl().isBlank()) {
                ArrayNode refs = comp.putArray("externalReferences");
                ObjectNode ref = refs.addObject();
                ref.put("type", "website");
                ref.put("url", sc.getSourceUrl());
            }

            if (cv.getNotes() != null && !cv.getNotes().isBlank()) {
                comp.put("description", cv.getNotes());
            }
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bom);
    }
}
