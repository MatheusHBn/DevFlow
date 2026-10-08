package com.Matheus.audit_service.api;

import com.Matheus.audit_service.dto.AuditResponse;
import com.Matheus.audit_service.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/audits")
@RequiredArgsConstructor
@Tag(name = "Audits", description = "Endpoints for retrieving task audit logs")
public class AuditController {

    private final AuditService service;

    @Operation(summary = "Get all audit logs", description = "Returns all audit logs generated from task events.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit logs successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<AuditResponse>> findAllAudits() {
        return ResponseEntity.ok(service.findAllAudits());
    }

    @Operation(summary = "Get an audit log by ID", description = "Returns a specific audit log by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Audit log successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Audit log not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuditResponse> findAuditById(
            @Parameter(description = "Unique identifier of the audit log", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(service.findAuditById(id));
    }
}
