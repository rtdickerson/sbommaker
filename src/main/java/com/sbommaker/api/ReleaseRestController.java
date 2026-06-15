package com.sbommaker.api;

import com.sbommaker.service.BomService;
import com.sbommaker.service.ReleaseService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/releases")
public class ReleaseRestController {

    private final ReleaseService releaseService;
    private final BomService bomService;

    public ReleaseRestController(ReleaseService releaseService, BomService bomService) {
        this.releaseService = releaseService;
        this.bomService = bomService;
    }

    @PutMapping
    public ResponseEntity<ReleaseResponse> upsert(@Valid @RequestBody ReleaseRequest request) {
        ReleaseService.UpsertResult result =
                releaseService.upsert(request.name(), request.tag(), request.description());
        ReleaseResponse body = ReleaseResponse.of(result.release(), result.created());
        if (result.created()) {
            URI location = URI.create("/api/releases/" + result.release().getTag());
            return ResponseEntity.created(location).body(body);
        }
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{tag}")
    public ResponseEntity<ReleaseResponse> getByTag(@PathVariable String tag) {
        return ResponseEntity.ok(ReleaseResponse.of(releaseService.findByTag(tag), false));
    }

    @PutMapping("/{tag}/components")
    public ResponseEntity<ReleaseResponse> addComponent(@PathVariable String tag,
                                                        @Valid @RequestBody ReleaseComponentRequest request) {
        releaseService.addComponentVersionByName(tag, request.componentName(), request.version());
        return ResponseEntity.ok(ReleaseResponse.of(releaseService.findByTag(tag), false));
    }

    @GetMapping(value = "/{tag}/bom", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getBom(@PathVariable String tag) throws Exception {
        long releaseId = releaseService.findByTag(tag).getId();
        String json = bomService.generateCycloneDxJson(releaseId);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"bom-" + tag.replaceAll("[^a-zA-Z0-9._-]", "_") + ".json\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json);
    }
}
