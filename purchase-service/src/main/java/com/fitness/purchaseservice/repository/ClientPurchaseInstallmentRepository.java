package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.ClientPurchaseInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientPurchaseInstallmentRepository extends JpaRepository<ClientPurchaseInstallment, Integer> {

    List<ClientPurchaseInstallment> findAllByClientPurchaseId(Integer id);
}
