package com.Matheus.audit_service.repository;

import com.Matheus.audit_service.domain.Audit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<Audit, Long> {
}
