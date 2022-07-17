package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.ClientPurchaseInstallment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ClientPurchaseInstallmentRepository extends JpaRepository<ClientPurchaseInstallment, Integer> {

    List<ClientPurchaseInstallment> findAllByClientPurchaseId(Integer id);

    @Query(value =
            "select * from client_purchase_installment cpi " +
            "join client_purchase cp " +
            "on cpi.client_purchase_id = cp.id " +
            "join club_management_clients.client c " +
            "on cp.client_username = c.username " +
            "where (c.club_id = :clubId and cpi.expected_pay_date between :fromDate and :toDate) " +
            "and (cp.client_username like %:search% ) ", nativeQuery = true)
    Page<ClientPurchaseInstallment> search(Date fromDate, Date toDate, String search, Integer clubId, Pageable pageable);
}
