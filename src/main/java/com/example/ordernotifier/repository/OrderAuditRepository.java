package com.example.ordernotifier.repository;

import com.example.ordernotifier.entity.OrderAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderAuditRepository extends JpaRepository<OrderAudit, Long> {
}