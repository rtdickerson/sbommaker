package com.sbommaker.web;

import com.sbommaker.model.ProductRelease;
import com.sbommaker.service.BomService;
import com.sbommaker.service.ReleaseService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bom")
public class BomController {

    private final BomService bomService;
    private final ReleaseService releaseService;

    public BomController(BomService bomService, ReleaseService releaseService) {
        this.bomService = bomService;
        this.releaseService = releaseService;
    }

    @GetMapping("/{releaseId}")
    public ResponseEntity<String> downloadBom(@PathVariable Long releaseId) throws Exception {
        ProductRelease release = releaseService.findById(releaseId);
        String json = bomService.generateCycloneDxJson(releaseId);
        String filename = "bom-" + release.getTag().replaceAll("[^a-zA-Z0-9._-]", "_") + ".json";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }
}
