package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.ClientPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientPurchaseRepository extends JpaRepository<ClientPurchase, Integer> {
}
