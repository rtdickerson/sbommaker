package com.sbommaker.api;

import com.sbommaker.service.ComponentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/components")
public class ComponentRestController {

    private final ComponentService componentService;

    public ComponentRestController(ComponentService componentService) {
        this.componentService = componentService;
    }

    @PutMapping
    public ResponseEntity<ComponentResponse> upsert(@Valid @RequestBody ComponentRequest request) {
        ComponentService.UpsertResult result = componentService.upsert(
                request.name(), request.manufacturer(), request.sourceUrl());
        ComponentResponse body = ComponentResponse.of(result.component(), result.created());
        if (result.created()) {
            URI location = URI.create("/api/components/" + result.component().getId());
            return ResponseEntity.created(location).body(body);
        }
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComponentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ComponentResponse.of(componentService.findById(id), false));
    }

    @PutMapping("/{name}/versions")
    public ResponseEntity<VersionResponse> upsertVersion(@PathVariable String name,
                                                         @Valid @RequestBody VersionRequest request) {
        ComponentService.VersionUpsertResult result =
                componentService.upsertVersion(name, request.version(), request.notes());
        VersionResponse body = VersionResponse.of(result.version(), result.created());
        if (result.created()) {
            URI location = URI.create("/api/components/" + result.version().getComponent().getId()
                    + "/versions/" + result.version().getId());
            return ResponseEntity.created(location).body(body);
        }
        return ResponseEntity.ok(body);
    }
}
