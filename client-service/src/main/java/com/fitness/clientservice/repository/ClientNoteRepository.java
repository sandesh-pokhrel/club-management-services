package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.ClientNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface ClientNoteRepository extends JpaRepository<ClientNote, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE ClientNote cn SET cn.note = ?2 where cn.id = ?1")
    void updateNoteById(Integer id, String note);
}
