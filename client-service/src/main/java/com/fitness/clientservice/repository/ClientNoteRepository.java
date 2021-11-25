package com.fitness.clientservice.repository;

import com.fitness.clientservice.model.ClientNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientNoteRepository extends JpaRepository<ClientNote, Integer> {
}
