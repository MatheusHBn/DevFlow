package com.Matheus.audit_service.api;

import com.Matheus.audit_service.commons.FileUtils;
import com.Matheus.audit_service.dto.AuditResponse;
import com.Matheus.audit_service.exception.AuditNotFound;
import com.Matheus.audit_service.service.AuditService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(FileUtils.class)
class AuditControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuditService service;

    @Autowired
    private FileUtils fileUtils;

    @Test
    @Order(1)
    @DisplayName("Should return a list of all audits when successful")
    void findAll_ReturnsAllAudits_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceAsObject("audit/audit-request-200.json", AuditResponse.class);

        when(service.findAllAudits()).thenReturn(List.of(response));

        mockMvc.perform(get("/v1/audits").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].eventId")
                        .value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$[0].eventType").value("TASK_CREATED"))
                .andExpect(jsonPath("$[0].entityType").value("TASK"))
                .andExpect(jsonPath("$[0].entityId").value(42))
                .andExpect(jsonPath("$[0].description")
                        .value("Task \"Implement JWT authentication\" was created."))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-10-07T18:30:00"));

        verify(service).findAllAudits();
    }

    @Test
    @Order(2)
    @DisplayName("Should return audit by ID when it exists")
    void findById_ReturnsAudit_WhenSuccessful() throws Exception {
        var response = fileUtils.readResourceAsObject("audit/audit-request-200.json", AuditResponse.class);

        when(service.findAuditById(1L)).thenReturn(response);

        mockMvc.perform(get("/v1/audits/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.eventId")
                        .value("550e8400-e29b-41d4-a716-446655440000"))
                .andExpect(jsonPath("$.eventType").value("TASK_CREATED"))
                .andExpect(jsonPath("$.entityType").value("TASK"))
                .andExpect(jsonPath("$.entityId").value(42))
                .andExpect(jsonPath("$.description")
                        .value("Task \"Implement JWT authentication\" was created."))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-10-07T18:30:00"));

        verify(service).findAuditById(1L);
    }

    @Test
    @Order(3)
    @DisplayName("Should return an empty list when no audits exist")
    void findAll_ReturnsEmptyList_WhenNoAuditsExist() throws Exception {
        when(service.findAllAudits()).thenReturn(List.of());

        mockMvc.perform(get("/v1/audits").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(service).findAllAudits();
    }

    @Test
    @Order(4)
    @DisplayName("Should return 404 Not Found when audit does not exist")
    void findById_ReturnsNotFound_WhenAuditDoesNotExist() throws Exception {
        when(service.findAuditById(999L)).thenThrow(new AuditNotFound("Audit log not found"));

        mockMvc.perform(get("/v1/audits/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(service).findAuditById(999L);
    }
}