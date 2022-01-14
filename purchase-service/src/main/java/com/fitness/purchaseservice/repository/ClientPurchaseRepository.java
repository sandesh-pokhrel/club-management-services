package com.fitness.purchaseservice.repository;

import com.fitness.purchaseservice.model.ClientPurchase;
import com.fitness.purchaseservice.model.PurchaseSubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ClientPurchaseRepository extends JpaRepository<ClientPurchase, Integer> {

    boolean existsByClientUsernameAndPurchaseSubCategory(String username, PurchaseSubCategory purchaseSubCategory);

    List<ClientPurchase> findAllByClientUsername(String username);

    List<ClientPurchase> findAllByClientUsernameAndApptScheduledNot(String username, Integer apptNotEquals);

    ClientPurchase findByClientUsernameAndPurchaseSubCategoryAndApptScheduledNot(String username,
                                                                                 PurchaseSubCategory purchaseSubCategory,
                                                                                 Integer apptNotEquals);

    @Query("SELECT cp FROM ClientPurchase cp where lower(cp.clientUsername) like %:searchText%")
    Page<ClientPurchase> search(String searchText, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE ClientPurchase c set c.apptScheduled = -1 where c.clientUsername = ?1 and c.purchaseSubCategory = ?2")
    void updateApptScheduledToCompleted(String username, PurchaseSubCategory purchaseSubCategory);


}
