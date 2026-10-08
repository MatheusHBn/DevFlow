package com.Matheus.audit_service.mapper;

import com.Matheus.audit_service.domain.Audit;
import com.Matheus.audit_service.dto.AuditResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    AuditResponse toResponse(Audit audit);

    List<AuditResponse> toResponseList(List<Audit> audits);
}
